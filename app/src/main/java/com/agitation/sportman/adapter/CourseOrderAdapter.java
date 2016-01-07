package com.agitation.sportman.adapter;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.agitation.sportman.R;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.ViewHolder;

import java.util.List;
import java.util.Map;

/**
 * Created by fanwl on 2015/11/17.
 */
public class CourseOrderAdapter extends CommonAdapter<Map<String, Object>> {

    private int status;
    private OnBtnClickListener onBtnClickListener;
    public static final int ACTION_ORDER_DELETE = 0;
    public static final int ACTION_ORDER_PAY = 1;


    public CourseOrderAdapter(Context context, int status, List<Map<String, Object>> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
        this.status=status;
    }

    public void setData(List<Map<String, Object>> mDatas) {
        super.setData(mDatas);
    }
    public void setOnBtnClickListener(OnBtnClickListener onBtnClickListener){
        this.onBtnClickListener = onBtnClickListener;

    }

    @Override
    public void convert(ViewHolder helper, Map<String, Object> item) {
        helper.setText(R.id.course_order_match_name,item.get("name")+"");
        helper.setText(R.id.course_order_match_time,item.get("createDate")+"");
        helper.setText(R.id.course_order_match_address,item.get("address")+"");

        Button bt_pay = helper.getView(R.id.course_order_bt_pay);
        Button bt_delete = helper.getView(R.id.course_order_bt_delete);
        bt_pay.setTag(item);
        bt_delete.setTag(item);
        if (status== Mark.ORDER_STATUS_UNPAY){
            bt_pay.setText("支付");
            bt_delete.setText("删除");
        }else if (status== Mark.ORDER_STATUS_PAYED){
            bt_pay.setVisibility(View.GONE);
            bt_delete.setVisibility(View.GONE);
        }else if (status== Mark.ORDER_STATUS_UNADVICES){
            bt_pay.setVisibility(View.VISIBLE);
            bt_pay.setText("评价");
            bt_delete.setVisibility(View.GONE);
        }if (status== Mark.ORDER_STATUS_DONE){
            bt_pay.setVisibility(View.GONE);
            bt_delete.setVisibility(View.GONE);
        }

        bt_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> item = (Map<String, Object>) v.getTag();
                if (onBtnClickListener!=null){
                    onBtnClickListener.onBtnClickListener(item, ACTION_ORDER_PAY);
                }
            }
        });

        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> item = (Map<String, Object>) v.getTag();
                if (onBtnClickListener!=null){
                    onBtnClickListener.onBtnClickListener(item, ACTION_ORDER_DELETE);
                }
            }
        });
    }

    public interface OnBtnClickListener{
        void onBtnClickListener(Map<String, Object> item, int action);
    }
}
