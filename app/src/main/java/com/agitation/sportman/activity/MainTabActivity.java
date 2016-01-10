package com.agitation.sportman.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.fragment.Course;
import com.agitation.sportman.fragment.MyCenter;
import com.agitation.sportman.fragment.TestMatch;
import com.agitation.sportman.utils.Mark;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.umeng.update.UmengUpdateAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanwl on 2015/10/25.
 */
public class MainTabActivity extends BaseActivity {

    private FragmentTabHost main_tabhost;
    private LayoutInflater inflater;
    private Class<?> fragmentArray[] = {Course.class,TestMatch.class,MyCenter.class};
    private String mTextviewArray[] = {"课程", "比赛", "我的"};
    private int iconS[] = {R.drawable.course_icon, R.drawable.match_icon, R.drawable.mycenter_icon};

    //定位操作
    private LocationClient mLocClient;
    private MyLocationListenner myListener = new MyLocationListenner();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UmengUpdateAgent.update(this);
        setContentView(R.layout.main_tab_activity);
        initView();
        initVarible();
    }

    private void initVarible() {
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        //设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);        //是否打开GPS
        option.setCoorType("bd09ll");       //设置返回值的坐标类型。
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        option.setProdName("High运动"); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mLocClient.setLocOption(option);
        getLocation();
    }

    public void getLocation(){
        if(mLocClient == null)return;
        if (mLocClient.isStarted()){
            mLocClient.requestLocation();
        }else{
            mLocClient.start();
        }
    }

    private void initView() {
        inflater = LayoutInflater.from(this);
        main_tabhost = (FragmentTabHost)findViewById(R.id.main_tabhost);
        main_tabhost.setup(this, getSupportFragmentManager(), R.id.main_fragmentlayout);
        main_tabhost.getTabWidget().setDividerDrawable(null);
        //获取手机屏幕的宽度和高度
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        Mark.phoneWidth = (int)metric.widthPixels;
        Mark.phoneHeight = (int)metric.heightPixels;

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new Course());
        fragments.add(new TestMatch());
        fragments.add(new MyCenter());
        int count = fragmentArray.length;
        for (int i=0;i<count;i++){
            TabHost.TabSpec tabSpec =main_tabhost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            main_tabhost.addTab(tabSpec, fragmentArray[i], null);
        }
        main_tabhost.setCurrentTab(0);
        main_tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (mTextviewArray[2].equals(tabId)){
                    toolbar.setVisibility(View.GONE);
                }else{
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = inflater.inflate(R.layout.tab_item_view, null);
        TextView tab_item_text = (TextView) view.findViewById(R.id.tab_item_text);
        tab_item_text.setText(mTextviewArray[index]);
        ImageView tab_item_icon = (ImageView) view.findViewById(R.id.tab_item_icon);
        tab_item_icon.setBackgroundResource(iconS[index]);
        return view;
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null)return;
            dataHolder.setLongitude(location.getLongitude());
            dataHolder.setLatitude(location.getLatitude());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocClient != null && mLocClient.isStarted()) {
            mLocClient.stop();
            mLocClient = null;
        }
    }


}
