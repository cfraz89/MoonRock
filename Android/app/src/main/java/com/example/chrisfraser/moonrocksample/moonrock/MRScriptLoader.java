package com.example.chrisfraser.moonrocksample.moonrock;

import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by chrisfraser on 7/07/15.
 */
public class MRScriptLoader {
    WebView mWebView;

    public MRScriptLoader(WebView webView) {
        mWebView = webView;
    }

    public void loadScript(String fileName) {
        String script = loadAsset(fileName);
        if (script != null) {
            mWebView.evaluateJavascript(script, null);
        }
    }

    private String loadAsset(String fileName) {
        String script = null;
        BufferedReader reader = null;
        try {
            InputStream stream = mWebView.getContext().getAssets().open(fileName);
            reader = new BufferedReader(new InputStreamReader(stream));
            byte[] buffer = new byte[stream.available()];
            stream.read(buffer);
            script = new String(buffer);
        } catch (IOException e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return script;
    }
}
