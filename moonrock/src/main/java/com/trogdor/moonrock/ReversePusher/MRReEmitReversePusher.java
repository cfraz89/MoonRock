package com.trogdor.moonrock.ReversePusher;

import android.util.Log;

import rx.Subscriber;

/**
 * Created by chrisfraser on 15/07/15.
 */
public class MRReEmitReversePusher<T> extends MRReversePusher<T>{
    String lastData;

    public MRReEmitReversePusher(Class<T> unpackClass) {
        super(unpackClass);
    }

    @Override
    public void pushJson(String json) {
        super.pushJson(json);
        lastData = json;
    }

    @Override
    public void addSubscriber(Subscriber subscriber) {
        super.addSubscriber(subscriber);
        if (lastData == null)
            subscriber.onNext(lastData);
        else {
            try {
                T data = gson.fromJson(lastData, unpackClass);
                if (data != null)
                    subscriber.onNext(data);
                else {
                    String error = "Couldn't parse response: " + lastData;
                    Log.d("moonRock", error);
                    subscriber.onError(new Exception(error));
                }
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        }
    }

    public String getLastData() {
        return lastData;
    }

    public void setLastData(String lastData) {
        this.lastData = lastData;
    }
}
