package com.agitation.sportman.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.agitation.sportman.R;
import com.agitation.sportman.activity.CourseSubCatalog;
import com.agitation.sportman.adapter.CourseAdapter;
import com.agitation.sportman.adapter.ImageAdapter;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.ToastUtils;
import com.agitation.sportman.widget.CircleFlowIndicator;
import com.agitation.sportman.widget.ViewFlow;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fanwl on 2015/10/25.
 */
public class Course extends Fragment {

    private View rootView;
    private ViewFlow course_viewFlow;
    private ListView course_lv;
    private CourseAdapter courseAdapter;
    private List<Map<String,Object>> parentCatalogsList;
    private AQuery aq;
    private DataHolder dataHolder;
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
                ToastUtils.showToast(getContext(), parentCatalogsList.get(position).get("name").toString());
                Intent intent = new Intent(getContext(), CourseSubCatalog.class);
                intent.putExtra("parentCatalogId",parentCatalogsList.get(position).get("id")+"");
                startActivity(intent);
            }
        });
    }

    private void initView() {
        course_lv = (ListView) rootView.findViewById(R.id.course_lv);
        course_viewFlow = (ViewFlow) rootView.findViewById(R.id.product_viewFlow);
        final CircleFlowIndicator indic = (CircleFlowIndicator)rootView.findViewById(R.id.product_dot);
        course_viewFlow.setFlowIndicator(indic);
    }
    /*
    获取课程首页广告和课程的数据
     */
    public void CourseParentCatalog(){
        String url = Mark.getServerIp()+"/api/v1/course/getCourseParentCatalog";
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle()).ajax(url, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                if (info!=null){
                boolean result = Boolean.parseBoolean(info.get("result")+"");
                if (result){
                    Map<String,Object> retData = (Map<String, Object>) info.get("retData");
                    parentCatalogsList = (List<Map<String, Object>>) retData.get("parentCatalogs");
                    List<Map<String,Object>> adversitementsList = (List<Map<String, Object>>) retData.get("adversitements");
                    courseAdapter.setCourse(parentCatalogsList);
                    course_viewFlow.setAdapter(new ImageAdapter(getActivity(), adversitementsList));
                    course_viewFlow.setmSideBuffer(adversitementsList.size());
                }
            }
            }
        });
    }
}
