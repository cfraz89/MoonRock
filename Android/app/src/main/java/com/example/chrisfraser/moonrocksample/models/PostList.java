package com.example.chrisfraser.moonrocksample.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * Created by chrisfraser on 7/07/15.
 */
@JsonObject
public class PostList {
    @JsonField
    List<Post> data;

    public List<Post> getData() {
        return data;
    }
}
