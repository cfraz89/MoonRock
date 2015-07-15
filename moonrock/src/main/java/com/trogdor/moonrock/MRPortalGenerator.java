package com.trogdor.moonrock;

import com.google.gson.Gson;
import com.trogdor.moonrock.ReversePusher.MRReEmitReversePusher;
import com.trogdor.moonrock.ReversePusher.MRReversePushOnSubscribe;
import com.trogdor.moonrock.ReversePusher.MRReversePusher;
import com.trogdor.moonrock.annotations.Portal;
import com.trogdor.moonrock.annotations.ReversePortal;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MRPortalGenerator {
    private final MoonRock moonRock;
    private Object portalHost;
    private final Gson gson;
    private boolean portalsGeneratedBehind;

    private String loadedName;

    private List<Subscription> portalSubscriptions;
    private Map<String, MRReversePusher<?>> pushers;

    public MRPortalGenerator(MoonRock moonRock, Object portalHost) {
        this.moonRock = moonRock;
        this.portalHost = portalHost;
        this.gson = new Gson();
        this.portalSubscriptions = new ArrayList<>(20);
        this.pushers = new HashMap<>(20);
    }

    public void setLoadedName(String loadedName) {
        this.loadedName = loadedName;
    }

    public <T> void generatePortals() {
        Field[] fields = portalHost.getClass().getDeclaredFields();
        for (Field field : fields) {
            Portal portal = field.getAnnotation(Portal.class);
            if (portal != null) {
                portalFromAnnotation(field, portal);
            }
        }
        if (!portalsGeneratedBehind)
            this.portalsGenerated();

        for (Field field : fields) {
            ReversePortal reversePortal = field.getAnnotation(ReversePortal.class);
            if (reversePortal != null) {
                reversePortalFromAnnotation(field, reversePortal);
            }
        }
        this.portalsLinked();

        portalsGeneratedBehind = true;
    }

    private void portalFromAnnotation(Field field, Portal portal) {
        try {
            field.setAccessible(true);

            Observable<?> observable = (Observable<?>) field.get(portalHost);
            String name = portal.value().isEmpty() ? field.getName() : portal.value();
            portal(observable, name);
        } catch (Exception e) {
        }
    }

    private void reversePortalFromAnnotation(Field field, ReversePortal reversePortal) {
        try {
            field.setAccessible(true);
            String name = reversePortal.value().isEmpty() ? field.getName() : reversePortal.value();
            MRReversePusher<?> pusher = reversePusherForName(name, field, reversePortal);
            Observable<?> observable = Observable.create(new MRReversePushOnSubscribe<>(pusher));
            field.set(portalHost, observable.observeOn(AndroidSchedulers.mainThread()));

            reversePortal(name, pusher);
        } catch (Exception e) {
        }

    }

    private MRReversePusher<?> reversePusherForName(String name, Field field, ReversePortal reversePortal) {
        Class<?> clazz = classForField(field);
        MRReversePusher<?> pusher = null;
        if (pushers.containsKey(name))
            pusher = pushers.get(name);
        else {
            pusher = reversePortal.reEmit() ?
                    new MRReEmitReversePusher<>(clazz)
                    : new MRReversePusher<>(clazz);
            pushers.put(name, pusher);
        }
        return pusher;
    }


    Class<?> classForField(Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    public void portal(Observable<?> observable, String name) {
        if (!portalsGeneratedBehind) {
            String createScript = String.format("mrhelper.portal('%s', '%s')", loadedName, name);
            moonRock.runJS(createScript, null);
        }

        Subscription sub = observable.observeOn(AndroidSchedulers.mainThread()).subscribe(input -> {
            try {
                String serializedInput = input != null ? gson.toJson(input) : "null";
                String mirrorScript = String.format("mrhelper.activatePortal('%s', '%s', '%s')", loadedName, name, serializedInput);
                moonRock.runJS(mirrorScript, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        portalSubscriptions.add(sub);
    }

    public <T> void reversePortal(String name, MRReversePusher pusher) {
        moonRock.getReversePortals().registerReverse(name, pusher);
        if (!portalsGeneratedBehind) {
            String reverseScript = String.format("mrhelper.reversePortal('%s', '%s')", loadedName, name);
            moonRock.runJS(reverseScript, null);
        }
    }

    //Call after forwards portals have been setup
    public void portalsGenerated() {
        String finishedScript = String.format("mrhelper.portalsGenerated('%s')", loadedName);
        moonRock.runJS(finishedScript, null);
    }

    //Cal after reverse portals have been linked
    public void portalsLinked() {
        String finishedScript = String.format("mrhelper.portalsLinked('%s')", loadedName);
        moonRock.runJS(finishedScript, null);
    }

    public void unlinkPortals() {
        for(Subscription sub : portalSubscriptions)
            sub.unsubscribe();
    }

    public void setPortalHost(Object portalHost) {
        this.portalHost = portalHost;
    }
}