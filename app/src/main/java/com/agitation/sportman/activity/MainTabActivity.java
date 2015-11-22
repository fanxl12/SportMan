package com.agitation.sportman.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.fragment.Course;
import com.agitation.sportman.fragment.MyCenter;
import com.agitation.sportman.fragment.TestMatch;
import com.agitation.sportman.utils.DataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanwl on 2015/10/25.
 */
public class MainTabActivity extends BaseActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private DataHolder dataHolder=DataHolder.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab_activity);
        initToolbar();
        initView();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPage);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new Course());
        fragments.add(new TestMatch());
        fragments.add(new MyCenter());

        SampleFragmentPagerAdapter pagerAdapter = new SampleFragmentPagerAdapter(getSupportFragmentManager(),this,fragments);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        for (int i=0;i<tabLayout.getTabCount();i++){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab !=null){
                tab.setCustomView(pagerAdapter.getTabView(i));
            }
        }
        viewPager.setCurrentItem(0);
    }
    private void initToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter{

        private String[] tabTitles = new String[]{"课程","比赛","我的"};
        private int[] icons = new int[]{R.drawable.course,R.drawable.course,R.drawable.course};
        private Context context;
        private List<Fragment> fragments;

        public View getTabView(int position){
            View v = LayoutInflater.from(context).inflate(R.layout.course_tab,null);
            TextView  tv= (TextView) v.findViewById(R.id.textView);
            tv.setText(tabTitles[position]);
            ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
            imageView.setImageResource(icons[position]);
            return  v;
        }

        public SampleFragmentPagerAdapter(FragmentManager fm,Context context,List<Fragment> fragments) {
            super(fm);
            this.context=context;
            this.fragments=fragments;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
