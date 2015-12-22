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
        helper.setText(R.id.course_order_match_time,item.get("time")+"");
        helper.setText(R.id.course_order_match_address,item.get("address")+"");

        Button bt = helper.getView(R.id.course_order_bt_pay);
        bt.setTag(item);
        if (status== Mark.ORDER_STATUS_UNPAY){
            bt.setVisibility(View.VISIBLE);
            bt.setText("支付");
        }else if (status== Mark.ORDER_STATUS_PAYED){
            bt.setVisibility(View.INVISIBLE);
        }else if (status== Mark.ORDER_STATUS_UNADVICES){
            bt.setVisibility(View.VISIBLE);
            bt.setText("评价");
        }if (status== Mark.ORDER_STATUS_DONE){
            bt.setVisibility(View.INVISIBLE);
        }

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> item = (Map<String, Object>) v.getTag();
                if (onBtnClickListener!=null){
                    onBtnClickListener.onBtnClickListener(item);
                }
            }
        });
    }

    public interface OnBtnClickListener{
        void onBtnClickListener(Map<String, Object> item);
    }
}
