package com.agitation.sportman.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.agitation.sportman.R;
import com.agitation.sportman.adapter.CourseAdapter;
import com.agitation.sportman.adapter.ImageAdapter;
import com.agitation.sportman.widget.CircleFlowIndicator;
import com.agitation.sportman.widget.ViewFlow;
import com.androidquery.AQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanwl on 2015/10/25.
 */
public class Course extends Fragment {

    private View rootView;
    private ViewFlow course_viewFlow;
    private List<Map<String,Object>> courseList;
    private ListView course_lv;
    private CourseAdapter courseAdapter;
    private AQuery aq;
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
        }
        return rootView;
    }

    private void initVarble() {
        courseList = new ArrayList<Map<String,Object>>();
        for (int i =0;i<10;i++){
            Map<String,Object> item = new HashMap<String,Object>();
            int count = i % 2 ;
            if (count==0){
                item.put("ImaSre","http://img3.imgtn.bdimg.com/it/u=278794782,4012596141&fm=21&gp=0.jpg");
                item.put("name","羽毛球"+i);
            }else {
                item.put("ImaSre","http://pic26.nipic.com/20130121/432252_180251136000_2.jpg");
                item.put("name","足球"+i);
            }
            courseList.add(item);
        }
        courseAdapter = new CourseAdapter(getActivity(),courseList);
        course_lv.setAdapter(courseAdapter);
    }

    private void initView() {
        course_lv = (ListView) rootView.findViewById(R.id.course_lv);
        course_viewFlow = (ViewFlow) rootView.findViewById(R.id.product_viewFlow);
        final CircleFlowIndicator indic = (CircleFlowIndicator)rootView.findViewById(R.id.product_dot);
        course_viewFlow.setFlowIndicator(indic);
        //这个是设置轮播图片资源
        List<Map<String, Object>> imageList = new ArrayList<Map<String,Object>>();
        Map<String, Object> img1 = new HashMap<String, Object>();
        img1.put("ImgSrc", "http://pic24.nipic.com/20121009/9998177_124716564000_2.jpg");
        imageList.add(img1);

        Map<String, Object> img2 = new HashMap<String, Object>();
        img2.put("ImgSrc", "http://pic1.nipic.com/2008-12-09/2008129182038882_2.jpg");
        imageList.add(img2);

        //设置商品图片轮播
        course_viewFlow.setAdapter(new ImageAdapter(getActivity(), imageList));
        course_viewFlow.setmSideBuffer(imageList.size()); // 实际图片张数
    }
}
