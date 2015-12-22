package com.agitation.sportman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.agitation.sportman.R;
import com.agitation.sportman.utils.MyViewHolder;
import com.agitation.sportman.utils.UtilsHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by fanwl on 2015/11/26.
 */
public class CourseCatalogAdapter extends BaseAdapter {

    private List<Map<String,Object>> courseCatalogInfoList;
    private Context context;
    private OnCollectionClickListener onCollectionClickListener;

    public CourseCatalogAdapter(List<Map<String,Object>> courseCatalogInfoList,Context context){
        this.courseCatalogInfoList=courseCatalogInfoList;
        this.context=context;
    }

    public void setCourseCatalogInfo(List<Map<String,Object>> courseCatalogInfoList){
        this.courseCatalogInfoList=courseCatalogInfoList;
        notifyDataSetChanged();
    }

    public interface OnCollectionClickListener{
        void onCollectionClickListener(Map<String, Object> item, int position);
    }

    public void setOnCollectionClickListener(OnCollectionClickListener onCollectionClickListener){
        this.onCollectionClickListener = onCollectionClickListener;
    }

    @Override
    public int getCount() {
        return courseCatalogInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return courseCatalogInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view =convertView;
        if (view ==null){
            view = LayoutInflater.from(context).inflate(R.layout.catalog_item,null);
        }
        Map<String,Object> item = courseCatalogInfoList.get(position);
        TextView name = MyViewHolder.get(view,R.id.course_name);
        name.setText(item.get("name")+"");
        TextView address = MyViewHolder.get(view,R.id.course_address);
        address.setText(item.get("address")+"");
        TextView time = MyViewHolder.get(view,R.id.course_time);
        String startTime = item.get("startTime")+"";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = sdf.parse(startTime);
            time.setText(UtilsHelper.formatDateToHour(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TextView distance = MyViewHolder.get(view,R.id.course_distance);
        distance.setText("0");
        TextView money = MyViewHolder.get(view,R.id.course_money);
        money.setText(item.get("price") + "");
        ImageButton bt_collection = MyViewHolder.get(view,R.id.bt_collection);

        if (item.get("collectionId")==null){
            bt_collection.setImageResource(R.drawable.zambia_normal);
        }else {
            bt_collection.setImageResource(R.drawable.zambia_selected);
        }
        bt_collection.setTag(item);
        bt_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> item = (Map<String, Object>) v.getTag();
                if (onCollectionClickListener != null) {
                    onCollectionClickListener.onCollectionClickListener(item, position);
                }
            }
        });
        return view;
    }
}
