package com.beacon.transmitter.controllers.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.beacon.transmitter.MyApp;

import java.io.IOException;

/**
 * Created by Yuriy on 2016-07-09 Ibeacon.
 */

public class NetworkUtil {

    private static boolean isNetworkAvailable() {
        NetworkInfo activeNetworkInfo = getConnectivityManager().getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private static ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) MyApp.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static boolean isOnline() {
        if (!isNetworkAvailable()) return false;
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

}
