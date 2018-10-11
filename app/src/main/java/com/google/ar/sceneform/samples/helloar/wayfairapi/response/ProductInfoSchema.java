package com.google.ar.sceneform.samples.helloar.wayfairapi.response;

import com.google.gson.annotations.SerializedName;

public class ProductInfoSchema {

    @SerializedName("sku")
    private String sku;

    @SerializedName("product_name")
    private String producName;

    @SerializedName("product_description")
    private String productDescription;

    @SerializedName("product_page_url")
    private String productPageUrl;

    @SerializedName("class_name")
    private String className;

    @SerializedName("sale_price")
    private String salePrice;

    @SerializedName("thumbnail_image_url")
    private String thumbnailImageUrl;

    @SerializedName("model")
    private ModelSchema model;

    public String getSku() {
        return sku;
    }

    public String getProducName() {
        return producName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getProductPageUrl() {
        return productPageUrl;
    }

    public String getClassName() {
        return className;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public ModelSchema getModel() {
        return model;
    }
}
