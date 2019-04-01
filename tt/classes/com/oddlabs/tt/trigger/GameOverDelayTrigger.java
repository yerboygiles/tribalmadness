package com.oddlabs.tt.trigger;

import com.oddlabs.tt.animation.TimerAnimation;
import com.oddlabs.tt.animation.Updatable;
import com.oddlabs.tt.camera.Camera;
import com.oddlabs.tt.delegate.GameStatsDelegate;
import com.oddlabs.tt.viewer.WorldViewer;

public final strictfp class GameOverDelayTrigger implements Updatable {

    private final TimerAnimation delay_timer;
    private final WorldViewer viewer;
    private final Camera camera;
    private final String label_str;

    public GameOverDelayTrigger(WorldViewer viewer, Camera camera, String label_str) {
        this.viewer = viewer;
        this.camera = camera;
        this.label_str = label_str;
        this.delay_timer = new TimerAnimation(viewer.getWorld().getAnimationManagerRealTime(), this, 1.5f);
        delay_timer.start();
    }

    @Override
    public void update(Object anim) {
        delay_timer.stop();
        viewer.getGUIRoot().pushDelegate(new GameStatsDelegate(viewer, camera, label_str));
    }
}
