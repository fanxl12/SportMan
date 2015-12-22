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

import java.util.List;
import java.util.Map;

/**
 * Created by fanwl on 2015/12/4.
 */
public class CollectionAdapter extends BaseAdapter {

    private List<Map<String ,Object>> collectionList;
    private Context context;
    private OnIconClickListener onIconClickListener;


    public CollectionAdapter(List<Map<String ,Object>> collectionList, Context context){
        this.collectionList=collectionList;
        this.context=context;
    }

    public void setCollectionList(List<Map<String ,Object>> collectionList){
        this.collectionList=collectionList;
        notifyDataSetChanged();
    }

    public void setOnIconClickListener(OnIconClickListener onIconClickListener){
        this.onIconClickListener=onIconClickListener;
    }

    public interface OnIconClickListener{
        void onIconClickListener(Map<String, Object> item, int position);

    }
    @Override
    public int getCount() {
        return collectionList.size();
    }

    @Override
    public Object getItem(int position) {
        return collectionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view =convertView;
        if (view==null){
            view = LayoutInflater.from(context).inflate(R.layout.collection_item,null);
        }
        Map<String, Object> item = collectionList.get(position);
        TextView name = MyViewHolder.get(view,R.id.collection_name);
        TextView address = MyViewHolder.get(view,R.id.collection_address);
        ImageButton icon = MyViewHolder.get(view,R.id.collection_icon);
        name.setText(item.get("name")+"");
        address.setText(item.get("address") + "");

        icon.setImageResource(R.drawable.zambia_selected);
        icon.setTag(item);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> item = (Map<String, Object>) v.getTag();
                if (onIconClickListener!=null){
                    onIconClickListener.onIconClickListener(item, position);
                }
            }
        });
        return view;
    }



}
