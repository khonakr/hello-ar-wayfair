package com.google.ar.sceneform.samples.helloar.wayfairapi;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static final String BASE_URL = "https://www.wayfair.com";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(String email, String apiKey) {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new BasicAuthInterceptor(email, apiKey));
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}