package com.agitation.sportman.adapter;

import android.content.Context;
import android.widget.TextView;

import com.agitation.sportman.R;
import com.agitation.sportman.utils.ViewHolder;

import java.util.List;
import java.util.Map;

/**
 * 菜单筛选栏左侧的Adapter
 * Created by Fanxl on 2015/12/26.
 */
public class LeftMenuAdapter extends CommonAdapter<Map<String, Object>> {

    private int selectedPosition = 0;

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public LeftMenuAdapter(Context context, List<Map<String, Object>> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    public void setData(List<Map<String, Object>> mDatas){
        this.mDatas=mDatas;
        this.notifyDataSetChanged();
    }

    @Override
    public void convert(ViewHolder helper, Map<String, Object> item) {

        //选中和没选中时，设置不同的颜色
        if (helper.getPosition() == selectedPosition){
            helper.getConvertView().setBackgroundResource(R.color.popup_right_bg);
        }else{
            helper.getConvertView().setBackgroundResource(R.drawable.selector_left_normal);
        }

        TextView nameTv = helper.getView(R.id.left_item_name);
        nameTv.setText(item.get("name")+"");

        if (item.get("child") != null) {
            List<Map<String, Object>> secondList = (List<Map<String, Object>>) item.get("child");
            if (secondList!=null && secondList.size()>0){
                nameTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right_arrow, 0);
            }
        } else {
            nameTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }
}
