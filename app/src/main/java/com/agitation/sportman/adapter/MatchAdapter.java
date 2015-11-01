package com.agitation.sportman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.agitation.sportman.R;
import com.agitation.sportman.utils.MyViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Map;

/**
 * Created by fanwl on 2015/11/1.
 */
public class MatchAdapter extends BaseAdapter {

    private List<Map<String,Object>> matchList;
    private Context context;
    private ImageLoader imageLoader;
    private LayoutInflater inflater;

    private MatchAdapter(){};

    public MatchAdapter(Context context,List<Map<String,Object>> matchList){
        this.context=context;
        this.matchList=matchList;
        inflater=LayoutInflater.from(context);
        this.imageLoader= ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return matchList.size();
    }

    @Override
    public Object getItem(int i) {
        return matchList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view==null){
            view = inflater.inflate(R.layout.course_item,null);
        }
        Map<String,Object> item = matchList.get(i);
        ImageView match_iamge = MyViewHolder.get(view,R.id.course_image);
        String image_url = item.get("ImaSre")+"";
        imageLoader.displayImage(image_url,match_iamge);
        TextView match_name = MyViewHolder.get(view,R.id.course_name);
        match_name.setText(item.get("name")+"");
        return view;
    }
}
