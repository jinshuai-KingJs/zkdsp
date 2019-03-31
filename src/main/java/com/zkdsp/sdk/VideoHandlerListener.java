package com.zkdsp.sdk;

/**
 * Created by KingJS on 2019/3/29.
 */

public interface VideoHandlerListener {
    /**
     * Called when the ad is loaded , and is ready to be displayed completely
     * @param unitId
     */
    void onVideoLoadSuccess(String unitId);

    /**
     * Called when the ad is loaded , but is not ready to be displayed completely
     * @param unitId
     */
    void onLoadSuccess(String unitId);
    /**
     * Called when the ad is load failed with the errorMsg
     * @param errorMsg
     */
    void onVideoLoadFail(String errorMsg);
    /**
     * Called when the ad is shown
     */
    void onAdShow();

    /**
     * Called when the ad is closed
     */
    void onAdClose();

    /**
     * Called when the ad is shown failed
     * @param errorMsg
     */
    void onShowFail(String errorMsg);
    /**
     * Called when the ad is clicked
     * @param unitId
     */
    void onVideoAdClicked(String unitId);

    /**
     * Called when the ad played completely
     * @param unitId
     */
    void onVideoComplete(String unitId);

    /**
     * Called when the ad endcard be shown
     * @param unitId
     */
    void onEndcardShow(String unitId);
}
