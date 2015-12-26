package com.agitation.sportman.activity;

import android.os.Bundle;
import android.view.View;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;

/**
 * Created by fanwl on 2015/12/25.
 */
public class CommentList extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_list);
        initToolbar();
    }

    private void initToolbar() {
        if (toolbar!=null){
            title.setText("评论列表");
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
}
