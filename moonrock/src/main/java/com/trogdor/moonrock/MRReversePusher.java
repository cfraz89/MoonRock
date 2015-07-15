package com.trogdor.moonrock;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by chrisfraser on 15/07/15.
 */
public class MRReversePusher<T> {
    protected List<Subscriber<T>> subscribers;
    protected Gson gson;
    protected Class<T> unpackClass;

    public MRReversePusher(Class<T> unpackClass) {
        subscribers = new ArrayList<>(10);
        this.unpackClass = unpackClass;
        gson = new Gson();
    }

    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void removeSubscriber(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void pushJson(String json) {
        try {
            T data = gson.fromJson(json, unpackClass);
            if (data != null)
                push(data);
            else {
                String error = "Couldn't parse response: " + data;
                Log.d("moonRock", error);
                error(new Exception(error));
            }
        } catch (Exception e) {
            e.printStackTrace();
            error(e);
        }
    }

    public void push(T data) {
        for(Subscriber<T> s : subscribers)
            s.onNext(data);
    }

    public void error(Throwable error) {
        for(Subscriber<T> s : subscribers)
            s.onError(error);
    }

    public void complete() {
        for(Subscriber<T> s : subscribers) {
            s.onCompleted();
        }
    }

    public void unsubscribeAll() {
        for(Subscriber<T> s : subscribers) {
            s.unsubscribe();
        }
    }
}
