package com.agitation.sportman.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioGroup;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.fragment.CourseSubFragment;
import com.agitation.sportman.fragment.PublicCourseFragment;

/**
 * Created by fanwl on 2015/11/23.
 */
public class CourseSubCatalog extends BaseActivity{


    private CourseSubFragment courseSubFragment;
    private PublicCourseFragment publicCourseFragment;

    private FragmentManager fragmentManager;
    private String parentCatalogId, subTitle;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_sub);
        Intent intent = getIntent();
        parentCatalogId = intent.getStringExtra("parentCatalogId");
        subTitle = intent.getStringExtra("subTitle");
        initToolbar();
        initVarible();
        initView();
        fragmentManager = getSupportFragmentManager();
        setTabSelection(0);
    }

    private void initToolbar() {
        if (toolbar!=null){
            title_select_rg.setVisibility(View.VISIBLE);
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

    private void initVarible() {
        bundle = new Bundle();
    }

    private void initView() {
        title_select_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View checkView = title_select_rg.findViewById(checkedId);
                if (!checkView.isPressed()) {
                    return;
                }
                switch (checkedId) {
                    case R.id.title_rb_left:
                        setTabSelection(0);
                        break;
                    case R.id.title_rb_right:
                        setTabSelection(1);
                        break;
                }
            }
        });

        title_select_rg.check(R.id.title_rb_left);
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     */
    private void setTabSelection(int index){
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index)
        {
            case 0:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                if (courseSubFragment == null)
                {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    courseSubFragment = new CourseSubFragment();
                    bundle.putString("parentCatalogId",parentCatalogId);
                    courseSubFragment.setArguments(bundle);
                    transaction.add(R.id.id_course, courseSubFragment);
                } else
                {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(courseSubFragment);
                }
                break;
            case 1:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                if (publicCourseFragment == null)
                {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    publicCourseFragment = new PublicCourseFragment();
                    transaction.add(R.id.id_course, publicCourseFragment);
                } else
                {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(publicCourseFragment);
                }
                break;
        }
        transaction.commit();
    }


    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction
     *            用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction)
    {
        if (courseSubFragment != null)
        {
            transaction.hide(courseSubFragment);
        }
        if (publicCourseFragment != null)
        {
            transaction.hide(publicCourseFragment);
        }
    }

}
