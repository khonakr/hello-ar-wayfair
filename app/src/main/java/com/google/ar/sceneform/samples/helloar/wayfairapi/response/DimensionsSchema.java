package com.google.ar.sceneform.samples.helloar.wayfairapi.response;

import com.google.gson.annotations.SerializedName;

public class DimensionsSchema {

    @SerializedName("x")
    private float x;

    @SerializedName("y")
    private float y;

    @SerializedName("z")
    private float z;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }
}
