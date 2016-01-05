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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanwl on 2015/10/25.
 */
public class Match extends Fragment {

    private View rootView;
    private ViewFlow course_viewFlow;
    private List<Map<String,Object>> courseList;
    private ListView course_lv;
    private CourseAdapter courseAdapter;

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
                item.put("ImaSre","http://pica.nipic.com/2008-02-23/200822315382790_2.jpg");
                item.put("name","羽毛球"+i);
            }else {
                item.put("ImaSre","http://img1.3lian.com/img2008/sport/1/18.jpg");
                item.put("name","足球"+i);
            }
            courseList.add(item);
        }
        courseAdapter = new CourseAdapter(getActivity(), courseList, R.layout.course_item);
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
        img1.put("ImgSrc", "http://img4.imgtn.bdimg.com/it/u=1519979105,1747027397&fm=21&gp=0.jpg");
        imageList.add(img1);

        Map<String, Object> img2 = new HashMap<String, Object>();
        img2.put("ImgSrc", "http://v1.qzone.cc/avatar/201407/26/12/45/53d3327c29bb4880.jpg%21200x200.jpg");
        imageList.add(img2);

        //设置商品图片轮播
        course_viewFlow.setAdapter(new ImageAdapter(getActivity(), imageList));
        course_viewFlow.setmSideBuffer(imageList.size()); // 实际图片张数
    }

}
