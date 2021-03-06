package com.agitation.sportman.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agitation.sportman.BaseFragment;
import com.agitation.sportman.R;
import com.agitation.sportman.activity.Collection;
import com.agitation.sportman.activity.CourseOrder;
import com.agitation.sportman.activity.Login;
import com.agitation.sportman.activity.PreferentialCode;
import com.agitation.sportman.activity.Setting;
import com.agitation.sportman.activity.UserInfoEdit;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.FastBlur;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.ScreenUtils;
import com.agitation.sportman.utils.ToastUtils;
import com.agitation.sportman.widget.BadgeView;
import com.agitation.sportman.widget.CircleImageView;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.Map;

/**
 * Created by fanwl on 2015/10/25.
 */
public class MyCenter extends BaseFragment implements View.OnClickListener {

    private View rootView;
    private CircleImageView mycenter_head;
    private ImageView second_bg;
    private TextView userName;
    private DataHolder dataHolder;
    public static final int EDIT_PHOTO = 125;
    private ImageLoader imageLoader;
    private AQuery aq;
    private TextView mycenter_bt_course, mycenter_bt_match;
    private BadgeView courseBadge, matchBadge;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) parent.removeView(rootView);
        } else {
            rootView = inflater.inflate(R.layout.my_center, container, false);
            initVarible();
            initView();
        }
        return rootView;
    }
    private void initVarible() {
        aq = mActivity.aq;
        dataHolder = mActivity.dataHolder;
        imageLoader = ImageLoader.getInstance();
    }

    private void initView() {
        rootView.findViewById(R.id.mycenter_collection).setOnClickListener(this);
        userName = (TextView)rootView.findViewById(R.id.center_userName);
        mycenter_head = (CircleImageView) rootView.findViewById(R.id.mycenter_head);
        second_bg = (ImageView)rootView.findViewById(R.id.second_bg);
        mycenter_head.setOnClickListener(this);
        mycenter_bt_course = (TextView) rootView.findViewById(R.id.mycenter_bt_course);
        mycenter_bt_course.setOnClickListener(this);

        mycenter_bt_match = (TextView) rootView.findViewById(R.id.mycenter_bt_match);
        mycenter_bt_match.setOnClickListener(this);

        int width = ScreenUtils.getScreenW(getActivity());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mycenter_bt_course.getLayoutParams();
        params.width = width/2;
        mycenter_bt_course.setLayoutParams(params);
        mycenter_bt_match.setLayoutParams(params);

        courseBadge = new BadgeView(getActivity(), mycenter_bt_course);
        courseBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        courseBadge.setBadgeBackgroundColor(android.graphics.Color.parseColor("#FFA200"));

        matchBadge = new BadgeView(getActivity(), mycenter_bt_match);
        matchBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        matchBadge.setBadgeBackgroundColor(android.graphics.Color.parseColor("#FFA200"));

        rootView.findViewById(R.id.mycenter_preferentail_code).setOnClickListener(this);

        rootView.findViewById(R.id.mycenter_setting).setOnClickListener(this);
        second_bg.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                second_bg.getViewTreeObserver().removeOnPreDrawListener(this);
                second_bg.buildDrawingCache();
                Bitmap bmp = second_bg.getDrawingCache();
                blur(bmp, second_bg);
                return true;
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        if (dataHolder.isLogin()){
            String headImg = dataHolder.getImageProfix() + dataHolder.getUserData().get("head")+"";
            setCenterHead(headImg);
            userName.setText(dataHolder.getUserData().get("name") + "");
            getOrderNumber();
        }else {
            Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.default_head);
            setHeadImage(bitmap);
            userName.setText("未登录");
            courseBadge.hide();
            matchBadge.hide();
        }
    }

    /**
     * 我的中心获取课程和比赛的消息数量
     */
    private void getOrderNumber(){
        mActivity.showLoadingDialog();
        String url = Mark.getServerIp() + "/api/v1/order/getOrderNumber";
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle()).ajax(url, Map.class,
                new AjaxCallback<Map>(){
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                mActivity.dismissLoadingDialog();
                if (info!=null){
                    if (Boolean.parseBoolean(info.get("result")+"")){
                        Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                        String courseNumberStr = retData.get("courseNumber")+"";
                        String matchNumberStr = retData.get("matchNumber")+"";
                        if (!TextUtils.isEmpty(courseNumberStr) && !"null".equals(courseNumberStr)){
                            int courseNumber = Integer.parseInt(courseNumberStr);
                            if (courseNumber>0){
                                courseBadge.setText(courseNumberStr);
                                courseBadge.show();
                            }else{
                                courseBadge.hide();
                            }
                        }else{
                            courseBadge.hide();
                        }
                        if (!TextUtils.isEmpty(matchNumberStr) && !"null".equals(matchNumberStr)){
                            int matchNumber = Integer.parseInt(matchNumberStr);
                            if (matchNumber>0){
                                matchBadge.setText(matchNumberStr);
                                matchBadge.show();
                            }else{
                                matchBadge.hide();
                            }
                        }else{
                            matchBadge.hide();
                        }
                    }
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==EDIT_PHOTO){
            setHeadImage(dataHolder.getCenterHeadBit());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mycenter_head:
                if (dataHolder.isLogin()){
                    startActivityForResult(new Intent(getActivity(), UserInfoEdit.class), 100);
                }else {
                    startActivity(new Intent(getActivity(), Login.class));
                }
                break;
            case R.id.mycenter_bt_course:
                if (dataHolder.isLogin()){
                    startActivity(new Intent(getActivity(), CourseOrder.class));
                }else{
                    ToastUtils.showToast(getContext(), "请登录");
                }
                break;
            case R.id.mycenter_bt_match:
                ToastUtils.showToast(getActivity(), "暂未开放");
                break;
            case R.id.mycenter_collection:
                if (dataHolder.isLogin()){
                    startActivity(new Intent(getActivity(), Collection.class));
                }else{
                    ToastUtils.showToast(getContext(), "请登录");
                }
                break;
            case R.id.mycenter_preferentail_code:
                if (dataHolder.isLogin()){
                    startActivity(new Intent(getActivity(), PreferentialCode.class));
                }else{
                    ToastUtils.showToast(getContext(), "请登录");
                }
                break;
            case R.id.mycenter_setting:
                startActivity(new Intent(getActivity(), Setting.class));
                break;
        }
    }
    public void setCenterHead(String url){

        imageLoader.loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                setHeadImage(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    private void setHeadImage(Bitmap loadedImage){
        mycenter_head.setImageBitmap(loadedImage);
        second_bg.setImageBitmap(loadedImage);
        second_bg.buildDrawingCache();
        Bitmap bmp = second_bg.getDrawingCache();
        if (bmp!=null){
            blur(bmp, second_bg);
        }

    }

    /*
    给我的中心背景图像进行模糊处理
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void blur(Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();
        float scaleFactor = 8;
        float radius = 2;

        Bitmap overlay = Bitmap.createBitmap(
                (int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop()
                / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        if (view instanceof ImageView){
            ((ImageView)view).setImageBitmap(overlay);
        }else {
            view.setBackground(new BitmapDrawable(getResources(), overlay));
        }
        System.out.println(System.currentTimeMillis() - startMs + "ms");
    }
}