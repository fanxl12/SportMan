package com.agitation.sportman.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.adapter.CommonAdapter;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.ViewHolder;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

/**
 * Created by Fanxl on 2016/1/30.
 */
public class PastCourse extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate{

    private BGARefreshLayout past_refresh;
    private ListView past_course_lv;

    private int pageNumber = 1;
    private final int PAGE_SIZE = 10;
    private boolean isRefreshing = false;
    private boolean isLoading = false;
    private Map<String, Object> param;
    private List<Map<String, Object>> datas;
    private PastAdater adater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.past_course);
        initToolBar();
        initView();
        initVarible();
        processLogic();
        getOpenCourse();

    }

    private void initVarible() {
        param = new HashMap<>();
        param.put("pageSize", PAGE_SIZE);

        datas = new ArrayList<>();

        adater = new PastAdater(this, datas, R.layout.past_course_item);
        past_course_lv.setAdapter(adater);
        past_course_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void initView() {
        past_refresh = (BGARefreshLayout)findViewById(R.id.past_refresh);
        past_course_lv = (ListView)findViewById(R.id.past_course_lv);
    }

    protected void processLogic() {
        BGAStickinessRefreshViewHolder stickinessRefreshViewHolder = new BGAStickinessRefreshViewHolder(this, true);
        stickinessRefreshViewHolder.setStickinessColor(R.color.colorPrimary);
        stickinessRefreshViewHolder.setRotateImage(R.mipmap.bga_refresh_stickiness);
        past_refresh.setRefreshViewHolder(stickinessRefreshViewHolder);
        past_refresh.setDelegate(this);
    }

    private void initToolBar() {
        if (toolbar!=null){
            title.setText("往期公开课");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        if (!isRefreshing){
            isRefreshing = true;
            pageNumber = 1;
            getOpenCourse();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (!isLoading){
            pageNumber++;
            isLoading = true;
            getOpenCourse();
            return true;
        }
        return false;
    }

    //获取公开课列表
    private void getOpenCourse(){
        if (!isRefreshing  && !isLoading){
            showLoadingDialog();
        }
        String url = Mark.getServerIp()+ "/api/v1/course/getPastOpenCourse";
        param.put("pageNumber", pageNumber);
        aq.transformer(new MapTransformer()).ajax(url, param, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                if (info != null) {
                    Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                    List<Map<String, Object>> courses = (List<Map<String, Object>>) retData.get("courseList");
                    if (courses!=null){
                        if (isLoading){
                            datas.addAll(courses);
                        }else {
                            datas = courses;
                        }
                        adater.setData(datas);
                    }
                }

                if (isRefreshing){
                    isRefreshing = false;
                    past_refresh.endRefreshing();
                }else if(isLoading){
                    isLoading = false;
                    past_refresh.endLoadingMore();
                }else{
                    dismissLoadingDialog();
                }
            }
        });
    }

    class PastAdater extends CommonAdapter<Map<String, Object>>{

        public PastAdater(Context context, List<Map<String, Object>> mDatas, int itemLayoutId) {
            super(context, mDatas, itemLayoutId);
        }

        @Override
        public void convert(ViewHolder helper, Map<String, Object> item) {
            helper.setText(R.id.past_tv_name, item.get("name")+"");
            helper.setText(R.id.past_tv_address, item.get("address")+"");
            helper.setText(R.id.past_tv_score, item.get("totalScore")+"");
            helper.setText(R.id.past_tv_price, "￥"+item.get("price"));
        }
    }
}
