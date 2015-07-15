package com.trogdor.moonrock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trogdor.moonrock.annotations.Portal;
import com.trogdor.moonrock.annotations.ReversePortal;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.android.widget.OnTextChangeEvent;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

public class MRPortalGenerator {
    private final MoonRock mMoonRock;
    private Object mPortalHost;
    private final Gson mGson;

    private String mLoadedName;

    private List<Subscription> portalSubscriptions;

    public MRPortalGenerator(MoonRock moonRock, Object portalHost) {
        this.mMoonRock = moonRock;
        this.mPortalHost = portalHost;
        this.mGson = new Gson();
        this.portalSubscriptions = new ArrayList<>(20);
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
            MRReversePusher<?> pusher = new MRReversePusher<>(classForField(field));
            Observable<?> subject = Observable.create(new MRReversePushOnSubscribe<>(pusher));
            field.set(mPortalHost, subject.observeOn(AndroidSchedulers.mainThread()));
            String name = reversePortal.value().isEmpty() ? field.getName() : reversePortal.value();
            reversePortal(name, pusher);
        } catch (Exception e) {
        }

    }


    Class<?> classForField(Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    public void portal(Observable<?> observable, String name) {
        String createScript = String.format("mrhelper.portal('%s', '%s')", mLoadedName, name);
        mMoonRock.runJS(createScript, null);

        Subscription sub = observable.observeOn(AndroidSchedulers.mainThread()).subscribe(input -> {
            try {
                String serializedInput = input != null ? mGson.toJson(input) : "null";
                String mirrorScript = String.format("mrhelper.activatePortal('%s', '%s', '%s')", mLoadedName, name, serializedInput);
                mMoonRock.runJS(mirrorScript, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        portalSubscriptions.add(sub);
    }

    public <T> void reversePortal(String name, MRReversePusher pusher) {
        mMoonRock.getReversePortals().registerReverse(name, pusher);
        String reverseScript = String.format("mrhelper.reversePortal('%s', '%s')", mLoadedName, name);
        mMoonRock.runJS(reverseScript, null);
    }

    //Call after forwards portals have been setup
    public void portalsGenerated() {
        String finishedScript = String.format("mrhelper.portalsGenerated('%s')", mLoadedName);
        mMoonRock.runJS(finishedScript, null);
    }

    //Cal after reverse portals have been linked
    public void portalsLinked() {
        String finishedScript = String.format("mrhelper.portalsLinked('%s')", mLoadedName);
        mMoonRock.runJS(finishedScript, null);
    }

    public void unlinkPortals() {
        for(Subscription sub : portalSubscriptions)
            sub.unsubscribe();
    }

    public void setPortalHost(Object portalHost) {
        this.mPortalHost = portalHost;
    }
}