package com.beacon.transmitter.controllers.retrofit;

import android.annotation.SuppressLint;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Created by Yuriy on 2016-07-09 Ibeacon.
 */

public class RestAPICustomSSL {

    @SuppressWarnings("null")
    protected static OkHttpClient.Builder configureSSL(OkHttpClient.Builder client) {
        SSLContext sslContext = null;
        X509TrustManager x509TrustManager = null;
        try {
            x509TrustManager = getTrustManager();
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, new TrustManager[]{x509TrustManager}, new SecureRandom());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        try {
            final HostnameVerifier hostnameVerifier = (hostname, session) -> true;
            if (sslContext != null) {
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                client.hostnameVerifier(hostnameVerifier)
                        .sslSocketFactory(sslSocketFactory, x509TrustManager);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return client;
    }


    private static X509TrustManager getTrustManager() {
        return new X509TrustManager() {
            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }
}
