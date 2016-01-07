package com.agitation.sportman.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.utils.UtilsHelper;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
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

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {


        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        initToolbar();
        initView();

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
                UMImage image = new UMImage(Setting.this, "http://www.umeng.com/images/pic/social/integrated_3.png");
                new ShareAction(Setting.this).setDisplayList(displaylist)
                        .withText( "呵呵" )
                        .withTitle("title")
                        .withTargetUrl("http://www.baidu.com")
                        .withMedia(image)
                        .setShareboardclickCallback(shareBoardlistener)
                        .open();
            }
        });
    }
}
