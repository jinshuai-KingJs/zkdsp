package com.zkdsp.sdk;

import android.app.Application;
import android.content.Context;

/**
 * Created by KingJS on 2019/3/29.
 */

public class ZKdsp extends Application {

    private static Context context;
    public static boolean isDebug = true;
    public static String token = "";

    @Override
    public void onCreate() {
        super.onCreate();
        //在这里初始化你需要初始化的东西
    }


    public static Context getContext() {
        return context;
    }



}