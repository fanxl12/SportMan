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
public class CourseAdapter extends BaseAdapter {

    private List<Map<String,Object>> courseList;
    private Context context;
    private ImageLoader imageLoader;
    private LayoutInflater inflater;

    private CourseAdapter(){}

    public CourseAdapter(Context context,List<Map<String,Object>> courseList){
        this.context=context;
        this.courseList=courseList;
        inflater = LayoutInflater.from(context);
        this.imageLoader=ImageLoader.getInstance();
    }
    public void setCourse(List<Map<String,Object>> courseList){
        this.courseList=courseList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public Object getItem(int i) {
        return courseList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view = inflater.inflate(R.layout.course_item,null);
        }
        Map<String,Object> item = courseList.get(i);
        ImageView course_image = MyViewHolder.get(view,R.id.course_image);
        String image_url = item.get("ImaSre")+"";
        imageLoader.displayImage(image_url,course_image);
        TextView course_name = MyViewHolder.get(view,R.id.course_name);
        course_name.setText(item.get("name")+"");
        return view;
    }
}
