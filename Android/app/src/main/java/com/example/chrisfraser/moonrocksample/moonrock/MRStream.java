package com.example.chrisfraser.moonrocksample.moonrock;

import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.google.gson.Gson;

import java.io.IOException;

import rx.Observable;
import rx.subjects.Subject;

/**
 * Created by chrisfraser on 7/07/15.
 */
public class MRStream<T> {
    private Subject<T, T> mStreamSubject;
    private Class<T> mUnpackClass;
    Gson mGson;
    public MRStream(Subject<T, T> streamSubject, Class<T> unpackClass)
    {
        mStreamSubject = streamSubject;
        mUnpackClass = unpackClass;
        mGson = new Gson();
    }

    public void push(String data) {
            try {
                T value = mGson.fromJson(data, mUnpackClass);
                if (value != null)
                    mStreamSubject.onNext(value);
                else {
                    String error = "Couldn't parse response: " + data;
                    Log.d("moonRock", error);
                    mStreamSubject.onError(new Exception(error));
                }
            } catch (Exception e) {
                e.printStackTrace();
                mStreamSubject.onError(e);
            }
    }

    public Observable<T> getObservable() {
        return mStreamSubject.asObservable();
    }
}
