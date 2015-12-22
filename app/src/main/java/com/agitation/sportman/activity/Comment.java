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
    private TextView commentNum;
    private String courseId;
    private double score= 5.0;
    private EditText et_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment);
        courseId = getIntent().getStringExtra("courseId");
        initView();

    }

    private void initView() {
        et_content = (EditText) findViewById(R.id.et_commnt_content);
        findViewById(R.id.bt_commint).setOnClickListener(this);
        ratingBar = (RatingBar) findViewById(R.id.ratingbar);
        commentNum = (TextView) findViewById(R.id.comment_num);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                commentNum.setText(rating + "分");
                score = rating;
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
        String url = Mark.getServerIp()+ "/api/v1/advice/saveAdvice";
        Map<String, Object> param = new HashMap<>();
        param.put("content",content);
        param.put("score",score);
        param.put("courseId",courseId);
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle())
            .ajax(url, param, Map.class, new AjaxCallback<Map>() {
                @Override
                public void callback(String url, Map info, AjaxStatus status) {
                    if (info != null) {

                    }
                }
            });
    }
}
