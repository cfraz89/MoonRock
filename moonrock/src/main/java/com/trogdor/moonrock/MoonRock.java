package com.trogdor.moonrock;

import android.content.Context;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.AsyncSubject;

/**
 * Created by chrisfraser on 6/07/15.
 */

// The starter class. Use it to load modules which do the work
public class MoonRock {
    public static final String MainStream = "main";

    private WebView mWebView;
    private Context mContext;
    private MRStreamManager mStreamManager;
    private MRReversePortalManager mMRReversePortalManager;

    private AsyncSubject<MoonRock> mReadySubject;

    private Map<String, MoonRockModule> mLoadedModules;

    private boolean mNeedsLoad;

    public static Observable<MoonRockModule> createWithModule(Context context, String moduleName, Object portalHost) {
        Observable<MoonRockModule> moduleReady = new MoonRock(context).loadModule(moduleName, portalHost);
        return moduleReady;
    }

    public MoonRock(Context context)
    {
        mContext = context;
        mStreamManager = new MRStreamManager();
        mMRReversePortalManager = new MRReversePortalManager();
        mLoadedModules = new HashMap<>();
        mWebView = new WebView(mContext);
        setupWebView();
        mReadySubject = AsyncSubject.create();
        mNeedsLoad = true;
    }

    private void setupWebView() {
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        MoonRock self = this;
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mReadySubject.onNext(self);
                mReadySubject.onCompleted();
            }
        });
    }

    private void addDefaultExtensions() {
        mWebView.addJavascriptInterface(mStreamManager, "streamInterface");
        mWebView.addJavascriptInterface(mMRReversePortalManager, "reversePortalInterface");

    }

    public Observable<MoonRock> ready() {
        return mReadySubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MoonRockModule> loadModule(String module, Object portalHost) {
        if (mNeedsLoad) {
            load();
        }
        AsyncSubject<MoonRockModule> readySubject = AsyncSubject.create();
        loadModule(module, portalHost, readySubject);
        return readySubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public void registerExtensions(Map<Object, String> extensions) {
        if (extensions != null) {
            for (Map.Entry<Object, String> extension : extensions.entrySet())
                mWebView.addJavascriptInterface(extension.getKey(), extension.getValue());
        }
    }

    private void load() {
        addDefaultExtensions();
        mNeedsLoad = false;
        mWebView.loadUrl("file:///android_asset/moonrock.html");
    }

    public void loadModule(String moduleName, Object portalHost, AsyncSubject<MoonRockModule> moduleReadySubject) {
        if (mNeedsLoad) {
            load();
        }
        mReadySubject.subscribe(moonRock ->{
            MoonRockModule module = new MoonRockModule(this, moduleName, portalHost, moduleReadySubject);
            mLoadedModules.put(module.getLoadedName(), module);
        });
    }

    public MoonRockModule getModule(String loadedName) {
        return mLoadedModules.get(loadedName);
    }

    public MRStreamManager getStreams() {
        return mStreamManager;
    }

    public MRReversePortalManager getReversePortals() { return mMRReversePortalManager; }

    public WebView getWebView() {
        return mWebView;
    }

    public void runJS(String script, ValueCallback<String> callback) {
        mWebView.evaluateJavascript(script, callback);
    }
}
