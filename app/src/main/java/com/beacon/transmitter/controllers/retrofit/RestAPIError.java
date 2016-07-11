package com.beacon.transmitter.controllers.retrofit;


import com.beacon.transmitter.MyApp;
import com.beacon.transmitter.R;

/**
 * Created by Yuriy on 2016-07-10 Ibeacon.
 */

public class RestAPIError {

    private static final int NOT_FOUND = 404;
    private static final int BAD_REQUEST = 400;
    private static final int CONFLICT = 409;

    public static String getErrorMessage(int code) {
        switch (code) {
            case NOT_FOUND:
                return MyApp.appContext.getString(R.string.error_not_found_user);
            case CONFLICT:
                return MyApp.appContext.getString(R.string.error_conflict);
            case BAD_REQUEST:
                return MyApp.appContext.getString(R.string.error_bad_request);
        }
        return null;
    }

}
