package sdk.test.com.sdksample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zkdsp.sdk.VideoActivity;
import com.zkdsp.sdk.VideoHandler;
import com.zkdsp.sdk.VideoHandlerListener;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "MainActivity";
    VideoHandler handler;
    VideoHandlerListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "You clicked module button!!");
                if(listener == null) {
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
                }
                if(handler == null) {
                    //测试的token和id，正式环境需要替换
                    handler = new VideoHandler(MainActivity.this, "15047", "3vpsR8Fa");
                    handler.setListener(listener);
                    handler.load();
                }
                if(handler != null && handler.isReady()) {
                    handler.show();
                } else {
                    Log.d(TAG, "onClick: not ready" );
                }
            }
        });
    }
}
