package com.zkdsp.sdk;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by KingJS on 2019/3/30.
 */

public class Util {
    private static final String TAG = "Util";
    public static HashMap<String, VideoHandlerListener> listenerMap;

    static {
        listenerMap = new HashMap<String, VideoHandlerListener>();
    }

    public static void adsShow(String adsid){
        String showUrl = String.format("%s?source=baihe&step=client&adid=%s", Constant.ShowUrl, adsid);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(showUrl).method("GET", null).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                String resp = response.body().string();
//                Log.d(TAG, "onResponse: " + resp);
            }
        });
    }

    public static void adsClick(String adsid){
        String clickUrl = String.format("%s?source=baihe&adid=%s", Constant.ClickUrl, adsid);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(clickUrl).method("GET", null).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                String resp = response.body().string();
//                Log.d(TAG, "onResponse: " + resp);
            }
        });
    }
}
