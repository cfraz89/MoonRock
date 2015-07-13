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
                portalFromAnnotation(field, portal);
            }
            ReversePortal reversePortal = field.getAnnotation(ReversePortal.class);
            if (reversePortal != null) {
                reversePortalFromAnnotation(field, reversePortal);
            }
        }
        this.portalsGenerated();
    }

    private void portalFromAnnotation(Field field, Portal portal) {
        try {
            field.setAccessible(true);

            Observable<?> observable = (Observable<?>) field.get(mPortalHost);
            String name = portal.value().isEmpty() ? field.getName() : portal.value();
            portal(observable, name);
        } catch (Exception e) {
        }
    }

    private void reversePortalFromAnnotation(Field field, ReversePortal reversePortal) {
        try {
            field.setAccessible(true);
            PublishSubject subject = PublishSubject.create();
            field.set(mPortalHost, subject.observeOn(AndroidSchedulers.mainThread()));
            String name = reversePortal.value().isEmpty() ? field.getName() : reversePortal.value();
            reversePortal(subject, name, classForField(field));
        } catch (Exception e) {
        }

    }


    Class<?> classForField(Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    public void portal(Observable<?> observable, String name) {
        String createScript = String.format("mrHelper.portal('%s', '%s')", mLoadedName, name);
        mMoonRock.runJS(createScript, null);

        observable.subscribe(input -> {
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