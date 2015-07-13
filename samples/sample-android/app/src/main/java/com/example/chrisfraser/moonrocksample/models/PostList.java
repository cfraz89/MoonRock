package com.example.chrisfraser.moonrocksample.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * Created by chrisfraser on 7/07/15.
 */
public class PostList {
    private List<Post> data;

    public List<Post> getData() {
        return data;
    }
    public void setData(List<Post> data) {
        this.data = data;
    }
}
