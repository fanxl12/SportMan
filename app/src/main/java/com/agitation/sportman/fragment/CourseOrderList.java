package com.agitation.sportman.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.agitation.sportman.BaseFragment;
import com.agitation.sportman.R;
import com.agitation.sportman.activity.Comment;
import com.agitation.sportman.activity.CourseDetail;
import com.agitation.sportman.activity.CourseOrder;
import com.agitation.sportman.adapter.CourseOrderAdapter;
import com.agitation.sportman.inter.OrderNotice;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.ToastUtils;
import com.agitation.sportman.utils.UtilsHelper;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.pingplusplus.android.PaymentActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

/**
 * Created by fanwl on 2015/11/15.
 */
public class CourseOrderList extends BaseFragment implements OrderNotice, BGARefreshLayout.BGARefreshLayoutDelegate {

    private View rootView;
    private List<Map<String, Object>> orderList;
    private CourseOrderAdapter courseOrderAdapter;
    private BGARefreshLayout swipe_container;
    private ListView tickey_list_lv;
    private int status;
    private DataHolder dataHolder;
    private AQuery aq;
    public static final String STATUS_NAME_KEY = "STATUS_NAME_KEY";
    public static final int COMMENT_SUCCEED = 140;
    private String orderId;

    public static CourseOrderList getInstance(int status){
        Bundle bundle = new Bundle();
        bundle.putInt(STATUS_NAME_KEY, status);
        CourseOrderList courseOrderList = new CourseOrderList();
        courseOrderList.setArguments(bundle);
        return courseOrderList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView!=null){
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent!=null)parent.removeView(rootView);
        }else {
            rootView = inflater.inflate(R.layout.course_order_list, container, false);
            status = getArguments().getInt(STATUS_NAME_KEY);
            init();
            Log.e("初始化", status + "");
            initVarible();
            processLogic();
            dataChange();
        }
        return rootView;
    }

    private void initVarible() {
        aq = new AQuery(getContext());
        dataHolder = DataHolder.getInstance();
        orderList = new ArrayList<>();
        courseOrderAdapter = new CourseOrderAdapter(getActivity(), status, orderList, R.layout.courseorder_list_item);
        tickey_list_lv.setAdapter(courseOrderAdapter);
        tickey_list_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CourseDetail.class);
                intent.putExtra("courseId", orderList.get(position).get("courseId") + "");
                startActivity(intent);
            }
        });

        courseOrderAdapter.setOnBtnClickListener(new CourseOrderAdapter.OnBtnClickListener() {
            @Override
            public void onBtnClickListener(Map<String, Object> item, int action) {

                orderId = item.get("id") + "";
                if (action == CourseOrderAdapter.ACTION_ORDER_DELETE) {
                    deleteOrder();
                } else {
                    int status = Integer.parseInt(item.get("status") + "");
                    if (status == CourseOrder.STATUS_UNPAY) {
                        double totalMoney = Double.parseDouble(item.get("totalMoney") + "");
                        pay(totalMoney, orderId, item.get("name") + "", item.get("payWay") + "");
                    } else if (status == CourseOrder.STATUS_UNADVICES) {
                        Intent intent = new Intent(getContext(), Comment.class);
                        intent.putExtra("courseId", item.get("courseId") + "");
                        intent.putExtra("name", item.get("name") + "");
                        intent.putExtra("time", item.get("createDate") + "");
                        intent.putExtra("address", item.get("address") + "");
                        intent.putExtra("orderId", orderId);
                        startActivityForResult(intent, 140);
                    }
                }
            }
        });
    }

    //获取订单数据
