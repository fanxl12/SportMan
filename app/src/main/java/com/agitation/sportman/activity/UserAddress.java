package com.agitation.sportman.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
 * Created by fanwl on 2015/11/22.
 */
public class UserAddress extends BaseActivity {

    private ListView lv_address;
    private TextView text_address;
    private EditText et_address;
    private  String[] address;
    private String addressArea;
    private String completeAddress;
    private AQuery aq;
    private DataHolder dataHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_address);
        initToorbar();
        initView();
        initVarible();
    }

    private void initVarible() {
        aq = new AQuery(this);
        dataHolder = DataHolder.getInstance();
    }

    private void initView() {
        lv_address = (ListView)findViewById(R.id.lv_address);
        text_address = (TextView)findViewById(R.id.text_address);
        et_address = (EditText)findViewById(R.id.et_address);
        et_address.addTextChangedListener(new AddressTextWatch());
        address= new String[]{"黄浦区","虹口区","杨浦区","闸北区","普陀区","长宁区","静安区",
                "徐汇区","浦东新区","闵行区","奉贤区","金山区","松江区","青浦区","嘉定区","宝山区","崇明县"};
        ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,address);
        lv_address.setAdapter(addressAdapter);

        findViewById(R.id.bt_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_address.getText())){
                    ToastUtils.showToast(UserAddress.this,"请输入一个详细地址");
                }else {
                    ToastUtils.showToast(UserAddress.this,completeAddress);
                    modiftAddress();
                }
            }
        });
        lv_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                et_address.setText("");
                text_address.setText("上海市" + address[position]);
                addressArea = "上海市" + address[position];
            }
        });

    }

    public void modiftAddress(){
        String url = Mark.getServerIp()+"/baseApi/updateUser";
        Map<String,Object> param = new HashMap<>();
        param.put("action","updateAddress");
        param.put("address", completeAddress);
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle()).
                ajax(url, param, Map.class, new AjaxCallback<Map>() {
                    @Override
                    public void callback(String url, Map info, AjaxStatus status) {
                        if (info!=null){
                            if (Boolean.parseBoolean(info.get("result")+"")){
                                ToastUtils.showToast(UserAddress.this,"修改成功");
                                dataHolder.getUserData().put("address", completeAddress);
                                UserAddress.this.setResult(UserInfoEdit.CHOOSE_FROM_ADDRESS);
                                finish();
                            }
                        }
                    }
                });
    }

    private void initToorbar() {
        if (toolbar!=null){
            title.setText("选择地址");
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

    class AddressTextWatch implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
        @Override
        public void afterTextChanged(Editable s) {
            String et_address = s.toString();
            text_address.setText(addressArea+et_address);
            completeAddress=addressArea+et_address;
        }
    }
}
