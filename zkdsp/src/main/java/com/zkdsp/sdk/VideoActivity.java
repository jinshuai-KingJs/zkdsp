package com.zkdsp.sdk;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.squareup.picasso.Picasso;
import com.zkdsp.sdk.R;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
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

import java.util.Timer;
import java.util.TimerTask;


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
    LinearLayout encardContentLayout;
    FrameLayout encardLayout,webviewLayout,videoviewLayout;
    ImageView closeImg, encardImgView, encardLogoImgView;
    TextView encardTitleTextView, videoRemainTextView;
    Button encardBtn;
    Timer timer;
    int ticks;//剩余时间
    int duration; //总的时间，用来恢复计时
    String remainStr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_video);
        bindView();
        ticks = -1;
        remainStr = getResources().getString(R.string.remainsec);
        timer = new Timer();


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
//        if(!Constant.isDebug) {
//            mMediaController.setVisibility(View.GONE);
//        }
        //结束之后的监听方法

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                //播放结束后的动作
                if(timer != null) {
                    timer.cancel();
                    // 一定设置为null，否则定时器不会被回收
                    timer = null;
                }
                Log.d(TAG, "onCompletion: " + videoFile);
                onVideoComplete();
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                ticks = mVideoView.getDuration()/1000;
                duration = ticks;
                setTicks(ticks);
//                Log.d(TAG, "total ticks: " + ticks );

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(ticks > 0) {
                            ticks--;
                            setTicks(ticks);
                            Log.d(TAG, "remain ticks:" + ticks);
                        }
                    }
                }, 1000, 1000);
            }
        });
        //开始播放视频
        if(!TextUtils.isEmpty(videoFile)){
            mVideoView.setVideoPath(videoFile);
            if(Constant.isDebug) {
                mVideoView.setMediaController(mMediaController);
            }

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

    private void setTicks(final int val){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(val < 0 ){
                    return;
                }
                String str = remainStr.replace("#", String.valueOf(val));
                Log.d(TAG, "setTicks: " + str);
                if(videoRemainTextView != null) {
                    videoRemainTextView.setText(str);
                }
            }
        });
    }

    private void bindView(){
        videoviewLayout = (FrameLayout) findViewById(R.id.video_layout);
        encardContentLayout = (LinearLayout) findViewById(R.id.encard_content);
        encardLayout = (FrameLayout) findViewById(R.id.encard_layout);
        webviewLayout = (FrameLayout) findViewById(R.id.webview_layout);
        closeImg = (ImageView) findViewById(R.id.encard_close);
        encardImgView = (ImageView) findViewById(R.id.encard_img);
        encardLogoImgView = (ImageView) findViewById(R.id.encard_logo);
        encardTitleTextView = (TextView) findViewById(R.id.encard_title);
        videoRemainTextView = (TextView) findViewById(R.id.video_remain);
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
            ticks = duration;//恢复计时
            setTicks(ticks);
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
        if(timer != null){
            timer.cancel();
            timer = null;
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
