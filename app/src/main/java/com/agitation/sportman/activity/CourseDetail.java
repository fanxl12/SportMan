package com.agitation.sportman.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.adapter.CommentAdapter;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.ToastUtils;
import com.agitation.sportman.widget.CircleImageView;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pingplusplus.android.PaymentActivity;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanwl on 2015/11/27.
 */
public class CourseDetail extends BaseActivity implements View.OnClickListener {


    private View payView,fastPayView;
    private PopupWindow payWindow,fastPayWindow;
    private Button bt_enrolled,bt_fast_pay;
    private LayoutInflater inflater;
    private CheckBox alipy_pay, weixin_pay;
    private String courseId, orderId;
    private Map<String, Object> courseDetailInfo;
    private TextView buy_number, end_time, surplus_number, favorable_price, orginalPrice,
            start_time, address, course_introduction, coursr_type, notice, teacher_name, teacher_honor
            , tx_count, unit_price, subtotal_money, total_money;

    private int count = 1;
    private ListView lv_comment;
    private List<Map<String, Object>> commentList;
    private CommentAdapter commentAdapter;

    private double unitPrice=0.00;
    private double subMoney =0.00;
    View choose_bg;
    Animation dropdown_in, dropdown_out, dropdown_mask_out;

    private CircleImageView coach_head;
    private ImageLoader imageLoader;
    private TextView course_advices;
    private View footer;
    private TextView advice_tv_num;


    //分享操作
    private final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]{
            SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA,
            SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
    };

    UMImage image = new UMImage(CourseDetail.this, "http://www.umeng.com/images/pic/social/integrated_3.png");
    UMusic music = new UMusic("http://music.huoxing.com/upload/20130330/1364651263157_1085.mp3");

    UMVideo video = new UMVideo("http://video.sina.com.cn/p/sports/cba/v/2013-10-22/144463050817.html");

    private UMShareListener shareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(CourseDetail.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(CourseDetail.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(CourseDetail.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_detail);
        Intent intent = getIntent();
        courseId = intent.getStringExtra("courseId");
        initToolbar();
        initVarible();
        initView();
        getCourseDetailInfo();
        getAdviceList();
    }

    private void initToolbar() {
        if (toolbar!=null){
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
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList, this, true);
        imageLoader = ImageLoader.getInstance();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        choose_bg = findViewById(R.id.choose_bg);

        dropdown_in = AnimationUtils.loadAnimation(this, R.anim.dropdown_in);
        dropdown_out = AnimationUtils.loadAnimation(this, R.anim.dropdown_out);
        dropdown_mask_out = AnimationUtils.loadAnimation(this, R.anim.dropdown_mask_out);

        inflater = LayoutInflater.from(this);
        footer = inflater.inflate(R.layout.course_advice_footer, null);
        advice_tv_num = (TextView)footer.findViewById(R.id.advice_tv_num);
        footer.setVisibility(View.GONE);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseDetail.this, CommentList.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });
        lv_comment = (ListView) findViewById(R.id.lv_conment);
        lv_comment.addFooterView(footer);
        lv_comment.setAdapter(commentAdapter);
        lv_comment.setEnabled(false);


        buy_number = (TextView) findViewById(R.id.buy_number);
        end_time = (TextView) findViewById(R.id.end_time);
        surplus_number = (TextView) findViewById(R.id.surplus_number);
        favorable_price = (TextView) findViewById(R.id.favorable_price);
        orginalPrice = (TextView) findViewById(R.id.orginal_price);
        orginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        start_time = (TextView) findViewById(R.id.start_time);
        address = (TextView) findViewById(R.id.address);
        course_introduction = (TextView) findViewById(R.id.course_introduction);
        coursr_type = (TextView) findViewById(R.id.coursr_type);
        notice = (TextView) findViewById(R.id.notice);
        teacher_name = (TextView) findViewById(R.id.teacher_name);
        teacher_honor = (TextView) findViewById(R.id.teacher_honor);

        bt_enrolled = (Button) findViewById(R.id.bt_enrolled);
        bt_enrolled.setOnClickListener(this);

        //快速支付的popupWindow
        payView = inflater.inflate(R.layout.fast_pay_window, null);
        payWindow = new PopupWindow(payView, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        bt_fast_pay = (Button)payView.findViewById(R.id.bt_fast_pay);
        payView.findViewById(R.id.iv_add_num).setOnClickListener(this);
        payView.findViewById(R.id.iv_del_num).setOnClickListener(this);
        tx_count = (TextView) payView.findViewById(R.id.tx_count);

        unit_price = (TextView) payView.findViewById(R.id.unit_price);
        subtotal_money = (TextView) payView.findViewById(R.id.subtotal_money);
        total_money = (TextView) payView.findViewById(R.id.total_money);

        alipy_pay = (CheckBox) payView.findViewById(R.id.alipy_pay);
        weixin_pay = (CheckBox) payView.findViewById(R.id.weixin_pay);
        alipy_pay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    weixin_pay.setChecked(false);
                }
            }
        });
        weixin_pay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    alipy_pay.setChecked(false);
                }
            }
        });
        payWindow.setBackgroundDrawable(new BitmapDrawable());
        bt_fast_pay.setOnClickListener(this);
        payWindow.setOutsideTouchable(true);
        payWindow.setFocusable(true);
        payWindow.update();
        payWindow.setAnimationStyle(R.style.PopBottomToTop);

        payWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                choose_bg.startAnimation(dropdown_mask_out);
                choose_bg.setVisibility(View.GONE);
            }
        });

        //支付成功后的popupWindow
        fastPayView = inflater.inflate(R.layout.pay_successed, null);
        fastPayWindow = new PopupWindow(fastPayView, (int)(Mark.phoneWidth*0.8),LinearLayout.LayoutParams.WRAP_CONTENT);
