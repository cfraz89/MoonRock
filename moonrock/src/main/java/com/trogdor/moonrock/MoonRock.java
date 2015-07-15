package com.trogdor.moonrock;

import android.content.Context;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.trogdor.moonrock.ReversePusher.MRReversePusher;
import com.trogdor.moonrock.ReversePusher.MRSingleShotReversePusher;

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

    private WebView webView;
    private Context context;
    private MRStreamManager streamManager;
    private MRReversePortalManager reversePortalManager;

    private AsyncSubject<MoonRock> readySubject;

    private Map<String, MoonRockModule> loadedModules;

    private boolean needsLoad;
    private String pageUrl;
    private String baseUrl;

    public MoonRock(Context context)
    {
        this.context = context;
        baseUrl = DefaultBase;
        pageUrl = DefaultPage;
        streamManager = new MRStreamManager();
        reversePortalManager = new MRReversePortalManager();
        loadedModules = new HashMap<>();
        webView = new WebView(this.context);
        setupWebView();
        readySubject = AsyncSubject.create();
        needsLoad = true;
    }

    private void setupWebView() {
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setJavaScriptEnabled(true);
        MoonRock self = this;
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String config = String.format("System.config({baseURL:'%s'})", baseUrl);
                view.evaluateJavascript(config, null);
            }
        });
    }

    private void addDefaultExtensions() {
        webView.addJavascriptInterface(streamManager, "streamInterface");
        webView.addJavascriptInterface(reversePortalManager, "reversePortalInterface");
    }

    public Observable<MoonRock> ready() {
        return readySubject.observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MoonRockModule> loadModule(String module, String instance, Object portalHost) {
        if (needsLoad) {
            load();
        }
        AsyncSubject<MoonRockModule> readySubject = AsyncSubject.create();
        loadModule(module, instance, portalHost, readySubject);
        return readySubject.observeOn(AndroidSchedulers.mainThread());
    }

    public void registerExtensions(Map<Object, String> extensions) {
        if (extensions != null) {
            for (Map.Entry<Object, String> extension : extensions.entrySet())
                webView.addJavascriptInterface(extension.getKey(), extension.getValue());
        }
    }

    private void load() {
        addDefaultExtensions();
        MRReversePusher<?> pusher = new MRSingleShotReversePusher<>(null);
        streamManager.openStream("moonrock-configured", pusher).subscribe(b -> {
            readySubject.onNext(this);
            readySubject.onCompleted();
        });
        needsLoad = false;
        webView.loadUrl(baseUrl + "/" + pageUrl);
    }

    public void loadModule(String moduleName, String instanceName, Object portalHost, AsyncSubject<MoonRockModule> moduleReadySubject) {
        if (needsLoad) {
            load();
        }
        readySubject.observeOn(AndroidSchedulers.mainThread()).subscribe(moonRock -> {
            String instance = moduleNameForInstance(moduleName, instanceName);
            //If module already loaded, use that
            if (loadedModules.containsKey(instance)) {
                MoonRockModule module = loadedModules.get(instance);
                module.setPortalHost(portalHost);
                moduleReadySubject.onNext(loadedModules.get(instance));
                moduleReadySubject.onCompleted();
            } else {
                new MoonRockModule(this, moduleName, instance, portalHost, moduleReadySubject).ready().subscribe(module -> {
                    loadedModules.put(module.getLoadedName(), module);
                });
            }
        });
    }


    private String moduleNameForInstance(String module, String instanceName) {
        return String.format("instance_%s_%s", module.replace("/", "_"), instanceName);
    }

    public MoonRockModule getModule(String loadedName) {
        return loadedModules.get(loadedName);
    }

    public MRStreamManager getStreams() {
        return streamManager;
    }

    public MRReversePortalManager getReversePortals() { return reversePortalManager; }

    public WebView getWebView() {
        return webView;
    }

    public void runJS(String script, ValueCallback<String> callback) {
        webView.evaluateJavascript(script, callback);
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String mPageUrl) {
        this.pageUrl = mPageUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String mBaseUrl) {
        this.baseUrl = mBaseUrl;
    }

    public void destroy() {
        webView = null;
        context = null;
        for(Map.Entry<String, MoonRockModule> module : loadedModules.entrySet())
            module.getValue().unlinkPortals();
    }
}
