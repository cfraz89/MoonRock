package com.trogdor.moonrock;

import android.webkit.JavascriptInterface;

import java.util.HashMap;
import java.util.UUID;

import rx.subjects.PublishSubject;

/**
 * Created by chrisfraser on 7/07/15.
 */

//Transient observables for one time function to return to java
public class MRStreamManager {
    public HashMap<String, MRStream> mStreams;

    public MRStreamManager() {
        mStreams = new HashMap<>(100);
    }

    //For data to come back from single shots
    @JavascriptInterface
    public void push(String data, String streamName) {
        mStreams.get(streamName).push(data);
    }

    public String makeKey() {
        return UUID.randomUUID().toString();
    }

    <T> MRStream<T> makeSingleShotStream(String key, Class<T> unpackClass)
    {
        PublishSubject<T> subject = PublishSubject.create();
        MRStream<T> stream = new MRStream<>(subject, unpackClass);
        mStreams.put(key, stream);
        //Don't need to track it once its done a push
        stream.getObservable().subscribe(o -> {
            mStreams.remove(key);
        });
        return stream;
    }
}
