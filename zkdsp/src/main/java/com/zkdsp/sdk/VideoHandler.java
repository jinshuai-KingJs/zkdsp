package com.zkdsp.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by KingJS on 2019/3/29.
 */

public class VideoHandler {
    private static final String TAG = "VideoHandler";
    VideoHandlerListener mListener;
    String unit,token;
    String videoFile, pageImg, logoImg, pageTitle, landPage;
    String dspUrl = "";
    String cacheDir = ""; //临时文件都存在这里
    String adsID = "";
    Context mContext;
    Boolean isReady = false;

    public VideoHandler(Activity context, String id,String tkn){
        mContext = context;
        unit = id;
        token = tkn;
        cacheDir = context.getCacheDir().getPath() + File.separator ;
        buildUrl();
    }

    private String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void buildUrl(){
        String time = String.valueOf(System.currentTimeMillis());
        String apk = "com.huolea.bull";
        String bidstr = token + "_" + unit + "_" + apk + "_" + time;
        String bid = md5(bidstr);
        dspUrl = String.format("%s?" +
                "bid=%s&unit=%s&width=640&height=960" +
                "&screen_width=720&screen_height=1280" +
                "&ts=%s&app_pkg=%s&os=Android",Constant.FetchUrl, bid, unit, time, apk);
        Log.d(TAG, "buildUrl: :" + dspUrl);
    }

    public void setListener(VideoHandlerListener listener){
        mListener = listener;
        if(!TextUtils.isEmpty(unit) && mListener != null){
            Util.listenerMap.put(unit, mListener);
        }
    }

    public boolean isReady(){
        return isReady;
    }

    public void load() {
        if(mListener == null){
            return;
        }
        Log.d(TAG, "loading dsp configure");
        //1.创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(dspUrl).method("GET", null).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            //请求失败执行的方法
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
                mListener.onVideoLoadFail(e.getMessage());
            }
            //请求成功执行的方法
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Log.d(TAG, "onResponse: " + resp);
                Gson gson = new Gson();
                ResponseModel model = gson.fromJson(resp, ResponseModel.class);

                if(model == null || model.Code != 0 ){
                    mListener.onVideoLoadFail("parse response " + resp + " failed!");
                    return;
                }
                adsID = model.Data.resAdId;
                pageImg = model.Data.ad.img;
                logoImg = model.Data.ad.logo;
                pageTitle = model.Data.ad.title;
                pageImg = model.Data.ad.img;
                landPage = model.Data.ad.url;

                Log.d(TAG, "onResponse id: " + model.Data.resAdId);
                mListener.onLoadSuccess(unit);
                downloadMaterial(model); //下载素材到缓存,只是下载视频，图片临时加载就行
            }
        });
    }

    /***
     * 不要在主线程中调用
     * @param model
     */
    private void downloadMaterial(ResponseModel model){
        String[] splits = model.Data.ad.ext1.split("/");
        if(splits.length < 2){
            mListener.onVideoLoadFail("素材名非法:"+ model.Data.ad.ext1);
            return;
        }
        String fname = splits[splits.length-1];
        videoFile = cacheDir + fname;
        try{
            File file = new File(videoFile);
            //不重复下载
            if(file.exists()){
                isReady = true;
                Log.d(TAG, "downloadMaterial: " + videoFile + " exists!");
                mListener.onVideoLoadSuccess(unit);
                return;
            }
        }catch (Exception e){
            mListener.onVideoLoadFail(e.getMessage());
            return;
        }
        try{
            downloadFile(model.Data.ad.ext1, videoFile);
        }catch (Exception e){
            mListener.onVideoLoadFail(e.getMessage());
        }
        isReady = true;
        mListener.onVideoLoadSuccess(unit);
    }

    /***
     * 不要在主线程里运行
     * @param url 下载地址
     * @param fpath 存储地址
     * @throws IOException
     */
    public void downloadFile(String url, String fpath) throws IOException {
        long fileSize;
        File out = new File(fpath);
        URL myURL = new URL(url);
        URLConnection conn = myURL.openConnection();
        conn.connect();
        InputStream is = conn.getInputStream();
        fileSize = conn.getContentLength();
        if (fileSize <= 0)
            throw new RuntimeException("can not know the file`s size");
        if(fileSize >= 10 * 1024 * 1024){
            throw new RuntimeException("file size:" + fileSize + " is too large");
        }
        if (is == null)
            throw new RuntimeException("stream is null");
        Log.d(TAG, "downloadFile size:" + fileSize);
        FileOutputStream fos = new FileOutputStream(out);
        byte buf[] = new byte[1024];
        do {
            // 循环读取
            int numread = is.read(buf);
            if (numread == -1) {
                break;
            }
            fos.write(buf, 0, numread);
            Log.d(TAG, "download...");
        } while (true);
        try {
            is.close();
            Log.d(TAG, "down load:" + url + " complete");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void show(){
        if(mListener == null){
            return;
        }
        Intent intent = new Intent(mContext, VideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.INTENT_VIDEO, videoFile);
        bundle.putString(Constant.INTENT_UNIT, unit);
        bundle.putString(Constant.INTENT_IMG, pageImg);
        bundle.putString(Constant.INTENT_LOGO, logoImg);
        bundle.putString(Constant.INTENT_TITLE, pageTitle);
        bundle.putString(Constant.INTENT_LANDURL, landPage);
        bundle.putString(Constant.INTENT_ADSID, adsID);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }
}
