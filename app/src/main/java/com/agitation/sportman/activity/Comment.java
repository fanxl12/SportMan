package com.agitation.sportman.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

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
 * Created by fanwl on 2015/12/4.
 */
public class Comment extends BaseActivity implements View.OnClickListener {

    private RatingBar ratingBar;
    private TextView commentNum, commnet_name, commnet_time, commnet_address;
    private String courseId, name, time, address;
    private double score= 5.0;
    private EditText et_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment);
        courseId = getIntent().getStringExtra("courseId");
        name = getIntent().getStringExtra("name");
        time = getIntent().getStringExtra("time");
        address = getIntent().getStringExtra("address");
        initToolbar();
        initView();
    }

    /**
     * toolbar初始化
     */
    private void initToolbar() {
        if (toolbar!=null){
            title.setText("课程评价");
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
        commnet_name = (TextView) findViewById(R.id.name);
        commnet_time = (TextView) findViewById(R.id.time);
        commnet_address = (TextView) findViewById(R.id.address);

        commnet_name.setText(name);
        commnet_time.setText(time);
        commnet_address.setText(address);
        et_content = (EditText) findViewById(R.id.et_commnt_content);
        findViewById(R.id.bt_commint).setOnClickListener(this);
        ratingBar = (RatingBar) findViewById(R.id.ratingbar);
        commentNum = (TextView) findViewById(R.id.comment_num);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                float ratNum = rating * 2;
                commentNum.setText(ratNum+"");
                score = ratNum;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_commint:
                String content = et_content.getText().toString().trim();
                if (TextUtils.isEmpty(content)){
                    ToastUtils.showToast(Comment.this, "评论内容不能为空");
                    return;
                }
                commintCommentContent(content);
                break;

        }
    }

    private void commintCommentContent(String content) {
        showLoadingDialog();
        String url = Mark.getServerIp()+ "/api/v1/advice/saveAdvice";
        Map<String, Object> param = new HashMap<>();
        param.put("content",content);
        param.put("score",score);
        param.put("courseId",courseId);
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle())
            .ajax(url, param, Map.class, new AjaxCallback<Map>() {
                @Override
                public void callback(String url, Map info, AjaxStatus status) {
                    dismissLoadingDialog();
                    if (info != null) {
                        if(Boolean.parseBoolean(info.get("result")+"")){
                            ToastUtils.showToast(Comment.this, "评论成功");
                            finish();
                        }
                    }
                }
        });
    }
}
