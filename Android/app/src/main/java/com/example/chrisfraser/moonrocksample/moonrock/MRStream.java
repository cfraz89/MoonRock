package com.example.chrisfraser.moonrocksample.moonrock;

import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;

import rx.Observable;
import rx.subjects.Subject;

/**
 * Created by chrisfraser on 7/07/15.
 */
public class MRStream<T> {
    private Subject<T, T> mStreamSubject;
    private Class<T> mUnpackClass;
    public MRStream(Subject<T, T> streamSubject, Class<T> unpackClass)
    {
        mStreamSubject = streamSubject;
        mUnpackClass = unpackClass;
    }

    public void push(String data) {
        if (mUnpackClass == String.class)
            mStreamSubject.onNext((T)deString(data));
        else if(mUnpackClass == Integer.class)
            mStreamSubject.onNext((T)new Integer(data));
        else {
            try {
                T value = LoganSquare.parse(data, mUnpackClass);
                if (value != null)
                    mStreamSubject.onNext(value);
                else {
                    String error = "Couldn't parse response: " + data;
                    Log.d("moonRock", error);
                    mStreamSubject.onError(new Exception(error));
                }
            } catch (IOException e) {
                e.printStackTrace();
                mStreamSubject.onError(e);
            }
        }
    }

    String deString(String data) {
        return data.substring(1, data.length() - 1);
    }

    public Observable<T> getObservable() {
        return mStreamSubject.asObservable();
    }
}
