package com.beacon.transmitter.controllers.util;

/**
 * Created by Yuriy on 2016-07-09 Ibeacon.
 */

public class Validation {

    public static boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

}
