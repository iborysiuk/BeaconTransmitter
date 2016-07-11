package com.beacon.transmitter.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Yuriy on 2016-07-09 Ibeacon.
 */

public class Users implements Serializable {

    @SerializedName(value = "username")
    @Expose
    private String username;

    @SerializedName(value = "email")
    @Expose
    private String email;

    @SerializedName(value = "password")
    @Expose
    private String password;

    @SerializedName(value = "isActivated")
    @Expose(serialize = false)
    private boolean isActivated;

    public Users(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Users(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isActivated() {
        return isActivated;
    }
}
