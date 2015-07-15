package com.trogdor.moonrock.ReversePusher;

import rx.Subscriber;

/**
 * Created by chrisfraser on 15/07/15.
 */
public class MRSingleShotReversePusher<T> extends MRReversePusher<T> {
    public MRSingleShotReversePusher(Class<T> unpackClass) {
        super(unpackClass);
    }

    @Override
    public void push(T data) {
        for(Subscriber<T> s : this.subscribers) {
            s.onNext(data);
            s.onCompleted();
        }

    }
}
