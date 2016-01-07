package com.agitation.sportman.activity;

import android.annotation.SuppressLint;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UmengUpdateAgent.update(this);
        setContentView(R.layout.main_tab_activity);
        initView();
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
    @SuppressLint("InflateParams")
    private View getTabItemView(int index) {
        View view = inflater.inflate(R.layout.tab_item_view, null);
        TextView tab_item_text = (TextView) view.findViewById(R.id.tab_item_text);
        tab_item_text.setText(mTextviewArray[index]);
        ImageView tab_item_icon = (ImageView) view.findViewById(R.id.tab_item_icon);
        tab_item_icon.setBackgroundResource(iconS[index]);
        return view;
    }


}
