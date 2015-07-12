package com.example.chrisfraser.moonrocksample.moonrock;

import android.webkit.JavascriptInterface;

import java.util.HashMap;
import java.util.Map;

import rx.subjects.PublishSubject;

/**
 * Created by chrisfraser on 11/07/15.
 */
public class MRReversePortalManager {
    Map<String, MRStream> mReverseMap;

    public MRReversePortalManager() {
        mReverseMap = new HashMap<>(100);
    }

    @JavascriptInterface
    public void onNext(String data, String name) {
        MRStream subject = mReverseMap.get(name);
        subject.push(data);
    }

    public <T> void registerReverse(PublishSubject mReverseSubject, String name, Class<T> unpackClass) {
        MRStream<T> stream = new MRStream<>(mReverseSubject, unpackClass);
        mReverseMap.put(name, stream);
    }
}
