package com.agitation.sportman.activity;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.agitation.sportman.R;
import com.agitation.sportman.fragment.CourseSubFragment;
import com.agitation.sportman.fragment.PublicCourseFragment;

/**
 * Created by fanwl on 2015/11/23.
 */
public class CourseSubCatalog extends AppCompatActivity implements View.OnClickListener {


    private CourseSubFragment courseSubFragment;
    private PublicCourseFragment publicCourseFragment;

    private TextView title_course,public_course;
    private android.app.FragmentManager fragmentManager;
    private String parentCatalogId;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_sub);
        Intent intent = getIntent();
        parentCatalogId = intent.getStringExtra("parentCatalogId");
        initVarible();
        initView();
        fragmentManager = getFragmentManager();
        setTabSelection(0);
    }

    private void initVarible() {
        bundle = new Bundle();
    }

    private void initView() {
        ImageView  back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        title_course = (TextView)findViewById(R.id.title_course);
        public_course = (TextView)findViewById(R.id.public_course);
        title_course.setOnClickListener(this);
        public_course.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_course:
                setTabSelection(0);
                break;
            case R.id.public_course:
                setTabSelection(1);
                break;
            case R.id.back:
                onBackPressed();
                break;
        }
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     */
    @SuppressLint("NewApi")
    private void setTabSelection(int index)
    {
        // 重置按钮
        resetBtn();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index)
        {
            case 0:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                title_course.setBackgroundResource(R.drawable.course_left_title_round);
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
                public_course.setBackgroundResource(R.drawable.course_right_title_round);
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
     * 清除掉所有的选中状态。
     */
    private void resetBtn()
    {
        title_course.setBackgroundResource(R.drawable.course_left_title_normal_round);
        public_course.setBackgroundResource(R.drawable.course_right_title_normal_round);
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction
     *            用于对Fragment执行操作的事务
     */
    @SuppressLint("NewApi")
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
