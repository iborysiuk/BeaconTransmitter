package com.beacon.transmitter.controllers.retrofit;

import com.beacon.transmitter.model.Users;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Yuriy on 2016-07-09 Ibeacon.
 */

public interface ApiService {

    @POST("users/register")
    Observable<Response<ResponseBody>> register();

    @POST("users/login")
    Observable<Response<Users>> login();
}
