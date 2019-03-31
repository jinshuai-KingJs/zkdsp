# zkdsp
zkdsp for android

git clone git@github.com:jinshuai-KingJs/zkdsp.git
把项目拉到本地

Android Studio集成步骤：
1. File->New->Import Module... 选中zkdsp目录，确定
2. 项目的build.gradle中添加：
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    ....................
    compile project(':zkdsp')
    ....................
    }
3. 项目的AndroidManifest.xml中添加

     <activity
            android:name="com.zkdsp.sdk.VideoActivity"
            android:configChanges="orientation|keyboardHidden|screenSize"
            android:theme="@android:style/Theme.NoTitleBar.Fullscreen" />

4.在需要促发广告的Activity中：

 import com.zkdsp.sdk.VideoHandler;
 import com.zkdsp.sdk.VideoHandlerListener;
 VideoHandler handler;
 VideoHandlerListener listener;//回调接口
 handler = new VideoHandler(MainActivity.this, unit, token);//其中unit是广告位ID,Token是服务商标识，会单独提供
 listener = new VideoHandlerListener() {
                         @Override
                         public void onVideoLoadSuccess(String unitId) {
                             Log.d(TAG, "onVideoLoadSuccess: " + unitId);
                         }

                         @Override
                         public void onLoadSuccess(String unitId) {
                             Log.d(TAG, "onLoadSuccess: " + unitId);
                         }

                         @Override
                         public void onVideoLoadFail(String errorMsg) {
                             Log.d(TAG, "onVideoLoadFail: " + errorMsg);
                         }

                         @Override
                         public void onAdShow() {
                             Log.d(TAG, "onAdShow: ");
                         }

                         @Override
                         public void onAdClose() {
                             Log.d(TAG, "onAdClose: ");
                         }

                         @Override
                         public void onShowFail(String errorMsg) {
                             Log.d(TAG, "onShowFail: " + errorMsg);
                         }

                         //点击广告的回调
                         @Override
                         public void onVideoAdClicked(String unitId) {
                             Log.d(TAG, "onVideoAdClicked: ");
                         }

                         @Override
                         public void onVideoComplete(String unitId) {
                             Log.d(TAG, "onVideoComplete: ");
                         }

                         @Override
                         public void onEndcardShow(String unitId) {
                             Log.d(TAG, "onEndcardShow: " + unitId);
                         }
                     };
 handler.setListener(listener);//设置回调方法

 handler.load();//加载广告

//如果加载完成，那么可以进行展示

    if(handler != null && handler.isReady())
    {

                    handler.show();

                } else {

                    Log.d(TAG, "onClick: not ready" );

                }
    }



