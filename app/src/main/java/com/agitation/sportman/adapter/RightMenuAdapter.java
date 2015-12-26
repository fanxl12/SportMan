package com.agitation.sportman.adapter;

import android.content.Context;

import com.agitation.sportman.R;
import com.agitation.sportman.utils.ViewHolder;

import java.util.List;
import java.util.Map;

/**
 * Created by Fanxl on 2015/12/26.
 */
public class RightMenuAdapter extends CommonAdapter<Map<String, Object>> {

    public void setData(List<Map<String, Object>> mDatas){
        this.mDatas=mDatas;
        this.notifyDataSetChanged();
    }

    public RightMenuAdapter(Context context, List<Map<String, Object>> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder helper, Map<String, Object> item) {
        helper.setText(R.id.right_item_name, item.get("name")+"");
    }
}
