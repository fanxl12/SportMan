package com.agitation.sportman.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.adapter.CollectionAdapter;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.ToastUtils;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

/**
 * Created by fanwl on 2015/12/4.
 */
public class Collection extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate  {

    private ListView collection_list;
    private CollectionAdapter collectionAdapter;
    private List<Map<String, Object>> collectionList;
    private boolean isAutomaticRefresh = false;
    private BGARefreshLayout mRefreshLayout;

    private Handler refreshHandler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==120){
                mRefreshLayout.endRefreshing();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection);
        initToolbar();
        initVarible();
        initView();
        processLogic();
        getCollectionInfo();

    }

    private void initToolbar() {
        if (toolbar!=null){
            title.setText("我的收藏");
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

    private void initVarible() {
        collectionList = new ArrayList<>();
        collectionAdapter = new CollectionAdapter(collectionList, this);
    }

    private void initView() {
        mRefreshLayout = (BGARefreshLayout)findViewById(R.id.rl_listview_refresh);
        collection_list = (ListView) findViewById(R.id.collection_list);
        collection_list.setAdapter(collectionAdapter);
        collection_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Collection.this, CourseDetail.class);
                intent.putExtra("courseId", collectionList.get(position).get("id") + "");
                startActivity(intent);
            }
        });
        collectionAdapter.setOnIconClickListener(new CollectionAdapter.OnIconClickListener() {
            @Override
            public void onIconClickListener(Map<String, Object> item, int position) {
                deleteCollection(item.get("id") + "", position);
            }
        });
    }

    protected void processLogic() {
        BGAStickinessRefreshViewHolder stickinessRefreshViewHolder = new BGAStickinessRefreshViewHolder(this, false);
        stickinessRefreshViewHolder.setStickinessColor(R.color.colorPrimary);
        stickinessRefreshViewHolder.setRotateImage(R.mipmap.bga_refresh_stickiness);
        mRefreshLayout.setRefreshViewHolder(stickinessRefreshViewHolder);
        mRefreshLayout.setDelegate(this);
    }

    //取消收藏
    public void deleteCollection(String collectionId,final int position){
        String url = Mark.getServerIp() + "/api/v1/collect/deleteCourse";
        Map<String, Object> param = new HashMap<>();
        param.put("collectionId", collectionId);
        showLoadingDialog();
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle())
                .ajax(url, param, Map.class, new AjaxCallback<Map>() {
                    @Override
                    public void callback(String url, Map info, AjaxStatus status) {
                        dismissLoadingDialog();
                        if (info != null) {
                            if (Boolean.parseBoolean(info.get("result") + "")) {
                                ToastUtils.showToast(Collection.this, "取消收藏成功");
                                collectionList.remove(position);
                                collectionAdapter.setCollectionList(collectionList);
                            }
                        }
                    }
                });
    }

    /**
     * 获取收藏列表
     */
    public void getCollectionInfo(){
        String url = Mark.getServerIp() + "/api/v1/collect/getCollectList";
        showLoadingDialog();
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle()).
            ajax(url, Map.class, new AjaxCallback<Map>() {
                @Override
                public void callback(String url, Map info, AjaxStatus status) {
                    dismissLoadingDialog();
                    if (info != null) {
                        if (Boolean.parseBoolean(info.get("result") + "")) {
                            Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                            collectionList = (List<Map<String, Object>>) retData.get("collects");
//                            ListAnimation();
                            collectionAdapter.setCollectionList(collectionList);
                            if (isAutomaticRefresh) {
                                refreshHandler.sendEmptyMessageDelayed(Mark.DATA_REFRESH_SUCCEED, 2000);
                            }
                        }
                    }
                }
            });
    }

//    private void ListAnimation(View view){
//        Animation animation = (Animation) AnimationUtils.loadAnimation(
//                this, R.anim.list_anim);
//        LayoutAnimationController lac = new LayoutAnimationController(animation);
//        lac.setDelay(0.2f);  //设置动画间隔时间
//        lac.setOrder(LayoutAnimationController.ORDER_NORMAL); //设置列表的显示顺序
//        collection_list.setLayoutAnimation(lac);
//    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        isAutomaticRefresh = true;
        getCollectionInfo();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        return false;
    }
}
