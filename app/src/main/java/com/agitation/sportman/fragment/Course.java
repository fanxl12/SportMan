package com.agitation.sportman.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.agitation.sportman.BaseFragment;
import com.agitation.sportman.R;
import com.agitation.sportman.activity.CourseSubCatalog;
import com.agitation.sportman.adapter.CourseAdapter;
import com.agitation.sportman.entity.DataEngine;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

/**
 * Created by fanwl on 2015/10/25.
 */
public class Course extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    private View rootView;
    private ListView course_lv;
    private CourseAdapter courseAdapter;
    private List<Map<String,Object>> parentCatalogsList;
    private AQuery aq;
    private DataHolder dataHolder;
    private BGARefreshLayout mRefreshLayout;
    private static final int COURSE_REFRESH_SUCCEED = 120;
    private boolean isAutomaticRefresh = false;


    private Handler refreshHandler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==120){
                mRefreshLayout.endRefreshing();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView !=null){
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent!=null)parent.removeView(rootView);
        }else {
            rootView = inflater.inflate(R.layout.course,container,false);
            initView();
            initVarble();
            processLogic();
            CourseParentCatalog();
        }
        return rootView;
    }

    private void initVarble() {
        dataHolder = DataHolder.getInstance();
        aq = new AQuery(getContext());
        parentCatalogsList = new ArrayList<Map<String,Object>>();
        courseAdapter = new CourseAdapter(getActivity(),parentCatalogsList);
        course_lv.setAdapter(courseAdapter);
        course_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), CourseSubCatalog.class);
                intent.putExtra("parentCatalogId",parentCatalogsList.get(position).get("id")+"");
                intent.putExtra("subTitle",parentCatalogsList.get(position).get("name")+"");
                startActivity(intent);
            }
        });
    }

    private void initView() {
        mRefreshLayout = (BGARefreshLayout) rootView.findViewById(R.id.rl_listview_refresh);
        course_lv = (ListView) rootView.findViewById(R.id.course_lv);
    }

    protected void processLogic() {
        BGAStickinessRefreshViewHolder stickinessRefreshViewHolder = new BGAStickinessRefreshViewHolder(getActivity(), false);
        stickinessRefreshViewHolder.setStickinessColor(R.color.colorPrimary);
        stickinessRefreshViewHolder.setRotateImage(R.mipmap.bga_refresh_stickiness);
        mRefreshLayout.setRefreshViewHolder(stickinessRefreshViewHolder);
        mRefreshLayout.setCustomHeaderView(DataEngine.getCustomHeaderView(getContext()), true);
        mRefreshLayout.setDelegate(this);
    }

    /*
    获取课程首页广告和课程的数据
     */
    public void CourseParentCatalog(){
        if (!isAutomaticRefresh)mActivity.showLoadingDialog();
        String url = Mark.getServerIp()+"/api/v1/course/getCourseParentCatalog";
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle()).ajax(url, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                if (!isAutomaticRefresh)mActivity.dismissLoadingDialog();
                if (info!=null){
                if (Boolean.parseBoolean(info.get("result")+"")){
                    Map<String,Object> retData = (Map<String, Object>) info.get("retData");
                    dataHolder.setImageProfix(retData.get("imageProfix") + "");
                    parentCatalogsList = (List<Map<String, Object>>) retData.get("parentCatalogs");
                    List<Map<String,Object>> adversitementsList = (List<Map<String, Object>>) retData.get("adversitements");
                    courseAdapter.setCourse(parentCatalogsList);
                    if (isAutomaticRefresh) {
                        refreshHandler.sendEmptyMessage(COURSE_REFRESH_SUCCEED);
                    }
                }
            }
            }
        });
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        isAutomaticRefresh = true;
        CourseParentCatalog();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        return false;
    }
}
