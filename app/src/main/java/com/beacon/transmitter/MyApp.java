package com.beacon.transmitter;

import android.app.Application;

import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

/**
 * Created by Yuriy on 2016-07-09 Ibeacon.
 */

public class MyApp extends Application {

    public static MyApp appContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        new BackgroundPowerSaver(this);
    }

}
