package com.agitation.sportman.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.View;

import com.agitation.sportman.R;
import com.agitation.sportman.utils.SharePreferenceUtil;
import com.androidquery.AQuery;


/**
 * Created by fanwl on 2015/9/18.
 */
public class Login extends AppCompatActivity {

    private TextInputLayout login_username,login_password;
    private AppCompatCheckBox re_password;
    private AQuery aq;
    private boolean isRemeber = false;
    public static final String IS_RM_PW="IS_RM_PW";
    public static final String LOGIN_UN_NAME="LOGIN_UN_NAME";
    public static final String LOGIN_PW_NAME="LOGIN_PW_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();
        initVarible();
    }

    private void initVarible() {
        aq = new AQuery(this);
    }

    private void init() {
        login_username = (TextInputLayout) findViewById(R.id.login_username);
        login_password = (TextInputLayout) findViewById(R.id.login_password);
        re_password = (AppCompatCheckBox) findViewById(R.id.re_password);
        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,MainTabActivity.class));
                getToLogin();
            }
        });

        isRemeber = SharePreferenceUtil.getBoolean(this, "isRemeber", false);
        re_password.setChecked(isRemeber);

        if (isRemeber){
            String name = SharePreferenceUtil.getString(Login.this, "name", "");
            String password = SharePreferenceUtil.getString(Login.this, "password", "");
            login_username.getEditText().setText(name);
            login_password.getEditText().setText(password);
        }
    }

    private void getToLogin(){
        final String name = login_username.getEditText().getText().toString().trim();
        final String password = login_password.getEditText().getText().toString().trim();

        View focusView = null;
        boolean toLogin = true;
        if (TextUtils.isEmpty(name)){
            login_username.setError("用户名不能为空");
            focusView = login_username;
            toLogin = false;
        }else if (TextUtils.isEmpty(password)){
            login_password.setError("密码不能为空");
            focusView = login_password;
            toLogin = false;
        }
        if (!toLogin){
            focusView.requestFocus();
        }





//        String url = Mark.getServerIp()+"/apilogin";
//        Map<String,Object> param = new HashMap<String,Object>();
//        param.put("userName",name);
//        param.put("passWord",password);
//        aq.transformer(new MapTransformer()).ajax(url,param,Map.class,new AjaxCallback<Map>(){
//            @Override
//            public void callback(String url, Map result, AjaxStatus status) {
//                if (result!=null){
//                    boolean loginResult = Boolean.parseBoolean(result.get("result") + "");
//                    if (loginResult){
//
//
//
//                        isRemeber = re_password.isChecked();
//
//                        if (isRemeber){
//                            SharePreferenceUtil.setValue(Login.this, "name", name);
//                            SharePreferenceUtil.setValue(Login.this, "password", password);
//                        }
//                        SharePreferenceUtil.setValue(Login.this, "isRemeber", isRemeber);
//
//                        DataHolder dataHolder = DataHolder.getInstance();
//                        dataHolder.setBasicHandle(new BasicHandle(name, password));
////                        startActivity(new Intent(Login.this, MainTabActivity.class));
//                    }else{
//                        ToastUtils.showToast(Login.this, result.get("error") + "");
//                    }
//                }else {
//                    ToastUtils.showToast(Login.this,"登录失败:"+status.getError());
//                }
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

//        UpdateManager updateManager = new UpdateManager(Login.this);
//        updateManager.checkUpdate();
    }
}
