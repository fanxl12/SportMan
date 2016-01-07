package com.agitation.sportman.widget;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.agitation.sportman.R;
import com.agitation.sportman.adapter.LeftTestMenuAdapter;
import com.agitation.sportman.adapter.RightTestMenuAdapter;
import com.agitation.sportman.inter.ViewBaseAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScreenView extends LinearLayout implements ViewBaseAction {

	private ListView left_menu_lv;
	private ListView right_menu_lv;
	private List<Map<String, Object>> leftMenuList, rightMenuList;
	private LeftTestMenuAdapter leftMenuAdapter;
	private RightTestMenuAdapter rightMenuAdapter;
	private int tEaraPosition = 0;
	private int tBlockPosition = 0;
	private OnSelectListener mOnSelectListener;

	public ScreenView(Context context, List<Map<String, Object>> leftMenuList) {
		super(context);
		this.leftMenuList=leftMenuList;
		init(context);
	}

	public ScreenView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_region, this, true);
		left_menu_lv = (ListView) findViewById(R.id.listView);
		right_menu_lv = (ListView) findViewById(R.id.listView2);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			setBackground(ContextCompat.getDrawable(context, R.drawable.choosearea_bg_mid));
		}
		leftMenuAdapter = new LeftTestMenuAdapter(context, leftMenuList, R.layout.choose_item);
		leftMenuAdapter.setSelectedDrawble(R.drawable.choose_item_selected, R.drawable.choose_eara_item_selector);
		leftMenuAdapter.setSelectedPositionNoNotify(tEaraPosition);
		left_menu_lv.setAdapter(leftMenuAdapter);
		left_menu_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {


			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				leftMenuAdapter.setSelectedPosition(position);
				List<Map<String, Object>> rightDatas = (List<Map<String, Object>>) leftMenuList.get(position).get("child");
				if (rightDatas != null && rightDatas.size() > 0) {
					rightMenuAdapter.setData(rightDatas);
				}
			}
		});

		if (leftMenuList!=null && leftMenuList.size()>0){
			rightMenuList = (List<Map<String, Object>>) leftMenuList.get(0).get("child");
		}
		if (rightMenuList==null)rightMenuList = new ArrayList<>();

		rightMenuAdapter = new RightTestMenuAdapter(context, rightMenuList, R.layout.choose_item);
		rightMenuAdapter.setSelectedDrawble(R.drawable.choose_item_right, R.drawable.choose_plate_item_selector);
		rightMenuAdapter.setSelectedPositionNoNotify(tBlockPosition);
		right_menu_lv.setAdapter(rightMenuAdapter);
		right_menu_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				rightMenuAdapter.setSelectedPosition(position);
//				mOnSelectListener.getValue();
			}
		});

		setDefaultSelect();
	}

	public void setDefaultSelect() {
		left_menu_lv.setSelection(tEaraPosition);
		right_menu_lv.setSelection(tBlockPosition);
	}

	public void setOnSelectListener(OnSelectListener onSelectListener) {
		mOnSelectListener = onSelectListener;
	}

	public interface OnSelectListener {
		void getValue(Map<String, Object> param);
	}

	@Override
	public void hide() {

	}

	@Override
	public void show() {

	}
}
