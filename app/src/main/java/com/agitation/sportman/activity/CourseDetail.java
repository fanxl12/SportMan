package com.agitation.sportman.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.agitation.sportman.R;
import com.agitation.sportman.adapter.CommentAdapter;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.ToastUtils;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.pingplusplus.android.PaymentActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanwl on 2015/11/27.
 */
public class CourseDetail extends AppCompatActivity implements View.OnClickListener {


    private View payView,fastPayView;
    private PopupWindow payWindow,fastPayWindow;
    private Button bt_enrolled,bt_fast_pay;
    private int shareIconS[] = {R.drawable.course, R.drawable.course, R.drawable.course};
    private FragmentTabHost pay_successed_tabhost;
    private LayoutInflater inflater;
    private String courseId;
    private AQuery aq;
    private Map<String, Object> courseDetailInfo;
    private TextView buy_number, end_time, surplus_number, course_state, favorable_price, orginalPrice,
            start_time, address, course_introduction, coursr_type, notice, teacher_name, teacher_honor
            , tx_count;

    private int count = 1;
    private double totailMoney;
    private DataHolder dataHolder;
    private ListView lv_comment;
    private List<Map<String, Object>> commentList;
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_detail);
        Intent intent = getIntent();
        courseId = intent.getStringExtra("courseId");
        initVarible();
        initView();
        getCourseDetailInfo();
        getAdviceList();
    }

    private void initVarible() {
        aq = new AQuery(this);
        commentList = new ArrayList<>();
        dataHolder =DataHolder.getInstance();
        commentAdapter = new CommentAdapter(commentList, this);
    }

    private void initView() {
        inflater = LayoutInflater.from(this);
        lv_comment = (ListView) findViewById(R.id.lv_conment);
        lv_comment.setAdapter(commentAdapter);

        buy_number = (TextView) findViewById(R.id.buy_number);
        end_time = (TextView) findViewById(R.id.end_time);
        surplus_number = (TextView) findViewById(R.id.surplus_number);
        course_state = (TextView) findViewById(R.id.course_state);
        favorable_price = (TextView) findViewById(R.id.favorable_price);
        orginalPrice = (TextView) findViewById(R.id.orginal_price);
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
        payWindow.setBackgroundDrawable(new BitmapDrawable());
        bt_fast_pay.setOnClickListener(this);
        payWindow.setOutsideTouchable(true);
        payWindow.setFocusable(true);
        payWindow.update();
        payWindow.setAnimationStyle(R.style.PopBottomToTop);

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_enrolled:
                payWindow.showAtLocation(bt_enrolled, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
                break;
            case R.id.bt_fast_pay:
                totailMoney = Double.parseDouble(courseDetailInfo.get("price")+"") * count;
                pay(totailMoney);
                break;
            case R.id.iv_del_num:
                int countDel = Integer.parseInt(tx_count.getText().toString()) - 1;
                if (countDel==0)countDel=countDel + 1;
                count = countDel;
                tx_count.setText(countDel+"");
                break;
            case R.id.iv_add_num:
                int countAdd = Integer.parseInt(tx_count.getText().toString()) + 1;
                count = countAdd;
                tx_count.setText(countAdd+"");
                break;
        }
    }
    //提交订单
    public void commintCourseOrder(){
        String url = Mark.getServerIp() + "/api/v1/order/commitCourse";
        Map<String, Object> param = new HashMap<>();
        param.put("courseId",courseDetailInfo.get("id")+"");
        param.put("name",courseDetailInfo.get("name")+"");
        param.put("startTime",courseDetailInfo.get("startTime")+"");
        param.put("count",count);
        param.put("totalMoney",totailMoney);
        param.put("payWay","支付宝");
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle()).
                ajax(url, param, Map.class, new AjaxCallback<Map>() {
                    @Override
                    public void callback(String url, Map info, AjaxStatus status) {
                        if (info != null) {
                            if (Boolean.parseBoolean(info.get("result") + "")) {
                                Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                                payCourse(retData.get("orderId") + "");
                            }
                        }
                    }
                });
    }

    //提交支付宝订单
    public void payCourse(String orderId){
        String url = Mark.getServerIp() + "/api/v1/order/payCourse";
        Map<String, Object> param = new HashMap<>();
        param.put("orderId", orderId);
        param.put("payWay","支付宝");
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle()).
                ajax(url, param, Map.class, new AjaxCallback<Map>() {
                    @Override
                    public void callback(String url, Map info, AjaxStatus status) {
                        if (info != null) {

                        }
                    }
                });
    }

    private void getAdviceList(){
        String url = Mark.getServerIp() + "/api/v1/advice/getAdviceList";
        Map<String, Object> param = new HashMap<>();
        param.put("courseId",courseId);
        aq.transformer(new MapTransformer()).ajax(url, param, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                if (info != null) {
                    if (Boolean.parseBoolean(info.get("result") + "")) {
                        Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                        commentList = (List<Map<String, Object>>) retData.get("advices");
                        commentAdapter.setCommentList(commentList, true);
                        setListViewHeight(lv_comment);
                    }
                }
            }
        });
    }


    private void setListViewHeight(ListView lv){
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

        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.height = totalHeight + (lv.getDividerHeight() * (childNum - 1));
        //listView.getDividerHeight()获取子项间分隔符占用的高度
        //params.height最后得到整个ListView完整显示需要的高度
        lv.setLayoutParams(params);
    }


    private View getTabItemView(int index) {
        View view = inflater.inflate(R.layout.tab_item_view, null);
        ImageView tab_item_icon = (ImageView) view.findViewById(R.id.tab_item_icon);
        tab_item_icon.setBackgroundResource(shareIconS[index]);
        return view;
    }

    public void getCourseDetailInfo(){
        String url = Mark.getServerIp()+ "/api/v1/course/getCourseDetail";
        Map<String, Object> param = new HashMap<>();
        param.put("courseId",courseId);
        aq.transformer(new MapTransformer()).ajax(url, param, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
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

    public void setCourseDetailInfo(Map<String, Object> item){



        buy_number.setText(item.get("buyNumber") + "");

        int surplusNumber = Integer.parseInt(item.get("totalNumber")+"") - Integer.parseInt(item.get("buyNumber") + "");

        surplus_number.setText("" + surplusNumber);

        end_time.setText("" + item.get("endTime"));

//        course_state.setText("" + item.get("buy_number"));

        favorable_price.setText("￥" + item.get("price") + "/");

        orginalPrice.setText("￥" + item.get("orginalPrice"));

        start_time.setText("时间:" + item.get("startTime"));

        address.setText("地址:" + item.get("address"));

//        course_introduction.setText("" + item.get("buy_number"));

        coursr_type.setText("" + item.get("courseTypeName"));

        notice.setText("" + item.get("notice"));

//        teacher_name.setText("" + item.get("buy_number"));

//        teacher_honor.setText("" + item.get("buy_number"));
    }


    /**
     * 微信支付渠道
     */
    private final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private final String CHANNEL_ALIPAY = "alipay";
    private String payType = null;
    private static final int REQUEST_CODE_PAYMENT = 1;

    private void pay(double payMoney){

        payType = CHANNEL_ALIPAY;

        payMoney = 0.01;

        long bill = System.currentTimeMillis();

        Map<String, Object> params = new HashMap<>();
        //支付金额 单位为分
        params.put("amount", (int)(payMoney*100));
        //商品的标题，该参数最长为 32 个 Unicode 字符，银联全渠道（upacp/upacp_wap）限制在 32 个字节。
        params.put("subject", "High运动");
        //商品的描述信息，该参数最长为 128 个 Unicode 字符，yeepay_wap 对于该参数长度限制为 100 个 Unicode 字符。
        params.put("body", "课程订单"+":"+"201548751");
        //商户订单号，适配每个渠道对此参数的要求，必须在商户系统内唯一。推荐使用 8-20 位，要求数字或字母，不允许特殊字符
        params.put("order_no", bill);
        //支付渠道
        params.put("channel", payType);
        //发起支付请求终端的 ip 地址
        params.put("client_ip", "192.168.1.200");

        String url = Mark.getServerIp() + "/api/v1/drp/pay";
        aq.transformer(new MapTransformer()).auth(DataHolder.getInstance().getBasicHandle())
                .ajax(url, params, Map.class, new AjaxCallback<Map>() {

                    @Override
                    public void callback(String url, Map json, AjaxStatus status) {

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
                    commintCourseOrder();
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
    }


}
