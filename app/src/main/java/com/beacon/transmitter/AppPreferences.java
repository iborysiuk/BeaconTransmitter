package com.beacon.transmitter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Yuriy on 2016-07-09 Ibeacon.
 */

public class AppPreferences {

    private static final String PREF_CHECKED = "pref_checked";
    private static final String PREF_EMAIL = "pref_email";

    private static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(MyApp.appContext);
    }

    public static void setChecked(boolean isChecked) {
        getSharedPreferences().edit()
                .putBoolean(PREF_CHECKED, isChecked)
                .apply();
    }

    public static boolean isChecked() {
        return getSharedPreferences().getBoolean(PREF_CHECKED, false);
    }

    public static void setEmail(String email) {
        getSharedPreferences().edit()
                .putString(PREF_EMAIL, email)
                .apply();
    }

    public static String getEmail() {
        return getSharedPreferences().getString(PREF_EMAIL, null);
    }
}
