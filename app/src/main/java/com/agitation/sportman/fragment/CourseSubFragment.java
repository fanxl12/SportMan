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
import com.agitation.sportman.activity.CourseList;
import com.agitation.sportman.adapter.CourseAdapter;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

/**
 * Created by fanwl on 2015/11/24.
 */
public class CourseSubFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    private ListView course_sub_listview;
    private CourseAdapter courseAdapter;
    private List<Map<String,Object>> parentCatalogsSubList;
    private View rootView;
    private String parentCatalogId;
    private BGARefreshLayout mRefreshLayout;
    private AQuery aq;
    private DataHolder dataHolder;
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
            rootView = inflater.inflate(R.layout.course_sub_fragment, container, false);
            parentCatalogId = getArguments().getString("parentCatalogId");
            initVarble();
            initView();
            processLogic();
            getparentCatalogsSubList();
        }
        return rootView;
    }

    private void initView() {

        mRefreshLayout = (BGARefreshLayout) rootView.findViewById(R.id.rl_listview_refresh);
        course_sub_listview = (ListView) rootView.findViewById(R.id.course_sub_listview);
        course_sub_listview.setAdapter(courseAdapter);

        course_sub_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent catalogIntent = new Intent(getActivity(), CourseList.class);
                catalogIntent.putExtra("childCatalogId",parentCatalogsSubList.get(position).get("id")+"");
                catalogIntent.putExtra("childCatalogName",parentCatalogsSubList.get(position).get("name")+"");
                startActivity(catalogIntent);
            }
        });
    }
    private void initVarble() {
        aq = new AQuery(getActivity());
        dataHolder = DataHolder.getInstance();
        parentCatalogsSubList = new ArrayList<>();
        courseAdapter = new CourseAdapter(getActivity(), parentCatalogsSubList, R.layout.course_item);
    }

    protected void processLogic() {
        BGAStickinessRefreshViewHolder stickinessRefreshViewHolder = new BGAStickinessRefreshViewHolder(getActivity(), false);
        stickinessRefreshViewHolder.setStickinessColor(R.color.colorPrimary);
        stickinessRefreshViewHolder.setRotateImage(R.mipmap.bga_refresh_stickiness);
        mRefreshLayout.setRefreshViewHolder(stickinessRefreshViewHolder);
        mRefreshLayout.setDelegate(this);
    }

    public void getparentCatalogsSubList(){
        String url = Mark.getServerIp()+"/api/v1/course/getCourseChildCatalog";
        Map<String,Object> param = new HashMap<>();
        param.put("parentCatalogId", parentCatalogId);
        mActivity.showLoadingDialog();
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle()).ajax(url, param, Map.class,
                new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                mActivity.dismissLoadingDialog();
                if (info != null) {
                    if (Boolean.parseBoolean(info.get("result") + "")) {
                        Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                        parentCatalogsSubList = (List<Map<String, Object>>) retData.get("childCatalogs");
                        courseAdapter.setData(parentCatalogsSubList);
                        if (isAutomaticRefresh){
                            refreshHandler.sendEmptyMessageDelayed(Mark.DATA_REFRESH_SUCCEED, 1000);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        isAutomaticRefresh = true;
        getparentCatalogsSubList();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        return false;
    }
}
