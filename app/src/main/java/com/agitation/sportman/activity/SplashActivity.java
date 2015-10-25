package com.agitation.sportman.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.agitation.sportman.R;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.SharePreferenceUtil;
import com.androidquery.AQuery;

/**
 * Created by fanwl on 2015/10/25.
 */
public class SplashActivity extends AppCompatActivity {

    private static final int GO_TO_MAIN = 1;
    private static final int GO_TO_LOGIN = 2;
    private static final int DELAY_TIME = 2000;
    private String userName, passWord;

    private AQuery aq;
    private DataHolder dataHolder;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GO_TO_MAIN:
                    startActivity(new Intent(SplashActivity.this, Login.class));
                    SplashActivity.this.finish();
                    break;
                case GO_TO_LOGIN:
//                    autoLogin();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        checkAccount();
    }
    private void checkAccount(){
        boolean isRemember = SharePreferenceUtil.getBoolean(this, Login.IS_RM_PW, false);

        if (!isRemember){
//            dataHolder.setIsLogin(false);
            handler.sendEmptyMessageDelayed(GO_TO_MAIN, DELAY_TIME);
            return;
        }

        userName = SharePreferenceUtil.getString(this, Login.LOGIN_UN_NAME, "");
        passWord = SharePreferenceUtil.getString(this, Login.LOGIN_PW_NAME, "");

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWord)){
            handler.sendEmptyMessageDelayed(GO_TO_MAIN, DELAY_TIME);
        }else{
            handler.sendEmptyMessageDelayed(GO_TO_LOGIN, DELAY_TIME);
        }
    }
}
