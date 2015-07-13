package com.example.chrisfraser.moonrocksample.moonrock.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import rx.android.widget.OnTextChangeEvent;

/**
 * Created by chrisfraser on 13/07/15.
 */
public class OnTextChangedSerializer implements JsonSerializer<OnTextChangeEvent> {
    @Override
    public JsonElement serialize(OnTextChangeEvent src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.text().toString());
    }
}
