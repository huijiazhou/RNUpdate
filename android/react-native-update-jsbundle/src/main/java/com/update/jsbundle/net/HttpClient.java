package com.update.jsbundle.net;


import okhttp3.OkHttpClient;

/**
 * Created by zhj on 2018/7/20.
 */

public class HttpClient {

    private static HttpClient httpClient;

    private OkHttpClient okHttpClient;

    public static HttpClient get() {
        if (httpClient == null) {
            synchronized (HttpClient.class) {
                if (httpClient == null) {
                    httpClient = new HttpClient();
                }
            }
        }
        return httpClient;
    }

    private HttpClient() {

//自定义OkHttpClient
        OkHttpClient.Builder okHttpClient2 = new OkHttpClient.Builder();
//添加拦截器

        okHttpClient2.sslSocketFactory(SSLSocketFactoryUtils.createSSLSocketFactory(), SSLSocketFactoryUtils.createTrustAllManager());
        okHttpClient2.hostnameVerifier(new SSLSocketFactoryUtils.TrustAllHostnameVerifier());
        // 超时时间

        // 错误重连
        okHttpClient2.retryOnConnectionFailure(true);


        okHttpClient = okHttpClient2.build();


    }

    public static OkHttpClient getHttpClient() {
        return get().okHttpClient;
    }
}
