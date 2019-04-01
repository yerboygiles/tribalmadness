package com.oddlabs.tt.camera;


public final strictfp class StaticCamera extends Camera {
    public StaticCamera(CameraState camera) {
        super(null, camera);
    }

    @Override
    public void doAnimate(float t) {
    }
}
