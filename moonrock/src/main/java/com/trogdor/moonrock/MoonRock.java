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
    static final String DefaultBase = "file:///android_asset";
    static final String DefaultPage = "moonrock.html";

    private WebView mWebView;
    private Context mContext;
    private MRStreamManager mStreamManager;
    private MRReversePortalManager mMRReversePortalManager;

    private AsyncSubject<MoonRock> mReadySubject;

    private Map<String, MoonRockModule> mLoadedModules;

    private boolean mNeedsLoad;
    private String mPageUrl;
    private String mBaseUrl;

    public MoonRock(Context context)
    {
        mContext = context;
        mBaseUrl = DefaultBase;
        mPageUrl = DefaultPage;
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
                String config = String.format("System.config({baseURL:'%s'})", mBaseUrl);
                view.evaluateJavascript(config, null);
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

    public Observable<MoonRockModule> loadModule(String module, String instance, Object portalHost) {
        if (mNeedsLoad) {
            load();
        }
        AsyncSubject<MoonRockModule> readySubject = AsyncSubject.create();
        loadModule(module, instance, portalHost, readySubject);
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
        mStreamManager.makeStream("moonrock-configured", Boolean.class).getObservable().subscribe(b -> {
            mReadySubject.onNext(this);
            mReadySubject.onCompleted();
        });
        mNeedsLoad = false;
        mWebView.loadUrl(mBaseUrl + "/" + mPageUrl);
    }

    public void loadModule(String moduleName, String instanceName, Object portalHost, AsyncSubject<MoonRockModule> moduleReadySubject) {
        if (mNeedsLoad) {
            load();
        }
        mReadySubject.observeOn(AndroidSchedulers.mainThread()).subscribe(moonRock -> {
            String instance = moduleNameForInstance(moduleName, instanceName);
            //If module already loaded, use that
            if (mLoadedModules.containsKey(instance)) {
                MoonRockModule module = mLoadedModules.get(instance);
                module.setPortalHost(portalHost);
                moduleReadySubject.onNext(mLoadedModules.get(instance));
                moduleReadySubject.onCompleted();
            }
            else {
                new MoonRockModule(this, moduleName, instance, portalHost, moduleReadySubject).ready().subscribe(module -> {
                    mLoadedModules.put(module.getLoadedName(), module);
                });
            }
        });
    }


    private String moduleNameForInstance(String module, String instanceName) {
        return String.format("instance_%s_%s", module.replace("/", "_"), instanceName);
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

    public String getPageUrl() {
        return mPageUrl;
    }

    public void setPageUrl(String mPageUrl) {
        this.mPageUrl = mPageUrl;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

    public void setBaseUrl(String mBaseUrl) {
        this.mBaseUrl = mBaseUrl;
    }

    public void destroy() {
        mWebView = null;
        mContext = null;
        for(Map.Entry<String, MoonRockModule> module : mLoadedModules.entrySet())
            module.getValue().unlinkPortals();
    }
}
