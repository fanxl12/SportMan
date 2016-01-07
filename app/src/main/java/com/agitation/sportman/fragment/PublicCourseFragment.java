package com.agitation.sportman.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
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
import com.agitation.sportman.widget.ViewLeft;
import com.agitation.sportman.widget.ViewMiddle;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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

    private ArrayList<View> mViewArray = new ArrayList<View>();
    private ExpandTabView expandTabView;
    private ViewMiddle timeChoiseView;
    private ViewLeft typeChoiseView;
    private ViewMiddle distanceChoiseView;
    private ViewLeft defaultChoiseView;
    private String[] items = new String[]{ "时间", "item2", "item3", "item4", "item5", "item6", "item7", "item8" };
    private String[] itemsVaule = new String[]{ "item1", "item2", "item3", "item4", "item5", "item6", "item7", "item8" };

    private ArrayList<String> groups = new ArrayList<String>();
    private SparseArray<LinkedList<String>> children = new SparseArray<LinkedList<String>>();
    private BGARefreshLayout mRefreshLayout;

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
           initListener();
       }
        return rootView;
    }

    private void initVarible() {

        for(int i=0;i<10;i++){
            groups.add(i+"行");
            LinkedList<String> tItem = new LinkedList<String>();
            for(int j=0;j<15;j++){

                tItem.add(i+"行"+j+"列");

            }
            children.put(i, tItem);
        }

        timeChoiseView = new ViewMiddle(getActivity(), groups,  children);
        typeChoiseView = new ViewLeft(getActivity(), items, itemsVaule);
        distanceChoiseView = new ViewMiddle(getActivity(), groups,  children);
        defaultChoiseView = new ViewLeft(getActivity(), items, itemsVaule);
        aq = new AQuery(getActivity());
        dataHolder = DataHolder.getInstance();
        courseCatalogInfoList = new ArrayList<>();
        courseCatalogAdapter = new CourseListAdapter(getActivity(), courseCatalogInfoList, R.layout.catalog_item);
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

        expandTabView = (ExpandTabView) rootView.findViewById(R.id.expandtab_view);

        mViewArray.add(timeChoiseView);
        mViewArray.add(typeChoiseView);
        mViewArray.add(distanceChoiseView);
        mViewArray.add(defaultChoiseView);
        ArrayList<String> mTextArray = new ArrayList<String>();
        mTextArray.add("时间");
        mTextArray.add("类型");
        mTextArray.add("区域");
        mTextArray.add("默认");
        expandTabView.setValue(mTextArray, mViewArray);
        expandTabView.setTitle(timeChoiseView.getShowText(), 0);
        expandTabView.setTitle(typeChoiseView.getShowText(), 1);
        expandTabView.setTitle(distanceChoiseView.getShowText(), 2);
        expandTabView.setTitle(defaultChoiseView.getShowText(), 3);


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
        String url = Mark.getServerIp()+ "/api/v1/course/getOpenCourse";
        Map<String, Object> param = new HashMap<>();
        param.put("pageNumber","1");
        param.put("pageSize","10");
        mActivity.showLoadingDialog();
        aq.transformer(new MapTransformer()).ajax(url, param, Map.class, new AjaxCallback<Map>(){
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                mActivity.dismissLoadingDialog();
                if (info!=null){
                    Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                    courseCatalogInfoList = (List<Map<String, Object>>) retData.get("courseList");
                    courseCatalogAdapter.setData(courseCatalogInfoList);
                }
            }
        });
    }

    private void initListener() {

        timeChoiseView.setOnSelectListener(new ViewMiddle.OnSelectListener() {

            @Override
            public void getValue(String showText) {

                onRefresh(timeChoiseView,showText);

            }
        });

        typeChoiseView.setOnSelectListener(new ViewLeft.OnSelectListener() {

            @Override
            public void getValue(String distance, String showText) {
                onRefresh(typeChoiseView, showText);
            }
        });

        distanceChoiseView.setOnSelectListener(new ViewMiddle.OnSelectListener() {

            @Override
            public void getValue(String showText) {

                onRefresh(distanceChoiseView,showText);

            }
        });

        defaultChoiseView.setOnSelectListener(new ViewLeft.OnSelectListener() {

            @Override
            public void getValue(String distance, String showText) {
                onRefresh(defaultChoiseView, showText);
            }
        });
    }

    private void onRefresh(View view, String showText) {

        expandTabView.onPressBack();
        int position = getPositon(view);
        if (position >= 0 && !expandTabView.getTitle(position).equals(showText)) {
            expandTabView.setTitle(showText, position);
        }
        ToastUtils.showToast(getActivity(), showText);

    }

    private int getPositon(View tView) {
        for (int i = 0; i < mViewArray.size(); i++) {
            if (mViewArray.get(i) == tView) {
                return i;
            }
        }
        return -1;
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
