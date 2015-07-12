package com.example.chrisfraser.moonrocksample.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by chrisfraser on 7/07/15.
 */
@JsonObject
public class Post {
    @JsonField
    String title;

    @JsonField
    String body;

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
