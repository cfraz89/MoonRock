package com.trogdor.moonrock;

import android.webkit.JavascriptInterface;

import com.trogdor.moonrock.ReversePusher.MRReversePusher;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisfraser on 11/07/15.
 */
public class MRReversePortalManager {
    Map<String, MRReversePusher> reverseMap;

    public MRReversePortalManager() {
        reverseMap = new HashMap<>(100);
    }

    @JavascriptInterface
    public void onNext(String data, String name) {
        MRReversePusher pusher = reverseMap.get(name);
        pusher.pushJson(data);
    }

    public <T> void registerReverse(String name, MRReversePusher<T> pusher) {
        reverseMap.put(name, pusher);
    }
}
