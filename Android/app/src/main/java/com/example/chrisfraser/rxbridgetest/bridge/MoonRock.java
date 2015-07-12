package com.example.chrisfraser.rxbridgetest.bridge;

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

    private Map<String, MRModule> mLoadedModules;

    public static Observable<MRModule> createWithModule(Context context, Map<Object, String> extensions, String moduleName, Object portalHost) {
        AsyncSubject<MRModule> readySubject = AsyncSubject.create();
        MoonRock moonRock = new MoonRock(context, extensions);
        moonRock.ready().subscribe(mr-> {
            mr.loadModule(moduleName, readySubject);
        });
        if (portalHost != null)
            readySubject.observeOn(AndroidSchedulers.mainThread()).subscribe(module -> module.getPortalGenerator().generatePortals(portalHost));
        return readySubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public MoonRock(Context context, Map<Object, String> extensions)
    {
        mContext = context;
        mStreamManager = new MRStreamManager();
        mMRReversePortalManager = new MRReversePortalManager();
        mLoadedModules = new HashMap<>();
        mWebView = new WebView(mContext);
        setupWebView();
        addExtensions(extensions);
        mReadySubject = AsyncSubject.create();

        mWebView.loadUrl("file:///android_asset/moonrock.html");

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

    private void addExtensions(Map<Object, String> extensions) {
        mWebView.addJavascriptInterface(mStreamManager, "streamInterface");
        mWebView.addJavascriptInterface(mMRReversePortalManager, "reversePortalInterface");
        if (extensions != null) {
            for (Map.Entry<Object, String> extension : extensions.entrySet())
                mWebView.addJavascriptInterface(extension.getKey(), extension.getValue());
        }
    }

    public Observable<MoonRock> ready() {
        return mReadySubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MRModule> loadModule(String module) {
        AsyncSubject<MRModule> readySubject = AsyncSubject.create();
        loadModule(module, readySubject);
        return readySubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public void loadModule(String moduleName, AsyncSubject<MRModule> readySubject) {
        readySubject.subscribe(module -> mLoadedModules.put(module.getLoadedName(), module));
        MRModule mrModule = new MRModule(this, moduleName, readySubject);
    }

    public MRModule getModule(String loadedName) {
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
