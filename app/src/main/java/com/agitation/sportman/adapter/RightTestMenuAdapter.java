package com.agitation.sportman.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.agitation.sportman.utils.ViewHolder;

import java.util.List;
import java.util.Map;

/**
 * Created by Fanxl on 2015/12/26.
 */
public class RightTestMenuAdapter extends CommonAdapter<Map<String, Object>> {

    private int selectedPosition = 0;
    private Drawable selectedDrawble;
    private Drawable normalDrawble;
    private Context context;

    public void setSelectedDrawble(int sId, int nId) {
        this.selectedDrawble = ContextCompat.getDrawable(context, sId);
        this.normalDrawble = ContextCompat.getDrawable(context, nId);
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        this.notifyDataSetChanged();
    }

    public void setSelectedPositionNoNotify(int selectedPosition){
        this.selectedPosition = selectedPosition;
    }

    public void setData(List<Map<String, Object>> mDatas){
        super.setData(mDatas);
    }

    public RightTestMenuAdapter(Context context, List<Map<String, Object>> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
        this.context = context;
    }

    @Override
    public void convert(ViewHolder helper, Map<String, Object> item) {
        TextView nameTv = (TextView) helper.getConvertView();
        if (helper.getPosition() == selectedPosition){
            nameTv.setBackground(selectedDrawble);
        }else{
            nameTv.setBackground(normalDrawble);//设置未选中状态背景图片
        }
        nameTv.setText(item.get("name")+"");
    }
}
