package com.example.chrisfraser.moonrocksample.moonrock;

import com.example.chrisfraser.moonrocksample.moonrock.notannotations.Portal;
import com.example.chrisfraser.moonrocksample.moonrock.notannotations.ReversePortal;
import com.example.chrisfraser.moonrocksample.moonrock.serializers.OnClickSerializer;
import com.example.chrisfraser.moonrocksample.moonrock.serializers.OnTextChangedSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.android.widget.OnTextChangeEvent;
import rx.subjects.PublishSubject;

public class MRPortalGenerator {
    private final MoonRock mMoonRock;
    private final Object mPortalHost;
    private final Gson mGson;

    private String mLoadedName;

    public MRPortalGenerator(MoonRock moonRock, Object portalHost) {
        this.mMoonRock = moonRock;
        this.mPortalHost = portalHost;
        this.mGson = new GsonBuilder()
                .registerTypeAdapter(OnClickEvent.class, new OnClickSerializer())
                .registerTypeAdapter(OnTextChangeEvent.class, new OnTextChangedSerializer())
                .create();
    }

    public void setLoadedName(String loadedName) {
        mLoadedName = loadedName;
    }

    public <T> void generatePortals() {
        Field[] fields = mPortalHost.getClass().getDeclaredFields();
        for (Field field : fields) {
            Portal portal = field.getAnnotation(Portal.class);
            if (portal != null) {
                portalFromAnnotation(field, portal);
            }
        }
        this.portalsGenerated();
        
        for (Field field : fields) {
            ReversePortal reversePortal = field.getAnnotation(ReversePortal.class);
            if (reversePortal != null) {
                reversePortalFromAnnotation(field, reversePortal);
            }
        }
        this.portalsLinked();
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

        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(input -> {
            try {
                String serializedInput = input != null ? mGson.toJson(input) : "null";
                String mirrorScript = String.format("mrHelper.activatePortal('%s', '%s', '%s')", mLoadedName, name, serializedInput);
                mMoonRock.runJS(mirrorScript, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public <T> void reversePortal(PublishSubject<T> mReverseSubject, String name, Class<T> unpackClass) {
        mMoonRock.getReversePortals().registerReverse(mReverseSubject, name, unpackClass);
        String reverseScript = String.format("mrHelper.reversePortal('%s', '%s')", mLoadedName, name);
        mMoonRock.runJS(reverseScript, null);
    }

    //Call after forwards portals have been setup
    public void portalsGenerated() {
        String finishedScript = String.format("mrHelper.portalsGenerated('%s')", mLoadedName);
        mMoonRock.runJS(finishedScript, null);
    }

    //Cal after reverse portals have been linked
    public void portalsLinked() {
        String finishedScript = String.format("mrHelper.portalsLinked('%s')", mLoadedName);
        mMoonRock.runJS(finishedScript, null);
    }


}