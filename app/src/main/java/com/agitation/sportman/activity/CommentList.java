package com.agitation.sportman.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.adapter.CommentAdapter;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

/**
 * Created by fanwl on 2015/12/25.
 */
public class CommentList extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate {

    private ListView lv_comment_list;
    private List<Map<String, Object>> commentList;
    private String courseId;
    private CommentAdapter commentAdapter;
    private BGARefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_list);
        initToolbar();
        Intent intent = getIntent();
        courseId = intent.getStringExtra("courseId");
        initVarible();
        init();
        processLogic();
        getAdviceList();
    }

    private void initVarible() {
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList, this, false);
    }

    private void init() {
        mRefreshLayout = (BGARefreshLayout)findViewById(R.id.rl_listview_refresh);
        lv_comment_list = (ListView) findViewById(R.id.lv_comment_list);
        lv_comment_list.setAdapter(commentAdapter);

    }

    protected void processLogic() {
        BGAStickinessRefreshViewHolder stickinessRefreshViewHolder = new BGAStickinessRefreshViewHolder(this, false);
        stickinessRefreshViewHolder.setStickinessColor(R.color.colorPrimary);
        stickinessRefreshViewHolder.setRotateImage(R.mipmap.bga_refresh_stickiness);
        mRefreshLayout.setRefreshViewHolder(stickinessRefreshViewHolder);
        mRefreshLayout.setDelegate(this);
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

    private void getAdviceList(){
        String url = Mark.getServerIp() + "/api/v1/advice/getAdviceList";
        Map<String, Object> param = new HashMap<>();
        param.put("courseId",courseId);
        param.put("pageNumber","1");
        param.put("pageSize", "10");
        showLoadingDialog();
        aq.transformer(new MapTransformer()).ajax(url, param, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                dismissLoadingDialog();
                if (info != null) {
                    if (Boolean.parseBoolean(info.get("result") + "")) {
                        Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                        commentList = (List<Map<String, Object>>) retData.get("advices");
                        commentAdapter.setCommentList(commentList);
                    }
                }
            }
        });
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        mRefreshLayout.endRefreshing();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        return false;
    }
}
