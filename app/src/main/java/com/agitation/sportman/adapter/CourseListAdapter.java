package com.agitation.sportman.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;

import com.agitation.sportman.R;
import com.agitation.sportman.utils.UtilsHelper;
import com.agitation.sportman.utils.ViewHolder;

import java.util.List;
import java.util.Map;

/**
 * Created by Fanxl on 2015/12/26.
 */
public class CourseListAdapter extends CommonAdapter<Map<String, Object>> {

    public CourseListAdapter(Context context, List<Map<String, Object>> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    public void setData(List<Map<String, Object>> mDatas){
        this.mDatas=mDatas;
        this.notifyDataSetChanged();
    }

    private OnCollectionClickListener onCollectionClickListener;
    public interface OnCollectionClickListener{
        void onCollectionClickListener(Map<String, Object> item, int position);
    }

    public void setOnCollectionClickListener(OnCollectionClickListener onCollectionClickListener){
        this.onCollectionClickListener = onCollectionClickListener;
    }

    @Override
    public void convert(final ViewHolder helper, Map<String, Object> item) {
        helper.setText(R.id.course_name, item.get("name")+"");
        helper.setText(R.id.course_address, item.get("address") + "");
        helper.setText(R.id.course_time, item.get("startTime") + "");
        String distanceStr = "";
        if (item.get("distance")!=null && !TextUtils.isEmpty(item.get("distance")+"")){
            double distance = Double.parseDouble(item.get("distance")+"");
            if (distance>1000){
                distanceStr = UtilsHelper.format1Decimal(distance/1000)+"km";
            }else {
                distanceStr = UtilsHelper.format1Decimal(distance) + "m";
            }
        }
        helper.setText(R.id.course_item_distance, distanceStr);
        helper.setText(R.id.course_money, item.get("price") + "");
        ImageButton bt_collection = helper.getView(R.id.bt_collection);
        if (item.get("collectionId")==null){
            bt_collection.setImageResource(R.drawable.collection_icon_normal);
        }else {
            bt_collection.setImageResource(R.drawable.collection_icon_selected);
        }
        bt_collection.setTag(item);
        bt_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> item = (Map<String, Object>) v.getTag();
                if (onCollectionClickListener != null) {
                    onCollectionClickListener.onCollectionClickListener(item, helper.getPosition());
                }
            }
        });
    }
}