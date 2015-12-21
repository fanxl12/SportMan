package com.agitation.sportman.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.SharePreferenceUtil;
import com.agitation.sportman.utils.ToastUtils;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fanwl on 2015/11/1.
 */
public class Registered extends BaseActivity {

    private AQuery aq;
    private EditText et_new_phone, et_new_password, et_new_password_again, et_verification_code;
    private String userName, password, verifyCode;
    private Button bt_getverrification_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registered);
        initToolbar();
        init();
        initVarible();
    }

    private void init() {
        et_new_phone = (EditText)findViewById(R.id.et_new_phone);
        et_verification_code = (EditText)findViewById(R.id.et_verification_code);
        bt_getverrification_code = (Button) findViewById(R.id.bt_getverrification_code);
        et_new_password = (EditText)findViewById(R.id.et_new_password);
        et_new_password_again = (EditText)findViewById(R.id.et_new_password_again);
        findViewById(R.id.bt_confirm_menber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegistered();
            }
        });
        bt_getverrification_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = et_new_phone.getText().toString().trim();
                if (TextUtils.isEmpty(userName)){
                    ToastUtils.showToast(Registered.this, "手机不能为空");
                    return;
                }
                getVerifyCode();
                countDown();
            }
        });
    }

    private void initVarible() {
        aq = new AQuery(this);

    }

    private void goToRegistered() {
        userName = et_new_phone.getText().toString().trim();
        if (TextUtils.isEmpty(userName)){
            ToastUtils.showToast(this, "手机号不能为空");
            return;
        }
        String code = et_verification_code.getText().toString().trim();
        if (!code.equals(verifyCode)){
            ToastUtils.showToast(Registered.this, "验证码错误");
            return;
        }
        password = et_new_password.getText().toString().trim();
        if (TextUtils.isEmpty(password)){
            ToastUtils.showToast(this,"密码不能为空");
            return;
        }
        String VipPasswordAgain = et_new_password_again.getText().toString().trim();
        if (TextUtils.isEmpty(VipPasswordAgain)){
            ToastUtils.showToast(this,"确认密码不能为空");
            return;
        }
        if (!password.equals(VipPasswordAgain)){
            ToastUtils.showToast(this,"两次输入的密码不一样，请重新输入");
            return;
        }
        String url = Mark.getServerIp()+"/baseApi/register";
        Map<String,Object> param = new HashMap<>();
        param.put("userName", userName);
        param.put("passWord",password);
        aq.transformer(new MapTransformer()).ajax(url,param,Map.class,new AjaxCallback<Map>(){
            @Override
            public void callback(String url, Map result, AjaxStatus status) {
                if (result!=null){
                    if (Boolean.parseBoolean(result.get("result")+"")){
                        SharePreferenceUtil.setValue(Registered.this, Login.IS_RM_PW, false);
                        toLogin();
                    }else {
                        ToastUtils.showToast(Registered.this,"注册失败"+"," + result.get("error"));
                    }
                }else {
                    ToastUtils.showToast(Registered.this,status.getError());
                }
            }
        });

    }

    private void initToolbar() {
        if (toolbar!=null){
            title.setText("注册");
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    public void toLogin(){
        String url = Mark.getServerIp()+"/baseApi/login";
        Map<String,Object> param = new HashMap<>();
        param.put("userName", userName);
        param.put("passWord", password);
        aq.transformer(new MapTransformer()).ajax(url, param, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map result, AjaxStatus status) {
                if (result != null) {
                    if (Boolean.parseBoolean(result.get("result") + "")) {
                        DataHolder dataHolder = DataHolder.getInstance();
                        dataHolder.setBasicHandle(userName, password);
                        dataHolder.setUserData((Map<String, Object>) result.get("retData"));
                        startActivity(new Intent(Registered.this, MainTabActivity.class));
                        finish();
                    } else {
                        ToastUtils.showToast(Registered.this, "注册失败" + "," + result.get("error"));
                    }
                }
            }
        });
    }

    //倒计时操作
    private void countDown(){
        bt_getverrification_code.setEnabled(false);
        new CountDownTimer(60*1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                bt_getverrification_code.setText("重发("+millisUntilFinished/1000+"秒)");
            }

            @Override
            public void onFinish() {
                bt_getverrification_code.setText("获取验证码");
                bt_getverrification_code.setEnabled(true);
            }
        }.start();
    }
    //获取验证码
    private void getVerifyCode(){
        String url = Mark.getServerIp()+"/baseApi/getPhoneCode";
        Map<String, Object> param = new HashMap<>();
        param.put("phoneNumber",userName);
        param.put("action","register");
        aq.transformer(new MapTransformer()).ajax(url, param, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map result, AjaxStatus status) {
                if (Boolean.parseBoolean(result.get("result") + "")) {
                    verifyCode = result.get("code") + "";
                } else {
                    ToastUtils.showToast(Registered.this, status.getError());
                }
            }
        });
    }

}
