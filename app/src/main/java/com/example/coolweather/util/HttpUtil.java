package com.example.coolweather.util;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

/**
 * Created by joy on 2016/12/20.
 */

public class HttpUtil {

    public static void sendOkHttpRequest(String address, Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
