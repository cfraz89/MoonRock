package com.trogdor.moonrock;

import android.os.Bundle;

import com.google.gson.Gson;
import com.trogdor.moonrock.ReversePusher.MRReversePusher;

import java.io.IOException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.AsyncSubject;

/**
 * Created by chrisfraser on 8/07/15.
 */
public class MoonRockModule {
    private final MRPortalGenerator mPortalGenerator;
    private final AsyncSubject<MoonRockModule> mReadySubject;
    private final MoonRock mMoonRock;
    private Object mPortalHost;
    private final String mLoadedName;
    private final Gson mGson;

    public MoonRockModule(MoonRock moonRock, String module, String instanceName, Object portalHost, AsyncSubject<MoonRockModule> readySubject) {
        mMoonRock = moonRock;
        mReadySubject = readySubject;
        mPortalGenerator = new MRPortalGenerator(moonRock, portalHost);
        mGson = new Gson();
        mLoadedName = instanceName;
        load(module);
    }

    public MoonRock getMoonRock() {
        return mMoonRock;
    }

    public <T> Observable<T> function(String functionName, Class<T> unpackClass, Object... args)
    {
        MRStreamManager streamManager = mMoonRock.getStreams();
        String streamKey = streamManager.makeKey();
        String script = null;
        try {
            script = makeFunctionInvocation(functionName, streamKey, args);
        } catch (Exception e) {
            return Observable.error(e);
        }

        MRReversePusher<T> pusher = new MRReversePusher<>(unpackClass);
        Observable<T> resultObservable = streamManager.openStream(streamKey, pusher);
        mMoonRock.runJS(script, result -> {
            if (result != "null")
                pusher.pushJson(result);
        });
        return resultObservable;
    }

    private String makeFunctionInvocation(String functionName, String streamKey, Object[] args) throws IOException {
        StringBuilder script = new StringBuilder(String.format("%s.%s('%s'", mLoadedName, functionName, streamKey));
        for (Object arg : args)
            script.append(String.format(", '%s'", mGson.toJson(arg)));
        script.append(")");
        return script.toString();
    }

    public Observable<MoonRockModule> ready() {
        return mReadySubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    private void load(String module) {
        mPortalGenerator.setLoadedName(mLoadedName);
        String loadScript = String.format("mrhelper.loadModule('%s', '%s')", module, mLoadedName);

        MRReversePusher<String> pusher = new MRReversePusher<>(String.class);
        Observable<String> loaded = mMoonRock.getStreams().openStream(mLoadedName, pusher);
        loaded.subscribe(r -> {
            mReadySubject.onNext(this);
            mReadySubject.onCompleted();
        });

        mMoonRock.runJS(loadScript, null);
    }

    public String getLoadedName() {
        return mLoadedName;
    }

    public MRPortalGenerator getPortalGenerator() {
        return mPortalGenerator;
    }

    public void generatePortals() {
        mPortalGenerator.generatePortals();
    }

    public void unlinkPortals() {
        mPortalGenerator.destroyPortals();
        setPortalHost(null);
    }

    public void setPortalHost(Object portalHost) {
        this.mPortalHost = portalHost;
        mPortalGenerator.setPortalHost(portalHost);
    }

    public void destroy() {
        String unloadScript = String.format("mrhelper.unloadModule('%s')", mLoadedName);
        mMoonRock.runJS(unloadScript, null);
    }

    public void saveInstanceState(Bundle state) {
        mPortalGenerator.saveInstanceState(state);
    }

    public void restoreInstanceState(Bundle state) {
        if (state != null)
            mPortalGenerator.restoreInstanceState(state);
    }
}
