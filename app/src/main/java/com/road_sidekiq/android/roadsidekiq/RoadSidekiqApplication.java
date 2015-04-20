package com.road_sidekiq.android.roadsidekiq;

import android.app.Application;

import com.road_sidekiq.android.roadsidekiq.utilities.FontFace;

/**
 * Created by rewin0087 on 4/18/15.
 */
public class RoadSidekiqApplication extends Application {
    public void onCreate() {
        FontFace.setAppFontToLed(this);
        setTheme(R.style.Theme_Yellow);
        super.onCreate();
    }
}
