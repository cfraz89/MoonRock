package com.trogdor.moonrock;

import android.util.Log;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.subjects.Subject;

/**
 * Created by chrisfraser on 7/07/15.
 */
public class MRStream<T> {
    private MRReversePusher mPusher;
    private Class<T> mUnpackClass;
    Gson mGson;
    public MRStream(MRReversePusher pusher, Class<T> unpackClass)
    {
        mPusher = pusher;
        mUnpackClass = unpackClass;
        mGson = new Gson();
    }

    public void push(String data) {
            try {
                T value = mGson.fromJson(data, mUnpackClass);
                if (value != null)
                    mPusher.push(data);
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
