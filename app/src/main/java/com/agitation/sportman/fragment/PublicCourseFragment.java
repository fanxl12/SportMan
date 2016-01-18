package com.agitation.sportman.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.agitation.sportman.BaseFragment;
import com.agitation.sportman.R;
import com.agitation.sportman.activity.CourseDetail;
import com.agitation.sportman.adapter.CourseListAdapter;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.ToastUtils;
import com.agitation.sportman.widget.ExpandTabView;
import com.agitation.sportman.widget.ScreenView;
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
public class PublicCourseFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    private View rootView;
    private ListView public_course_lv;
    private List<Map<String,Object>> courseCatalogInfoList;
    private CourseListAdapter courseCatalogAdapter;
    private AQuery aq;
    private DataHolder dataHolder;

    private ArrayList<View> mViewArrayTest = new ArrayList<>();
    private ExpandTabView expandtab_view;
    private ScreenView timeSv, typeSv, areaSv, sortSv;

    private BGARefreshLayout mRefreshLayout;

    private ScreenView.OnSelectListener selectTestListener = new ScreenView.OnSelectListener() {
        @Override
        public void getValue(Map<String, Object> param, ScreenView view) {
            onRefresh(view, param.get("name") + "");
        }
    };

    private int pageNumber;
    private final int PAGE_SIZE = 10;
    private boolean isRefreshing = false;
    private boolean isLoading = false;
    private Map<String, Object> param;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       if (rootView!=null){
           ViewGroup parent = (ViewGroup) rootView.getParent();
           if (parent!=null)parent.removeView(rootView);
       }else {
           rootView = inflater.inflate(R.layout.public_course_fragment,container,false);
           initVarible();
           initView();
           processLogic();
           getOpenCourse();
           getMenuData();
       }
        return rootView;
    }

    private void initMenuData(Map<String, Object> retData){
        List<Map<String, Object>> timeList = (List<Map<String, Object>>) retData.get("timeList");
        List<Map<String, Object>> typeList = (List<Map<String, Object>>) retData.get("courseTypes");
        List<Map<String, Object>> positionList = (List<Map<String, Object>>) retData.get("areas");
        List<Map<String, Object>> sortList = (List<Map<String, Object>>) retData.get("sortList");

        Map<String, Object> typeClear = new HashMap<>();
        typeClear.put("name", "全部类型");
        typeList.add(0, typeClear);

        timeSv = new ScreenView(getActivity(), timeList);
        typeSv = new ScreenView(getActivity(), typeList);
        areaSv = new ScreenView(getActivity(), positionList);
        sortSv = new ScreenView(getActivity(), sortList);

        timeSv.setOnSelectListener(selectTestListener);
        typeSv.setOnSelectListener(selectTestListener);
        areaSv.setOnSelectListener(selectTestListener);
        sortSv.setOnSelectListener(selectTestListener);

        mViewArrayTest.add(timeSv);
        mViewArrayTest.add(typeSv);
        mViewArrayTest.add(areaSv);
        mViewArrayTest.add(sortSv);

        ArrayList<String> mTextArray = new ArrayList<String>();
        mTextArray.add("时间");
        mTextArray.add("类型");
        mTextArray.add("区域");
        mTextArray.add("默认");

        expandtab_view.setValue(mTextArray, mViewArrayTest);
    }

    private void initVarible() {
        aq = mActivity.aq;
        dataHolder = mActivity.dataHolder;
        courseCatalogInfoList = new ArrayList<>();
        courseCatalogAdapter = new CourseListAdapter(getActivity(), courseCatalogInfoList, R.layout.catalog_item);
        param = new HashMap<>();
        param.put("pageSize", PAGE_SIZE);
    }

    protected void processLogic() {
        BGAStickinessRefreshViewHolder stickinessRefreshViewHolder = new BGAStickinessRefreshViewHolder(getActivity(), false);
        stickinessRefreshViewHolder.setStickinessColor(R.color.colorPrimary);
        stickinessRefreshViewHolder.setRotateImage(R.mipmap.bga_refresh_stickiness);
        mRefreshLayout.setRefreshViewHolder(stickinessRefreshViewHolder);
        mRefreshLayout.setDelegate(this);
    }

    private void initView() {

        mRefreshLayout = (BGARefreshLayout) rootView.findViewById(R.id.rl_listview_refresh);

        expandtab_view = (ExpandTabView) rootView.findViewById(R.id.expandtab_view_test);

        public_course_lv = (ListView)rootView.findViewById(R.id.public_course_lv);
        public_course_lv.setAdapter(courseCatalogAdapter);
        public_course_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CourseDetail.class);
                intent.putExtra("courseId", courseCatalogInfoList.get(position).get("id")+"");
                startActivity(intent);
            }
        });

        courseCatalogAdapter.setOnCollectionClickListener(new CourseListAdapter.OnCollectionClickListener() {
            @Override
            public void onCollectionClickListener(Map<String, Object> item, int position) {

                String courseId = item.get("id") + "";
                if (item.get("collectionId") == null) {
                    savaCollection(courseId, position);
                } else {
                    String collectionId = item.get("collectionId") + "";
                    deleteCollection(collectionId, position);
                }
                courseCatalogAdapter.notifyDataSetChanged();

            }
        });
    }

    /**
     * @param Id
     * @param position
     * 收藏操作
     */
    public void savaCollection(final String Id,final int position){
        String url = Mark.getServerIp() + "/api/v1/collect/save";
        Map<String, Object> param = new HashMap<>();
        param.put("courseId", Id);
        mActivity.showLoadingDialog();
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle())
                .ajax(url, param, Map.class, new AjaxCallback<Map>() {
                    @Override
                    public void callback(String url, Map info, AjaxStatus status) {
                        mActivity.dismissLoadingDialog();
                        if (info != null) {
                            if (Boolean.parseBoolean(info.get("result") + "")) {
                                ToastUtils.showToast(getActivity(), "收藏成功");
                                Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                                courseCatalogInfoList.get(position).put("collectionId", retData.get("collectionId") + "");
                                courseCatalogAdapter.setData(courseCatalogInfoList);
                            }
                        }
                    }
                });
    }

    /**
     * @param collectionId
     * @param position
     * 取消收藏操作
     */
    public void deleteCollection(String collectionId,final int position){
        String url = Mark.getServerIp() + "/api/v1/collect/deleteCourse";
        Map<String, Object> param = new HashMap<>();
        param.put("collectionId",collectionId);
        mActivity.showLoadingDialog();
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle())
                .ajax(url, param, Map.class, new AjaxCallback<Map>() {
                    @Override
                    public void callback(String url, Map info, AjaxStatus status) {
                        mActivity.dismissLoadingDialog();
                        if (info != null) {
                            if (Boolean.parseBoolean(info.get("result") + "")) {
                                ToastUtils.showToast(getActivity(), "取消收藏成功");
                                courseCatalogInfoList.get(position).remove("collectionId");
                                courseCatalogAdapter.setData(courseCatalogInfoList);
                            }
                        }
                    }
                });
    }


    //获取公开课列表
    private void getOpenCourse(){
        if (!isRefreshing && !isLoading){
            mActivity.showLoadingDialog();
        }
        String url = Mark.getServerIp()+ "/api/v1/course/getOpenCourse";
        param.put("pageNumber", pageNumber);
        aq.transformer(new MapTransformer()).ajax(url, param, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                if (isRefreshing){
                    mRefreshLayout.endRefreshing();
                }else if(isLoading){
                    isLoading = false;
                    mRefreshLayout.endLoadingMore();
                }else{
                    isRefreshing = false;
                    mActivity.dismissLoadingDialog();
                }
                if (info != null) {
                    Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                    courseCatalogInfoList = (List<Map<String, Object>>) retData.get("courseList");
                    courseCatalogAdapter.setData(courseCatalogInfoList);
                }
            }
        });
    }

    private void onRefresh(View view, String showText) {
        expandtab_view.onPressBack();
        int position = getPositon(view);
        if (position >= 0 && !expandtab_view.getTitle(position).equals(showText)) {
            expandtab_view.setTitle(showText, position);
        }
    }

    private int getPositon(View tView) {
        for (int i = 0; i < mViewArrayTest.size(); i++) {
            if (mViewArrayTest.get(i) == tView) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        isRefreshing = true;
        pageNumber = 1;
        getOpenCourse();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        pageNumber++;
        isLoading = true;
        getOpenCourse();
        return true;
    }

    /**
     * 获取筛选条件信息
     */
    private void getMenuData(){
        String url = Mark.getServerIp() + "/api/v1/course/getMenuData";
        showLoadingDialog();
        aq.transformer(new MapTransformer()).ajax(url, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                dismissLoadingDialog();
                if (info != null) {
                    if (Boolean.parseBoolean(info.get("result") + "")) {
                        Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                        initMenuData(retData);
                    }
                }
            }
        });
    }
}
