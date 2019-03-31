package com.zkdsp.sdk;

/**
 * Created by KingJS on 2019/3/30.
 */

public class Constant {
    public static boolean isDebug = false;

    public static final String INTENT_UNIT = "intent_unit";
    public static final String INTENT_VIDEO = "intent_videourl";
    public static final String INTENT_LOGO = "intent_logo";
    public static final String INTENT_IMG = "intent_img";
    public static final String INTENT_TITLE = "intent_title";
//    public static final String INTENT_DESC = "intent_desc";
    public static final String INTENT_LANDURL = "intent_landurl";
    public static final String INTENT_ADSID = "intent_adsid";

    private static final String BaseUrl = "http://api.zhangkadsp.com";
    public static final String FetchUrl = BaseUrl + "/fetch";
    public static final String ShowUrl = BaseUrl + "/v2/show";
    public static final String ClickUrl = BaseUrl + "/v2/click";


}
