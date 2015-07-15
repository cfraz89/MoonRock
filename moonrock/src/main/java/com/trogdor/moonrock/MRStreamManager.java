package com.trogdor.moonrock;

import android.webkit.JavascriptInterface;

import com.trogdor.moonrock.ReversePusher.MRReversePushOnSubscribe;
import com.trogdor.moonrock.ReversePusher.MRReversePusher;

import java.util.HashMap;
import java.util.UUID;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by chrisfraser on 7/07/15.
 */

//Transient observables for one time function to return to java
public class MRStreamManager {
    public HashMap<String, MRReversePusher> pushers;

    public MRStreamManager() {
        pushers = new HashMap<>(100);
    }

    //For data to come back from single shots
    @JavascriptInterface
    public void push(String data, String streamName) {
        pushers.get(streamName).push(data);
    }

    public String makeKey() {
        return UUID.randomUUID().toString();
    }

    <T> Observable<T> openStream(String key, MRReversePusher<T> pusher)
    {
        MRReversePushOnSubscribe<T> onSubscribe = new MRReversePushOnSubscribe<>(pusher);
        Observable<T> observable = Observable.create(onSubscribe);
        pushers.put(key, pusher);
        return observable.observeOn(AndroidSchedulers.mainThread());
    }
}
