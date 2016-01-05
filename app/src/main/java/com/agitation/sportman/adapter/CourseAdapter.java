package com.agitation.sportman.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.agitation.sportman.R;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Map;

/**
 * Created by fanwl on 2015/11/1.
 */
public class CourseAdapter extends CommonAdapter<Map<String, Object>> {

    private ImageLoader imageLoader;
    private String imageProfix;

    public CourseAdapter(Context context, List<Map<String, Object>> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
        this.imageLoader=ImageLoader.getInstance();
    }

    public void setData(List<Map<String,Object>> courseList){
        super.setData(courseList);
        this.imageProfix = DataHolder.getInstance().getImageProfix();
    }

    @Override
    public void convert(ViewHolder helper, Map<String, Object> item) {
        ImageView course_image = helper.getView(R.id.course_image);
        String image_url = imageProfix + item.get("url");
        imageLoader.displayImage(image_url, course_image);
        helper.setText(R.id.course_name, item.get("name")+"");
    }
}