//        pay_successed_tabhost = (FragmentTabHost)fastPayView.findViewById(R.id.pay_successed_tabhost);
        fastPayWindow.setBackgroundDrawable(new BitmapDrawable());
        fastPayWindow.setOutsideTouchable(true);
        fastPayWindow.setFocusable(true);
        fastPayWindow.update();
        fastPayWindow.setAnimationStyle(R.style.PopBottomToTop);
//        pay_successed_tabhost.setup(this, getSupportFragmentManager());

//        pay_successed_tabhost.getTabWidget().setDividerDrawable(null);

//        pay_successed_tabhost.addTab(pay_successed_tabhost.newTabSpec("QQ").setIndicator(getTabItemView(0)));
//        pay_successed_tabhost.addTab(pay_successed_tabhost.newTabSpec("微信").setIndicator(getTabItemView(1)));
//        pay_successed_tabhost.addTab(pay_successed_tabhost.newTabSpec("朋友圈").setIndicator(getTabItemView(2)));

//        pay_successed_tabhost.setCurrentTab(0);

        findViewById(R.id.course_ll_address).setOnClickListener(this);

        coach_head = (CircleImageView)findViewById(R.id.coach_head);
        course_advices = (TextView)findViewById(R.id.course_advices);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_enrolled:
                if (dataHolder.isLogin()){
                    unitPrice = Double.parseDouble(courseDetailInfo.get("price")+"");
                    unit_price.setText(unitPrice+"元/人");
                    subMoney = unitPrice*1;
                    subtotal_money.setText(subMoney+"");
                    total_money.setText(subMoney+"");
                    payWindow.showAtLocation(bt_enrolled, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    //背景变暗
                    choose_bg.startAnimation(dropdown_in);
                    choose_bg.setVisibility(View.VISIBLE);
                }else{
                    goTOLogin();
                }
                break;
            case R.id.bt_fast_pay:
                if (alipy_pay.isChecked()){
                    payType= CHANNEL_ALIPAY;
                }else {
                    payType = CHANNEL_WECHAT;
                }
                commintCourseOrder();
                break;
            case R.id.iv_del_num:
                int countDel = Integer.parseInt(tx_count.getText().toString()) - 1;
                if (countDel==0)countDel=countDel + 1;
                count = countDel;
                tx_count.setText(countDel+"");
                subMoney = unitPrice * count;
                subtotal_money.setText(subMoney+"");
                total_money.setText(subMoney+"");
                break;
            case R.id.iv_add_num:
                int countAdd = Integer.parseInt(tx_count.getText().toString()) + 1;
                count = countAdd;
                tx_count.setText(countAdd+"");
                subMoney = unitPrice * count;
                subtotal_money.setText(subMoney+"");
                total_money.setText(subMoney+"");
                break;
//            case R.id.more_comment:
//                Intent intent = new Intent(CourseDetail.this, CommentList.class);
//                intent.putExtra("courseId", courseId);
//                startActivity(intent);
//                break;
            case R.id.course_ll_address:
                Intent mapIntent = new Intent(CourseDetail.this, MapActivity.class);
                mapIntent.putExtra(MapActivity.MAP_TARGET_NAME, courseDetailInfo.get("companyName")+"");
                mapIntent.putExtra(MapActivity.MAP_TARGET_ADDRESS, courseDetailInfo.get("address")+"");
                mapIntent.putExtra(MapActivity.MAP_LONGITUDE, Double.parseDouble(courseDetailInfo.get("longitude")+""));
                mapIntent.putExtra(MapActivity.MAP_LATIDUTE, Double.parseDouble(courseDetailInfo.get("latitude")+""));
                startActivity(mapIntent);
                break;
        }
    }
    //提交订单
    public void commintCourseOrder(){
        String url = Mark.getServerIp() + "/api/v1/order/commitCourse";
        Map<String, Object> param = new HashMap<>();
        param.put("courseId",courseDetailInfo.get("id")+"");
        param.put("name", courseDetailInfo.get("name")+"");
        param.put("startTime",courseDetailInfo.get("startTime")+"");
        param.put("count",count);
        param.put("totalMoney",subMoney);
        param.put("payWay",payType);
        showLoadingDialog();
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle()).
                ajax(url, param, Map.class, new AjaxCallback<Map>() {
                    @Override
                    public void callback(String url, Map info, AjaxStatus status) {
                        dismissLoadingDialog();
                        if (info != null) {
                            if (Boolean.parseBoolean(info.get("result") + "")) {
                                Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                                orderId = retData.get("orderId") + "";
                                pay(subMoney, orderId, retData.get("name") + "");
                            }
                        }
                    }
                });
    }


    /**
     * 购买前没有登录的操作
     */
    private void goTOLogin(){
        new AlertDialogWrapper.Builder(this)
                .setMessage("请登录")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CourseDetail.this, Login.class);
                        intent.putExtra("isNormalLogin", false);
                        startActivity(intent);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

    }


    //提交支付宝订单
    public void payCourse(){
        String url = Mark.getServerIp() + "/api/v1/order/payCourse";
        Map<String, Object> param = new HashMap<>();
        param.put("orderId", orderId);
        param.put("payWay", payType);
        param.put("courseId", courseId);
        showLoadingDialog();
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle()).
                ajax(url, param, Map.class, new AjaxCallback<Map>() {
                    @Override
                    public void callback(String url, Map info, AjaxStatus status) {
                        dismissLoadingDialog();
                        if (info != null) {
                            if (Boolean.parseBoolean(info.get("result") + "")) {
                                fastPayWindow.showAtLocation(bt_enrolled, Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            }
                        }
                    }
                });
    }

    /**
     * 获取评论列表
     */
    private void getAdviceList(){
        String url = Mark.getServerIp() + "/api/v1/advice/getAdviceList";
        Map<String, Object> param = new HashMap<>();
        param.put("courseId", courseId);
        param.put("pageNumber", 1);
        param.put("pageSize", 10);
        showLoadingDialog();
        aq.transformer(new MapTransformer()).ajax(url, param, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                dismissLoadingDialog();
                if (info != null) {
                    if (Boolean.parseBoolean(info.get("result") + "")) {
                        Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                        commentList = (List<Map<String, Object>>) retData.get("advices");
                        if (commentList == null || commentList.size() == 0) {
                            course_advices.setVisibility(View.GONE);
                            lv_comment.setVisibility(View.GONE);
                            footer.setVisibility(View.GONE);
                        } else {
                            course_advices.setVisibility(View.VISIBLE);
                            lv_comment.setVisibility(View.VISIBLE);
                            footer.setVisibility(View.VISIBLE);
                            advice_tv_num.setText("查看全部"+commentList.size()+"条评论");
                            commentAdapter.setCommentList(commentList);
                            setListViewHeight(lv_comment);
//                            setListViewHeightBasedOnChildren(lv_comment);
                        }
                    }
                }
            }
        });
    }

    /**
     * @param lv
     * 重新测量listview的高度
     */
    private void setListViewHeight(ListView lv){
        if (lv==null)return;

        //获取ListView对应的Adapter
        ListAdapter listAdapter = lv.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int childNum = listAdapter.getCount();
        for (int i = 0, len = childNum; i < len; i++) {   //listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, lv);
            listItem.measure(0, View.MeasureSpec.UNSPECIFIED);  //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight();  //统计所有子项的总高度
        }

        if (footer.getVisibility()==View.VISIBLE){
            totalHeight += footer.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.height = totalHeight + (lv.getDividerHeight() * (childNum - 1));
        //listView.getDividerHeight()获取子项间分隔符占用的高度
        //params.height最后得到整个ListView完整显示需要的高度
        lv.setLayoutParams(params);
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        if(listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        if (footer.getVisibility()==View.VISIBLE){
            totalHeight += footer.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 获取本课程的基本信息
     */
    public void getCourseDetailInfo(){
        String url = Mark.getServerIp()+ "/api/v1/course/getCourseDetail";
        Map<String, Object> param = new HashMap<>();
        param.put("courseId", courseId);
        showLoadingDialog();
        aq.transformer(new MapTransformer()).ajax(url, param, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                dismissLoadingDialog();
                if (info != null) {
                    if (Boolean.parseBoolean(info.get("result") + "")) {
                        Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                        courseDetailInfo = (Map<String, Object>) retData.get("course");
                        setCourseDetailInfo(courseDetailInfo);
                    }
                }
            }
        });
    }
    //设置课程详情数据
    public void setCourseDetailInfo(Map<String, Object> item){

        title.setText(item.get("name")+"");
        
        buy_number.setText(item.get("buyNumber") + "");

        int surplusNumber = Integer.parseInt(item.get("totalNumber")+"") - Integer.parseInt(item.get("buyNumber") + "");

        surplus_number.setText("" + surplusNumber);

        end_time.setText("" + item.get("endTime"));

        favorable_price.setText("￥" + item.get("price"));

        orginalPrice.setText("￥" + item.get("orginalPrice"));

        start_time.setText("时间: " + item.get("startTime"));

        address.setText("地址: " + item.get("address"));

        course_introduction.setText("" + item.get("introduce"));

        coursr_type.setText("" + item.get("courseTypeName"));

        notice.setText("" + item.get("notice"));

        teacher_name.setText(item.get("coachName") + " " + item.get("coachTime"));

        teacher_honor.setText("" + item.get("honor"));

//        if (item.get("coachUrl")!=null){
//            String headUrl = dataHolder.getImageProfix()+item.get("coachUrl")+"";
//            imageLoader.displayImage(headUrl, coach_head, ImageOptHelper.getAvatarOptions());
//        }
    }


    /**
     * 微信支付渠道
     */
    private final String CHANNEL_WECHAT = "wx";
    /**
     * 支付宝支付渠道
     */
    private final String CHANNEL_ALIPAY = "alipay";
    private String payType = CHANNEL_ALIPAY;
    private static final int REQUEST_CODE_PAYMENT = 1;

    private void pay(double payMoney, String orderId, String name){

//        payType = CHANNEL_ALIPAY;

        String body = "订单号:"+orderId+", "+name+", 价格:"+payMoney;

        payMoney = 0.01;


        Map<String, Object> params = new HashMap<>();
        //支付金额 单位为分
        params.put("amount", (int)(payMoney*100));
        //商品的标题，该参数最长为 32 个 Unicode 字符，银联全渠道（upacp/upacp_wap）限制在 32 个字节。
        params.put("subject", name);
        //商品的描述信息，该参数最长为 128 个 Unicode 字符，yeepay_wap 对于该参数长度限制为 100 个 Unicode 字符。
        params.put("body", body);
        //商户订单号，适配每个渠道对此参数的要求，必须在商户系统内唯一。推荐使用 8-20 位，要求数字或字母，不允许特殊字符
        params.put("order_no", orderId);
        //支付渠道
        params.put("channel", payType);
        //发起支付请求终端的 ip 地址
        params.put("client_ip", "192.168.1.200");
        params.put("phoneType", "android");

        String url = Mark.getServerIp() + "/api/v1/drp/pay";
        showLoadingDialog();
        aq.transformer(new MapTransformer()).auth(DataHolder.getInstance().getBasicHandle())
                .ajax(url, params, Map.class, new AjaxCallback<Map>() {
                    @Override
                    public void callback(String url, Map json, AjaxStatus status) {
                        dismissLoadingDialog();
                        if (json != null) {
                            if (Boolean.parseBoolean(json.get("result")+"")){
                                String result = ((Map<String, Object>)json.get("retData")).get("charge")+"";
                                Intent intent = new Intent();
                                String packageName = CourseDetail.this.getPackageName();
                                ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
                                intent.setComponent(componentName);
                                intent.putExtra(PaymentActivity.EXTRA_CHARGE, result);
                                startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                            }else{
                                ToastUtils.showToast(CourseDetail.this, json.get("error") + "");
                            }
                        } else {
                            ToastUtils.showToast(CourseDetail.this, "服务器请求错误:"
                                    + status.getCode());
                            Log.e("ProductList请求错误", status.getCode() + "");
                        }
                    }
                });

    }

    /**
     * onActivityResult 获得支付结果，如果支付成功，服务器会收到ping++ 服务器发送的异步通知。
     * 最终支付成功根据异步通知为准
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_PAYMENT){
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                String error  = data.getExtras().getString("error_msg");
                String extra_msg  = data.getExtras().getString("extra_msg");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                if ("success".equals(result)){
                    ToastUtils.showToast(CourseDetail.this, "支付成功");
                    payWindow.dismiss();
                    payCourse();
                }else if("user_cancelled".equals(result)){
                    ToastUtils.showToast(CourseDetail.this, "支付取消");
                }else {
//                    Logger.show("error_msg", error);
//                    Logger.show("extra_msg", extra_msg);
                    ToastUtils.showToast(CourseDetail.this, error+"-----"+extra_msg);
                }
//                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
//                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
            }
        }
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.menu_share){
            ToastUtils.showToast(this, "分享");
            new ShareAction(CourseDetail.this)
                    .setDisplayList(SHARE_MEDIA.QQ,
                            SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN,
                            SHARE_MEDIA.WEIXIN_CIRCLE)
                    .withMedia(image)
                    .setListenerList(shareListener, shareListener, shareListener, shareListener).open();
        }
        return super.onOptionsItemSelected(item);
    }

}
