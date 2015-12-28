package com.agitation.sportman.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;

/**
 * Created by Fanxl on 2015/12/28.
 */
public class WebActivity extends BaseActivity {

    private WebView wv;
    public static final String URL_NAME = "URL_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity);
        String url = getIntent().getStringExtra(URL_NAME);
        initToolbar();
        initView(url);
    }

    private void initView(String url) {
        wv = (WebView)findViewById(R.id.web_wv);
        wv.loadUrl(url);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient() {

            //覆盖webview默认通过第三方浏览器浏览网页，返回false就会用第三方浏览器，返回true用自己来加载url
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                if (!wv.getSettings().getLoadsImagesAutomatically()) {
                    wv.getSettings().setLoadsImagesAutomatically(true);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                System.out.println("请求的URL：" + url);
            }
            //WebViewClient来帮助WebView处理页面控制和请求
        });

        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setLoadWithOverviewMode(true);


        if(Build.VERSION.SDK_INT >= 19) { //对加载的优化
            wv.getSettings().setLoadsImagesAutomatically(true);
        } else {
            wv.getSettings().setLoadsImagesAutomatically(false);
        }

        wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口

//	    wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//设置使用缓存机制

        wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); //滑动条的样式

        wv.getSettings().setSupportZoom(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.setInitialScale(80); //定义网页比例 100是默认，小于100就是缩小
        wv.setHorizontalScrollBarEnabled(false);  //取消Horizontal ScrollBar显示

        wv.setWebChromeClient(new WebChromeClient() {

            //网页进度加载提示
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
            }

            @Override
            public void onReceivedTitle(WebView view, String webTitle) {
                super.onReceivedTitle(view, webTitle);
                title.setText(webTitle);
            }
        });
    }

    private void initToolbar() {
        if (toolbar!=null){
            title.setText("详情");
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
}
