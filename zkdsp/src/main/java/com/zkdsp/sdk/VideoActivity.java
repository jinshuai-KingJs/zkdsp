package com.zkdsp.sdk;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.squareup.picasso.Picasso;
import com.zkdsp.sdk.R;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;


/**
 * Created by KingJS on 2019/3/29.
 */

public class VideoActivity extends Activity{
    private static final String TAG = "VideoActivity";
    Context mContext;
    String videoFile,adsUnit, encardImg, encardLogo, landUrl, encardTitle, adsId;
    VideoHandlerListener mListener;
    VideoView mVideoView;
    WebView mWebview;
    MediaController mMediaController;
    LinearLayout videoviewLayout, encardContentLayout;
    FrameLayout encardLayout,webviewLayout;
    ImageView closeImg, encardImgView, encardLogoImgView;
    TextView encardTitleTextView;
    Button encardBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_video);
        bindView();


        Intent intent = getIntent();
        if (intent != null) {
            // 获取Bundle对象中的参数
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                videoFile = bundle.getString(Constant.INTENT_VIDEO, "");
                adsUnit = bundle.getString(Constant.INTENT_UNIT, "");//广告位ID
                encardImg = bundle.getString(Constant.INTENT_IMG, "");
                encardLogo = bundle.getString(Constant.INTENT_LOGO, "");
                encardTitle = bundle.getString(Constant.INTENT_TITLE, "");
                landUrl = bundle.getString(Constant.INTENT_LANDURL, "");
                adsId = bundle.getString(Constant.INTENT_ADSID, "");//每个广告的UID
                if(Util.listenerMap.containsKey(adsUnit)){
                    mListener = Util.listenerMap.get(adsUnit);
                }
            }
        }
        bindContents();
        bindClick();

//        Log.i(TAG, "onCreate: get videoFile:" + videoFile);
        mMediaController = new MediaController(mContext,false);
        if(!Constant.isDebug) {
            mMediaController.setVisibility(View.INVISIBLE);
        }
        //结束之后的监听方法
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                //播放结束后的动作
                Log.d(TAG, "onCompletion: " + videoFile);
                onVideoComplete();
            }
        });
        //开始播放视频
        if(!TextUtils.isEmpty(videoFile)){
            mVideoView.setVideoPath(videoFile);
            mVideoView.setMediaController(mMediaController);
            mVideoView.seekTo(0);
            mVideoView.requestFocus();
            mVideoView.start();
            Log.d(TAG, "start play: " + videoFile);
            if(mListener != null){
                mListener.onAdShow();
            }
            //统计展示
            Util.adsShow(adsId);
        } else {
            if(mListener != null){
                mListener.onShowFail("video url is empty");
            }
        }
    }

    private void bindView(){
        videoviewLayout = (LinearLayout) findViewById(R.id.video_layout);
        encardContentLayout = (LinearLayout) findViewById(R.id.encard_content);
        encardLayout = (FrameLayout) findViewById(R.id.encard_layout);
        webviewLayout = (FrameLayout) findViewById(R.id.webview_layout);
        closeImg = (ImageView) findViewById(R.id.encard_close);
        encardImgView = (ImageView) findViewById(R.id.encard_img);
        encardLogoImgView = (ImageView) findViewById(R.id.encard_logo);
        encardTitleTextView = (TextView) findViewById(R.id.encard_title);
        mWebview = (WebView) findViewById(R.id.webview);
        mVideoView = (VideoView) findViewById(R.id.video_view);
        encardBtn = (Button) findViewById(R.id.encard_button);
    }

    //添加控件图案，链接
    private void bindContents(){
        encardTitleTextView.setText(encardTitle);
        Picasso.with(this).load(encardImg).into(encardImgView);
        Picasso.with(this).load(encardLogo).into(encardLogoImgView);
    }

    private void bindClick(){
        //右上角的关闭点击
        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        encardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webviewLayout.setVisibility(View.VISIBLE);
                encardLayout.setVisibility(View.GONE);
                mWebview.getSettings().setJavaScriptEnabled(true);
                mWebview.loadUrl(landUrl);
//                Log.d(TAG, "encardLayout landurl: " + landUrl);
                if(mListener != null){
                    mListener.onVideoAdClicked(adsUnit);
                }
                //DSP统计点击
                Util.adsClick(adsId);
            }
        });

        //绑定layout，好像按钮的点击不太灵，重复绑定一次.不会重复促发上面的点击
        encardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webviewLayout.setVisibility(View.VISIBLE);
                encardLayout.setVisibility(View.GONE);
                mWebview.getSettings().setJavaScriptEnabled(true);
                mWebview.loadUrl(landUrl);
//                Log.d(TAG, "encardBtn landurl: " + landUrl);
                if(mListener != null){
                    mListener.onVideoAdClicked(adsUnit);
                }
                //DSP统计点击
                Util.adsClick(adsId);
            }
        });

    }

    //视频结束，展示encard页面
    private void onVideoComplete(){
        if(mListener != null){
            mListener.onVideoComplete(adsUnit);
            mListener.onEndcardShow(adsUnit);
        }
        videoviewLayout.setVisibility(View.GONE);
        encardLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mVideoView != null && mVideoView.canPause() && mVideoView.isPlaying()){
            mVideoView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mVideoView != null && !mVideoView.isPlaying()){
//            mVideoView.resume();
            mVideoView.seekTo(0);
            mVideoView.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlaybackVideo();
        if(mListener != null){
            mListener.onAdClose();
        }
    }

    private void stopPlaybackVideo() {
        try {
            if(mVideoView != null) {
                mVideoView.stopPlayback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            //播放视频的时候强制看完，不能退出
            if(mVideoView != null && mVideoView.isPlaying() && !Constant.isDebug
                    && videoviewLayout.getVisibility() == View.VISIBLE){
                return true;
            } else {
                return super.dispatchKeyEvent(event);
            }
        }else {
            return super.dispatchKeyEvent(event);
        }
    }
}
