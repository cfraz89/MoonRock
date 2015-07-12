package com.example.chrisfraser.rxbridgetest.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by chrisfraser on 13/07/15.
 */
@JsonObject
public class Add {
    @JsonField
    int input1;
    @JsonField
    int input2;

    public Add() {}

    public Add(String input1, String input2) {
        this.input1 = Integer.parseInt(input1);
        this.input2 = Integer.parseInt(input2);
    }
}
