package com.example.chrisfraser.moonrocksample.moonrock.Annotations;

import com.bluelinelabs.logansquare.LoganSquare;
import com.example.chrisfraser.moonrocksample.moonrock.MoonRock;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

public class PortalGenerator {
    private final MoonRock mMoonRock;
    private final Object mPortalHost;

    private String mLoadedName;

    public PortalGenerator(MoonRock moonRock, Object portalHost) {
        this.mMoonRock = moonRock;
        this.mPortalHost = portalHost;
    }

    public void setLoadedName(String loadedName) {
        mLoadedName = loadedName;
    }

    public <T> void generatePortals() {
        for (Field field : mPortalHost.getClass().getDeclaredFields()) {
            Portal portal = field.getAnnotation(Portal.class);
            if (portal != null) {
                try {
                    field.setAccessible(true);
                    PublishSubject subject = PublishSubject.create();
                    String name = portal.value().isEmpty() ? field.getName() : portal.value();
                    if (portal.direction() == Direction.Auto && field.getType() == Observer.class
                            || portal.direction() == Direction.Forward) {
                        field.set(mPortalHost, subject);
                        portal(subject, name);
                    } else if (portal.direction() == Direction.Auto && field.getType() == Observable.class
                            | portal.direction() == Direction.Reverse) {
                        field.set(mPortalHost, subject.observeOn(AndroidSchedulers.mainThread()));
                        reversePortal(subject, name, classForField(field));
                    }
                } catch (Exception e) {
                }
            }
        }
        this.portalsGenerated();
    }

    Class<?> classForField(Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    public <T> void portal(PublishSubject<T> subject, String name) {
        String createScript = String.format("mrHelper.portal('%s', '%s')", mLoadedName, name);
        mMoonRock.runJS(createScript, null);

        subject.subscribe(input -> {
            try {
                String serializedInput = input != null ? LoganSquare.serialize(input) : "null";
                String mirrorScript = String.format("mrHelper.activatePortal('%s', '%s', '%s')", mLoadedName, name, serializedInput);
                mMoonRock.runJS(mirrorScript, null);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    public <T> void reversePortal(PublishSubject<T> mReverseSubject, String name, Class<T> unpackClass) {
        mMoonRock.getReversePortals().registerReverse(mReverseSubject, name, unpackClass);
        String reverseScript = String.format("mrHelper.reversePortal('%s', '%s')", mLoadedName, name);
        mMoonRock.runJS(reverseScript, null);
    }

    public void portalsGenerated() {
        String finishedScript = String.format("mrHelper.portalsGenerated('%s')", mLoadedName);
        mMoonRock.runJS(finishedScript, null);
    }
}