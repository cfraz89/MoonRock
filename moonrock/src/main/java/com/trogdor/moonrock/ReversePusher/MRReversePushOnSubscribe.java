package com.trogdor.moonrock.ReversePusher;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by chrisfraser on 15/07/15.
 */
public class MRReversePushOnSubscribe<T> implements Observable.OnSubscribe<T> {
    MRReversePusher<T> pusher;

    public MRReversePushOnSubscribe(MRReversePusher<T> pusher) {
        this.pusher = pusher;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        pusher.addSubscriber(subscriber);
        subscriber.add(new Subscription() {
            @Override
            public void unsubscribe() {
                pusher.removeSubscriber(subscriber);
            }

            @Override
            public boolean isUnsubscribed() {
                return false;
            }
        });
    }

    public MRReversePusher<T> getPusher() {
        return pusher;
    }
}
