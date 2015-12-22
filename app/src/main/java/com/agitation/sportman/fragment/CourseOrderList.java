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
import com.agitation.sportman.activity.Comment;
import com.agitation.sportman.adapter.CourseOrderAdapter;
import com.agitation.sportman.inter.OrderNotice;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.ToastUtils;
import com.agitation.sportman.utils.UtilsHelper;
import com.agitation.sportman.widget.RefreshLayout;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fanwl on 2015/11/15.
 */
public class CourseOrderList extends Fragment implements OrderNotice {

    private View rootView;

    private List<Map<String, Object>> courseOrderList;
    private CourseOrderAdapter courseOrderAdapter;
    private RefreshLayout swipe_container;
    private ListView tickey_list_lv;
    private int status;
    private DataHolder dataHolder;
    private AQuery aq;
    public static final String STATUS_NAME_KEY = "STATUS_NAME_KEY";

    public static CourseOrderList getInstance(int status){
        Bundle bundle = new Bundle();
        bundle.putInt(STATUS_NAME_KEY, status);
        CourseOrderList courseOrderList = new CourseOrderList();
        courseOrderList.setArguments(bundle);
        return courseOrderList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView!=null){
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent!=null)parent.removeView(rootView);
        }else {
            rootView = inflater.inflate(R.layout.course_order_list, container, false);
            status = getArguments().getInt(STATUS_NAME_KEY);
            init();
            initVarible();
            getCourseOrderList();
        }
        return rootView;
    }

    private void initVarible() {
        aq = new AQuery(getContext());
        dataHolder = DataHolder.getInstance();
        courseOrderList = new ArrayList<>();
        courseOrderAdapter = new CourseOrderAdapter(getActivity(), status, courseOrderList, R.layout.courseorder_list_item);
        tickey_list_lv.setAdapter(courseOrderAdapter);
        tickey_list_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ToastUtils.showToast(getContext(),"我被点击了");
            }
        });

        courseOrderAdapter.setOnBtnClickListener(new CourseOrderAdapter.OnBtnClickListener() {
            @Override
            public void onBtnClickListener(Map<String, Object> item) {
                int status = Integer.parseInt(item.get("status")+"");
                if (status== Mark.ORDER_STATUS_UNPAY){
                    ToastUtils.showToast(getContext(),"支付");
                }else if (status== Mark.ORDER_STATUS_PAYED){
                }else if (status== Mark.ORDER_STATUS_UNADVICES){
                    Intent intent = new Intent(getContext(), Comment.class);
                        intent.putExtra("courseId",item.get("courseId")+"");
                    startActivity(intent);
                }if (status== Mark.ORDER_STATUS_DONE){

                }
            }
        });

    }

    //获取订单数据
    public void getCourseOrderList(){
        String url = Mark.getServerIp() + "/api/v1/order/getCourseOrderList";
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle())
                .ajax(url, Map.class, new AjaxCallback<Map>() {
                    @Override
                    public void callback(String url, Map info, AjaxStatus status) {
                    if (info != null) {
                        if (Boolean.parseBoolean(info.get("result") + "")) {
                            Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                            courseOrderList = (List<Map<String, Object>>) retData.get("courseOrderList");
                            selectedOrderData();
                        }
                    }
                    }
                });
    }

    private void init() {
        swipe_container = (RefreshLayout)rootView.findViewById(R.id.course_list_refresh);
        //设置动画颜色
        swipe_container.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        tickey_list_lv = (ListView)rootView.findViewById(R.id.course_list_lv);
    }

    public void selectedOrderData(){
        List<Map<String, Object>> orderList = UtilsHelper.selectMapList(courseOrderList, "get(:_currobj,'status') like '" + status + "'");
        courseOrderAdapter.setData(orderList);
    }

    @Override
    public void dataChange() {

    }
}
