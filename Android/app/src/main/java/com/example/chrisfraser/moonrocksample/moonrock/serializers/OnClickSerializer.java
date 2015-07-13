package com.example.chrisfraser.moonrocksample.moonrock.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import rx.android.view.OnClickEvent;

/**
 * Created by chrisfraser on 13/07/15.
 */
public class OnClickSerializer implements JsonSerializer<OnClickEvent>{
    @Override
    public JsonElement serialize(OnClickEvent src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonObject();
    }
}
