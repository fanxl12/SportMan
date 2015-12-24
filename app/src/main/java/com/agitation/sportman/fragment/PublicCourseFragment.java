package com.agitation.sportman.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.agitation.sportman.R;
import com.agitation.sportman.adapter.CourseCatalogAdapter;
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

/**
 * Created by fanwl on 2015/11/24.
 */
public class PublicCourseFragment extends Fragment {

    private View rootView;
    private ListView public_course_lv;
    private List<Map<String,Object>> courseCatalogInfoList;
    private CourseCatalogAdapter courseCatalogAdapter;
    private AQuery aq;
    private DataHolder dataHolder;
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
           getOpenCourse();
       }
        return rootView;
    }

    private void initVarible() {
        aq = new AQuery(getActivity());
        dataHolder = DataHolder.getInstance();
        courseCatalogInfoList = new ArrayList<>();
        courseCatalogAdapter = new CourseCatalogAdapter(courseCatalogInfoList,getActivity());
    }

    private void initView() {
        public_course_lv = (ListView)rootView.findViewById(R.id.public_course_lv);
        public_course_lv.setAdapter(courseCatalogAdapter);
        public_course_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
            }
        });
        courseCatalogAdapter.setOnCollectionClickListener(new CourseCatalogAdapter.OnCollectionClickListener() {
            @Override
            public void onCollectionClickListener(Map<String, Object> item, int position) {

            }
        });
    }
    //获取公开课列表
    private void getOpenCourse(){
        String url = Mark.getServerIp()+ "/api/v1/course/getOpenCourse";
        Map<String, Object> param = new HashMap<>();
        param.put("pageNumber","1");
        param.put("pageSize","10");
        aq.transformer(new MapTransformer()).ajax(url, param, Map.class, new AjaxCallback<Map>(){
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                if (info!=null){
                    Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                    courseCatalogInfoList = (List<Map<String, Object>>) retData.get("courseList");
                    courseCatalogAdapter.setCourseCatalogInfo(courseCatalogInfoList);
                }
            }
        });
    }
}
