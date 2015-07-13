package com.example.chrisfraser.moonrocksample.moonrock;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.util.UUID;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.AsyncSubject;

/**
 * Created by chrisfraser on 8/07/15.
 */
public class MoonRockModule {
    private MRPortalGenerator mPortalGenerator;
    private AsyncSubject<MoonRockModule> mReadySubject;
    private MoonRock mMoonRock;
    private Object mPortalHost;
    String mLoadedName;

    public MoonRockModule(MoonRock moonRock, String module, Object portalHost, AsyncSubject<MoonRockModule> readySubject) {
        mMoonRock = moonRock;
        mReadySubject = readySubject;
        mPortalGenerator = new MRPortalGenerator(moonRock, portalHost);
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

        MRStream<T> resultStream = streamManager.makeStream(streamKey, unpackClass);
        mMoonRock.getWebView().evaluateJavascript(script, result -> {
            if (result != "null")
                resultStream.push(result);
        });
        return resultStream.getObservable().observeOn(AndroidSchedulers.mainThread());
    }

    private String makeFunctionInvocation(String functionName, String streamKey, Object[] args) throws IOException {
        StringBuilder script = new StringBuilder(String.format("%s.%s('%s'", mLoadedName, functionName, streamKey));
        for (Object arg : args)
            script.append(String.format(", '%s'", LoganSquare.serialize(arg)));
        script.append(")");
        return script.toString();
    }

    public Observable<MoonRockModule> ready() {
        return mReadySubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    private String nameForInstance(String module) {
        return String.format("instance_%s_%s", module.replace("/", "_"), UUID.randomUUID().toString().replace("-", "_"));
    }

    private void load(String module) {
        mLoadedName = nameForInstance(module);
        mPortalGenerator.setLoadedName(mLoadedName);
        String loadScript = String.format("mrHelper.loadModule('%s', '%s')", module, mLoadedName);

        MRStream<String> loadedStream = mMoonRock.getStreams().makeStream(mLoadedName, String.class);
        loadedStream.getObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(r -> {
            mPortalGenerator.generatePortals();
            mReadySubject.onNext(this);
            mReadySubject.onCompleted();
        });

        mMoonRock.runJS(loadScript, null);
    }

    public <T> void subscribeOnActivity(Observable<T> observable) {

    }

    public String getLoadedName() {
        return mLoadedName;
    }

    public MRPortalGenerator getPortalGenerator() {
        return mPortalGenerator;
    }
}
