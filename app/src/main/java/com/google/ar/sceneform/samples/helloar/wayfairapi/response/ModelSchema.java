package com.google.ar.sceneform.samples.helloar.wayfairapi.response;

import com.google.gson.annotations.SerializedName;

public class ModelSchema {

    @SerializedName("dimensions_inches")
    private DimensionsSchema dimensionsInches;

    @SerializedName("glb")
    private String glbUrl;

    @SerializedName("obj")
    private String objUrl;

    public DimensionsSchema getDimensionsInInches() {
        return dimensionsInches;
    }

    public String getGlbUrl() {
        return glbUrl;
    }

    public String getObjUrl() {
        return objUrl;
    }
}
