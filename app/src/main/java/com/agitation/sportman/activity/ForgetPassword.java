package com.agitation.sportman.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.ToastUtils;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fanwl on 2015/11/25.
 */
public class ForgetPassword extends BaseActivity {

    private EditText et_userName, et_NewPhone, et_verification_code;
    private Button bt_getverrification_code;
    private String verifyCode, userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);
        initToolbar();
        initView();
    }

    private void initToolbar() {
        if (toolbar!=null){
            title.setText("找回密码");
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initView() {
        et_userName = (EditText) findViewById(R.id.et_userphone);
        et_NewPhone = (EditText) findViewById(R.id.et_new_password);
        et_verification_code = (EditText) findViewById(R.id.et_verification_code);

        findViewById(R.id.bt_confirm_menber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = et_userName.getText().toString().trim();
                String userPW = et_NewPhone.getText().toString().trim();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPW)) {
                    ToastUtils.showToast(ForgetPassword.this, "用户名或密码不能为空");
                    return;
                }
                modiftPassword(userName, userPW);
            }
        });
        bt_getverrification_code = (Button) findViewById(R.id.bt_getverrification_code);
        bt_getverrification_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = et_userName.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    ToastUtils.showToast(ForgetPassword.this, "手机号不能为空");
                    return;
                }
                getVerifyCode();
                countDown();
            }
        });
    }

    public void modiftPassword(String userName,String userPW){
        String code = et_verification_code.getText().toString().trim();
        if (!code.equals(verifyCode)){
            ToastUtils.showToast(ForgetPassword.this, "验证码错误");
            return;
        }
        String url = Mark.getServerIp() + "/baseApi/updatePw";
        Map<String,Object> param = new HashMap<>();
        param.put("userName", userName);
        param.put("passWord",userPW);
        aq.transformer(new MapTransformer()).ajax(url, param, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                if (info != null) {
                    if (Boolean.parseBoolean(info.get("result") + "")) {
                        ToastUtils.showToast(ForgetPassword.this, "重置成功");
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
        param.put("action","forgetPw");
        aq.transformer(new MapTransformer()).ajax(url, param, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map result, AjaxStatus status) {
                if (Boolean.parseBoolean(result.get("result") + "")) {
                    verifyCode = result.get("code") + "";
                } else {
                    ToastUtils.showToast(ForgetPassword.this, status.getError());
                }
            }
        });
    }

}
