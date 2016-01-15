package com.agitation.sportman;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agitation.sportman.utils.DataHolder;
import com.androidquery.AQuery;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by fanwl on 2015/10/25.
 */
public class BaseActivity extends AppCompatActivity {

    protected LinearLayout rootLayout, linear_toobar;
    protected Toolbar toolbar;
    protected TextView title, title_course, public_course,sourse_sub_subtitle;
    public AQuery aq;
    public DataHolder dataHolder;
    private SweetAlertDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 这句很关键，注意是调用父类的方法
        super.setContentView(R.layout.activity_base);
        dataHolder = DataHolder.getInstance();
        aq = new AQuery(this);
        // 经测试在代码里直接声明透明状态栏更有效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        initBaseToolbar();
    }

    private void initBaseToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        //测量actionBarHeight
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)){
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        //根据版本的不同，重新计算高度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int padingTopPx = getResources().getDimensionPixelOffset(R.dimen.toolbar_padding_top);
            Log.i("padingTopPx", padingTopPx+"");
            actionBarHeight += padingTopPx;
        }
        Log.i("actionBarHeight", actionBarHeight + "");

        //重新设置toolbar的高度
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
        layoutParams.height = actionBarHeight;
        toolbar.setLayoutParams(layoutParams);

        title = (TextView)toolbar.findViewById(R.id.base_tv_title);

        title_course = (TextView)toolbar.findViewById(R.id.title_course);
        sourse_sub_subtitle = (TextView)toolbar.findViewById(R.id.sourse_sub_subtitle);
        public_course = (TextView)toolbar.findViewById(R.id.public_course);
        linear_toobar = (LinearLayout) toolbar.findViewById(R.id.linear_toobar);
    }

    @Override
    public void setContentView(int layoutId) {
        setContentView(View.inflate(this, layoutId, null));
    }

    @Override
    public void setContentView(View view) {
        rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        if (rootLayout == null) return;
        rootLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initBaseToolbar();
    }

    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            mLoadingDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.setTitleText("数据加载中...");
        }
        mLoadingDialog.show();
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }
}