//    public void getCourseOrderList(){
//        mActivity.showLoadingDialog();
//        String url = Mark.getServerIp() + "/api/v1/order/getCourseOrderList";
//        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle())
//                .ajax(url, Map.class, new AjaxCallback<Map>() {
//                    @Override
//                    public void callback(String url, Map info, AjaxStatus status) {
//                        mActivity.dismissLoadingDialog();
//                        if (info != null) {
//                            if (Boolean.parseBoolean(info.get("result") + "")) {
//                                Map<String, Object> retData = (Map<String, Object>) info.get("retData");
//                                List<Map<String, Object>> courseOrderList = (List<Map<String, Object>>) retData.get("courseOrderList");
//                                selectedOrderData(courseOrderList);
//                            }
//                        }
//                    }
//                });
//    }

    //删除订单
    public void deleteOrder(){
        mActivity.showLoadingDialog();
        String url = Mark.getServerIp() + "/api/v1/order/deleteOrder";
        Map<String, Object> param = new HashMap<>();
        param.put("id", orderId);
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle())
                .ajax(url, param, Map.class, new AjaxCallback<Map>() {
                    @Override
                    public void callback(String url, Map info, AjaxStatus status) {
                        mActivity.dismissLoadingDialog();
                        if (info != null) {
                            if (Boolean.parseBoolean(info.get("result") + "")) {
//                                getCourseOrderList();
                            }
                        }
                    }
                });
    }

    protected void processLogic() {
        BGAStickinessRefreshViewHolder stickinessRefreshViewHolder = new BGAStickinessRefreshViewHolder(getActivity(), false);
        stickinessRefreshViewHolder.setStickinessColor(R.color.colorPrimary);
        stickinessRefreshViewHolder.setRotateImage(R.mipmap.bga_refresh_stickiness);
        swipe_container.setRefreshViewHolder(stickinessRefreshViewHolder);
        swipe_container.setDelegate(this);
    }

    private void init() {
        swipe_container = (BGARefreshLayout) rootView.findViewById(R.id.swipe_container);
        tickey_list_lv = (ListView)rootView.findViewById(R.id.course_list_lv);
    }

    @Override
    public void dataChange() {
        if (courseOrderAdapter==null)return;
        orderList = UtilsHelper.selectMapList(dataHolder.getOrderList(), "get(:_currobj,'status') like '" + status + "'");
        courseOrderAdapter.setData(orderList);
    }

    /**
     * 支付宝支付渠道
     */
    private static final int REQUEST_CODE_PAYMENT = 1;

    private void pay(double payMoney, String orderId, String name, String payType){

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
        aq.transformer(new MapTransformer()).auth(DataHolder.getInstance().getBasicHandle())
                .ajax(url, params, Map.class, new AjaxCallback<Map>() {

                    @Override
                    public void callback(String url, Map json, AjaxStatus status) {

                        if (json != null) {
                            if (Boolean.parseBoolean(json.get("result")+"")){
                                String result = ((Map<String, Object>)json.get("retData")).get("charge")+"";
                                Intent intent = new Intent();
                                String packageName = getActivity().getPackageName();
                                ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
                                intent.setComponent(componentName);
                                intent.putExtra(PaymentActivity.EXTRA_CHARGE, result);
                                startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                            }else{
                                ToastUtils.showToast(getActivity(), json.get("error") + "");
                            }
                        } else {
                            ToastUtils.showToast(getActivity(), "服务器请求错误:"
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
                    ToastUtils.showToast(getActivity(), "支付成功");
                    payCourse();
                }else if("user_cancelled".equals(result)){
                    ToastUtils.showToast(getActivity(), "支付取消");
                }else {
//                    Logger.show("error_msg", error);
//                    Logger.show("extra_msg", extra_msg);
                    ToastUtils.showToast(getActivity(), error+"-----"+extra_msg);
                }
//                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
//                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
            }
        }
        if (requestCode==COMMENT_SUCCEED){
//            getCourseOrderList();
        }
    }

    //提交支付宝订单
    public void payCourse(){
        String url = Mark.getServerIp() + "/api/v1/order/payCourse";
        Map<String, Object> param = new HashMap<>();
        param.put("orderId", orderId);
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle()).
                ajax(url, param, Map.class, new AjaxCallback<Map>() {
                    @Override
                    public void callback(String url, Map info, AjaxStatus status) {
                        if (info != null) {
                            if (Boolean.parseBoolean(info.get("result") + "")) {
//                                getCourseOrderList();
                            }
                        }
                    }
                });
    }

    private Handler refreshHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==Mark.DATA_REFRESH_SUCCEED){
                swipe_container.endRefreshing();
            }
        }
    };

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
//        getCourseOrderList();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        return false;
    }
}
