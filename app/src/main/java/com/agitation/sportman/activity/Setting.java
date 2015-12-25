package com.agitation.sportman.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.utils.UtilsHelper;

/**
 * Created by fanwl on 2015/12/21.
 */
public class Setting extends BaseActivity {

    private TextView version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        initToolbar();
        initView();

    }


    private void initToolbar() {
        if (toolbar!=null){

//            title.setText("我的收藏");
            linear_toobar.setVisibility(View.VISIBLE);
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
        version = (TextView) findViewById(R.id.version);
        version.setText(UtilsHelper.getAppVersion(this));
    }
}
