package com.trogdor.moonrock.ReversePusher;

import rx.Subscriber;

/**
 * Created by chrisfraser on 15/07/15.
 */
public class MRReEmitReversePusher<T> extends MRReversePusher<T>{
    T lastData;

    public MRReEmitReversePusher(Class<T> unpackClass) {
        super(unpackClass);
    }

    @Override
    public void addSubscriber(Subscriber subscriber) {
        super.addSubscriber(subscriber);
        if (lastData != null)
            subscriber.onNext(lastData);
    }

    @Override
    public void push(T data) {
        super.push(data);
        lastData = data;
    }
}
