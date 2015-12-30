package com.agitation.sportman.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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

/**
 * Created by fanwl on 2015/11/24.
 */
public class CourseSubFragment extends Fragment {

    private ListView course_sub_listview;
    private CourseAdapter courseAdapter;
    private List<Map<String,Object>> parentCatalogsSubList;
    private View rootView;
    private String parentCatalogId;
    private AQuery aq;
    private DataHolder dataHolder;
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
            getparentCatalogsSubList(parentCatalogId);
        }
        return rootView;
    }

    private void initView() {
        course_sub_listview = (ListView) rootView.findViewById(R.id.course_sub_listview);
        course_sub_listview.setAdapter(courseAdapter);

        course_sub_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent catalogIntent = new Intent(getActivity(), CourseList.class);
                catalogIntent.putExtra("childCatalogId",parentCatalogsSubList.get(position).get("id")+"");
                startActivity(catalogIntent);
            }
        });
    }
    private void initVarble() {
        aq = new AQuery(getActivity());
        dataHolder = DataHolder.getInstance();
        parentCatalogsSubList = new ArrayList<>();
        courseAdapter = new CourseAdapter(getActivity(),parentCatalogsSubList);
    }
    public void getparentCatalogsSubList(String parentCatalogId){
        String url = Mark.getServerIp()+"/api/v1/course/getCourseChildCatalog";
        Map<String,Object> param = new HashMap<>();
        param.put("parentCatalogId", parentCatalogId);
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle()).ajax(url, param, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                if (info != null) {
                    if (Boolean.parseBoolean(info.get("result") + "")) {
                        Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                        parentCatalogsSubList = (List<Map<String, Object>>) retData.get("childCatalogs");
                        courseAdapter.setCourse(parentCatalogsSubList);
                    }
                }
            }
        });
    }

}
