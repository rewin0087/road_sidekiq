package com.road_sidekiq.android.roadsidekiq.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;

import com.road_sidekiq.android.roadsidekiq.utilities.ConnectionDetector;
import com.road_sidekiq.android.roadsidekiq.utilities.GPSTracker;

/**
 * Created by rewin0087 on 4/19/15.
 */
public class BaseActivity extends ActionBarActivity {
    final protected Handler handler = new Handler();

    public SharedPreferences preferences;

    public SharedPreferences.Editor editor;

    public static String LOGGEDIN = "LOGGEDIN";

    protected ConnectionDetector cd;

    protected GPSTracker gps;

    protected Bundle bundle;

}
