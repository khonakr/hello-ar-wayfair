/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.sceneform.samples.helloar;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.samples.helloar.wayfairapi.RetrofitClientInstance;
import com.google.ar.sceneform.samples.helloar.wayfairapi.request.WayfairApiRequest;
import com.google.ar.sceneform.samples.helloar.wayfairapi.response.ProductInfoSchema;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class HelloSceneformActivity extends AppCompatActivity {
    private static final String TAG = HelloSceneformActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private static final String EMAIL = "Your-email-here";
    private static final String API_KEY = "Your-api-key-here";


    private ArFragment arFragment;
    private ModelRenderable modelRenderable;
    private ModelRenderable cubeRenderable;

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * <p>Finishes the activity if Sceneform can not run
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        // Step 1: Start ARView / ARFragment
        setContentView(R.layout.activity_ux);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        // Make Wayfair Api call
        // API DOCS: bit.ly/wayfair3dapi
        // Get 5 models without registration / key
        // Register for free to get access to 200 Wayfair models
        RetrofitClientInstance.getRetrofitInstance(EMAIL, API_KEY)
                .create(WayfairApiRequest.class)
                .listModels()
                .enqueue(new Callback<List<ProductInfoSchema>>() {
                    @Override
                    public void onResponse(Call<List<ProductInfoSchema>> call, Response<List<ProductInfoSchema>> response) {
                        // Step 2: Get 3D assets and create a renderable
                        get3DAsset(response.body().get(0).getModel().getGlbUrl());
                    }

                    @Override
                    public void onFailure(Call<List<ProductInfoSchema>> call, Throwable t) {
                        Log.e("Api request failed", t.getMessage());
                    }
                });

        // Step 4: Add interactions to the Scene
        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (modelRenderable == null) {
                        return;
                    }

                    // Step 3: Build the Scene
                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    // Create the transformable model Node and add it to the anchor.
                    // A TransformableNode allows the node to be Translated, Rotated and Scaled by user
                    TransformableNode modelNode = new TransformableNode(arFragment.getTransformationSystem());
                    modelNode.setParent(anchorNode);
                    modelNode.setRenderable(modelRenderable);
                    // Wayfair models are of correct scale and dimensions already
                    // Disable Scale controller of a TransformableNode in order to prevent model scaling
                    modelNode.getScaleController().setEnabled(false);
                    modelNode.select();


                    // Inorder to hide the built in shadow plane for Wayfair models
                    // This is not required for other models
                    modelRenderable.setMaterial(modelRenderable.getSubmeshCount() - 1, cubeRenderable.getMaterial());
                });
    }

    // Step 2: Get 3D assets and create a renderable
    private void get3DAsset(String modelUrl) {
        // When you build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
        ModelRenderable.builder()
                .setSource(this,
                        RenderableSource.builder().setSource(
                                this,
                                Uri.parse(modelUrl),
                                RenderableSource.SourceType.GLB)
                                .setScale(1.0f)
                                .build())
                .build()
                .thenAccept(renderable -> {
                    // Create a renderable
                    this.modelRenderable = renderable;
                    getCubeAsset();
                })
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load model renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
    }


    // Inorder to hide the built in shadow plane for Wayfair models
    // This is not required for other models
    private void getCubeAsset() {
        ModelRenderable.builder()
                .setSource(this, R.raw.cube)
                .build()
                .thenAccept(renderable -> {
                    cubeRenderable = renderable;
                })
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load cube model renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
    }
}
