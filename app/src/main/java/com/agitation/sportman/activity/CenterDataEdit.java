package com.agitation.sportman.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.ToastUtils;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fanwl on 2015/11/7.
 */
public class CenterDataEdit extends BaseActivity implements View.OnClickListener {

    private LinearLayout mycenter_edit_head,mycenter_edit_name,mycenter_edit_sex,mycenter_edit_age,mycenter_edit_phone,mycenter_edit_address;
    private AQuery aq;
    private DataHolder dataHolder;
    private TextView edit_sex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_data_edit);
        initToorbar();
        intView();
        initVarble();
    }

    private void intView() {
        edit_sex = (TextView) findViewById(R.id.edit_sex);
        mycenter_edit_head = (LinearLayout) findViewById(R.id.mycenter_edit_head);
        mycenter_edit_name = (LinearLayout) findViewById(R.id.mycenter_edit_name);
        mycenter_edit_sex = (LinearLayout) findViewById(R.id.mycenter_edit_sex);
        mycenter_edit_age = (LinearLayout) findViewById(R.id.mycenter_edit_age);
        mycenter_edit_phone = (LinearLayout) findViewById(R.id.mycenter_edit_phone);
        mycenter_edit_address = (LinearLayout) findViewById(R.id.mycenter_edit_address);
        mycenter_edit_head.setOnClickListener(this);
        mycenter_edit_name.setOnClickListener(this);
        mycenter_edit_sex.setOnClickListener(this);
        mycenter_edit_age.setOnClickListener(this);
        mycenter_edit_phone.setOnClickListener(this);
        mycenter_edit_address.setOnClickListener(this);
    }

    private void initVarble() {
        aq = new AQuery(this);
        dataHolder = DataHolder.getInstance();
    }

    private void initToorbar() {
        if (toolbar!=null){
            title.setText("资料编辑");
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mycenter_edit_head:
                break;
            case R.id.mycenter_edit_name:
                break;
            case R.id.mycenter_edit_sex:
                chooseSexDialog();
                break;
            case R.id.mycenter_edit_age:
                break;
            case R.id.mycenter_edit_phone:
                break;
            case R.id.mycenter_edit_address:
                break;
        }
    }

    public void chooseSexDialog(){
        final String[] sex = new String[]{"男","女","保密"};
        AlertDialog.Builder sexDialog = new AlertDialog.Builder(this);
        sexDialog.setTitle("请选择性别");
        sexDialog.setItems(sex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ToastUtils.showToast(CenterDataEdit.this,"选择的是："+sex[i]);
                Map<String,Object> param = new HashMap<String, Object>();
                param.put("updateSex",sex[i]);
                chooseSex(param);

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        sexDialog.show();
    }

    /**
     * 选择性别
     */
    public void chooseSex(Map<String,Object> param){
        String url = Mark.getServerIp()+"/baseApi/updateUser";
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle()).ajax(url,param,Map.class,new AjaxCallback<Map>(){
            @Override
            public void callback(String url, Map result, AjaxStatus status) {
                if (result!=null){
                    boolean isResult = Boolean.parseBoolean(result.get("result")+"");
                    if (isResult){
                        ToastUtils.showToast(CenterDataEdit.this,"修改成功");
//                        edit_sex.setText("");
                    }
                }
            }
        });
    }

}
