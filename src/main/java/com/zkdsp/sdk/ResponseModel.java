package com.zkdsp.sdk;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by KingJS on 2019/3/30.
 */

public class ResponseModel implements Serializable {
//    @SerializeName("Code")
    public int Code;

//    @SerializeName("Msg")
    public String Msg;

    public ResponseData Data;

    public static class ResponseData{
        public String resAdId;
        public ResponseDataAd ad;
    }

    public static class ResponseDataAd{
        public String img; //图片链接
        public String title; //标题
        public String url; //落地页
        public String ext1; //mp4素材
        public String ext2;
        public String logo; //logo位置
    }


}
