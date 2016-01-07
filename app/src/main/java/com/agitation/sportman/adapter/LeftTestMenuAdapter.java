package com.agitation.sportman.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.agitation.sportman.utils.ViewHolder;

import java.util.List;
import java.util.Map;

/**
 * 菜单筛选栏左侧的Adapter
 * Created by Fanxl on 2015/12/26.
 */
public class LeftTestMenuAdapter extends CommonAdapter<Map<String, Object>> {

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

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public LeftTestMenuAdapter(Context context, List<Map<String, Object>> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
        this.context = context;
    }

    public void setData(List<Map<String, Object>> mDatas){
        super.setData(mDatas);
    }

    @Override
    public void convert(ViewHolder helper, Map<String, Object> item) {
        TextView nameTv = (TextView) helper.getConvertView();
        if (helper.getPosition() == selectedPosition){
            nameTv.setBackground(selectedDrawble);
        }else{
            nameTv.setBackground(normalDrawble);//设置未选中状态背景图片
        }
        nameTv.setPadding(20, 0, 0, 0);
        nameTv.setText(item.get("name") + "");
//        nameTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ToastUtils.showToast(context, "click");
//            }
//        });

//        if (item.get("child") != null) {
//            List<Map<String, Object>> secondList = (List<Map<String, Object>>) item.get("child");
//            if (secondList!=null && secondList.size()>0){
//                nameTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right_arrow, 0);
//            }
//        } else {
//            nameTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//        }
    }
}
