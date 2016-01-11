package com.agitation.sportman.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.utils.UtilsHelper;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;
import com.umeng.update.UmengUpdateAgent;

/**
 * Created by fanwl on 2015/12/21.
 */
public class Setting extends BaseActivity {

    private TextView version;
    private final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]{
            SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA,
            SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
    };

    UMImage image = new UMImage(Setting.this, "http://www.umeng.com/images/pic/social/integrated_3.png");
    UMusic music = new UMusic("http://music.huoxing.com/upload/20130330/1364651263157_1085.mp3");

    UMVideo video = new UMVideo("http://video.sina.com.cn/p/sports/cba/v/2013-10-22/144463050817.html");

    private UMShareListener shareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(Setting.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(Setting.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(Setting.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        initToolbar();
        initView();
        music.setTitle("sdasdasd");
        music.setThumb(new UMImage(Setting.this, "http://www.umeng.com/images/pic/social/chart_1.png"));

    }


    private void initToolbar() {
        if (toolbar!=null){

            title.setText("设置");
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

    private void initView() {
        version = (TextView) findViewById(R.id.version);
        version.setText(UtilsHelper.getAppVersion(this));
        findViewById(R.id.set_check_version).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengUpdateAgent.forceUpdate(Setting.this);
            }
        });

        findViewById(R.id.set_shere_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAction(Setting.this).setPlatform(SHARE_MEDIA.QQ).setCallback(shareListener)
                        .withText("hello umeng")
                        .withMedia(music)
                        .withTitle("qqshare")
                        .withTargetUrl("http://dev.umeng.com")
                        .share();
            }
        });
        findViewById(R.id.set_shere_wx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAction(Setting.this).setPlatform(SHARE_MEDIA.WEIXIN).setCallback(shareListener)
                        .withText("hello wx")
                        .withMedia(video)
                        .share();
            }
        });
        findViewById(R.id.set_shere_qqzone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAction(Setting.this).setPlatform(SHARE_MEDIA.QZONE).setCallback(shareListener)
                        .withText("hello umeng")
                        .withMedia(image)
                        .share();
            }
        });
        findViewById(R.id.set_shere_wxquan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAction(Setting.this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(shareListener)
                        .withText("hello umeng")
                        .withMedia(music)
                        .share();
            }
        });

        findViewById(R.id.set_shere_board).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * shareboard need the platform all you want and
                 * callbacklistener,then open it
                 **/
                new ShareAction(Setting.this)
                        .setDisplayList(SHARE_MEDIA.QQ,
                                SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN,
                                SHARE_MEDIA.WEIXIN_CIRCLE)
                        .setContentList(new ShareContent(), new ShareContent(), new ShareContent(), new ShareContent())
                        .withText("呵呵")
                        .withTitle("title")
                        .withTargetUrl("http://www.baidu.com")
                        .withMedia(image)
                        .setListenerList(shareListener, shareListener, shareListener, shareListener).open();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
