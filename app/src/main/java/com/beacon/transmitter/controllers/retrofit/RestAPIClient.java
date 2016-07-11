package com.beacon.transmitter.controllers.retrofit;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static okhttp3.logging.HttpLoggingInterceptor.Level;

/**
 * Created by Yuriy on 2016-07-09 Ibeacon.
 */

public class RestAPIClient extends RestAPICustomSSL {

    private static final String TAG = "RestAPIClient";

    private static final String API_USERNAME = "ibeacon";
    private static final String API_PASSWORD = "ibeacon2016";

    private static final String BASIC_URL = "https://ec2-54-173-90-117.compute-1.amazonaws.com:8443/api/";
    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient.Builder httpClient = configureSSL(new OkHttpClient.Builder());

    private static Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASIC_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());


    public static <T> T createVPService(Class<T> serviceClass, Object body) {
        return createService(serviceClass, BASIC_URL, API_USERNAME, API_PASSWORD, body);
    }

    private static <T> T createService(Class<T> serviceClass, String url, String username, String password, final Object body) {

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            String credentials = Credentials.basic(username, password);

            httpClient.addInterceptor(chain -> {
                Request originalRequest = chain.request();

                //transform && compose request body
                String jsonBody = getJsonBody(body);
                Log.i(TAG, "=====>RequestBody. json: " + jsonBody);

                RequestBody requestBody = !TextUtils.isEmpty(jsonBody)
                        ? RequestBody.create(JSON_TYPE, jsonBody) : null;

                Request.Builder builder1 = originalRequest.newBuilder().post(requestBody);

                //compose final authorizedRequest
                Request authorizedRequest = builder1
                        .addHeader("Authorization", credentials)
                        .build();

                Log.i(TAG, "=====>RequestUrl:" + authorizedRequest.toString());
                return chain.proceed(authorizedRequest);
            });
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.d(TAG, message));
        logging.setLevel(Level.BASIC);
        httpClient.addInterceptor(logging);

        OkHttpClient client = httpClient
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
                .build();

        Retrofit retrofit = builder
                .baseUrl(url)
                .client(client)
                .build();

        return retrofit.create(serviceClass);
    }

    private static String getJsonBody(Object body) {
        if (body == null) return null;
        return gson.toJson(body);
    }

}
