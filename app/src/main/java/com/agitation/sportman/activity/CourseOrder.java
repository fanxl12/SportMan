package com.agitation.sportman.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.fragment.CourseOrderList;
import com.agitation.sportman.inter.OrderNotice;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by fanwl on 2015/11/14.
 */
public class CourseOrder extends BaseActivity {

    public static final int STATUS_UNPAY = 0;
    public static final int STATUS_UNCONFIRM = 1;
    public static final int STATUS_UNUSER = 2;
    public static final int STATUS_UNADVICES = 3;
    public static final int STATUS_DONE = 4;

    private TabLayout tab_course_appointment;
    private ViewPager pager_course_appointment;
    private List<Integer> statusList = Arrays.asList(STATUS_UNPAY, STATUS_UNCONFIRM, STATUS_UNUSER, STATUS_UNADVICES, STATUS_DONE);
    private List<String> orderTitle = Arrays.asList("待支付", "待确认", "待出行","待评价", "已完成");
    private List<Fragment> mTabContents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_order);
        initToolbar();
        init();
        initVarible();
        getOrder();
    }

    private void initVarible() {
        for (Integer type : statusList){
            CourseOrderList courseOrderList = CourseOrderList.getInstance(statusList.get(type));
            mTabContents.add(courseOrderList);
        }
        setupViewPager(pager_course_appointment);
        pager_course_appointment.setOffscreenPageLimit(1);
        for (String title : orderTitle){
            tab_course_appointment.addTab(tab_course_appointment.newTab().setText(title));
        }
        tab_course_appointment.setupWithViewPager(pager_course_appointment);
    }

    private void initToolbar() {
        if (toolbar!=null){
            title.setText("课程订单");
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void getOrder(){
        showLoadingDialog();
        String url = Mark.getServerIp() + "/api/v1/order/getCourseOrderList";
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle())
                .ajax(url, Map.class, new AjaxCallback<Map>() {
                    @Override
                    public void callback(String url, Map info, AjaxStatus status) {
                        dismissLoadingDialog();
                        if (info != null) {
                            if (Boolean.parseBoolean(info.get("result") + "")) {
                                Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                                List<Map<String, Object>> courseOrderList = (List<Map<String, Object>>) retData.get("courseOrderList");
                                dataHolder.setOrderList(courseOrderList);
                                noticeDataChange();
                            }
                        }
                    }
                });
    }

    public void noticeDataChange(){
        for (int i=0; i<mTabContents.size(); i++){
            OrderNotice fragment = (OrderNotice) mTabContents.get(i);
            fragment.dataChange();
        }
    }

    private void init() {
        tab_course_appointment = (TabLayout) findViewById(R.id.tab_course_appointment);
        pager_course_appointment = (ViewPager) findViewById(R.id.pager_course_appointment);
    }

    private void setupViewPager(ViewPager mViewPager) {
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        for (int i=0; i<orderTitle.size(); i++){
            pagerAdapter.addFragment(mTabContents.get(i), orderTitle.get(i));
        }
        mViewPager.setAdapter(pagerAdapter);
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
