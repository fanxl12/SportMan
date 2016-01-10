package com.agitation.sportman.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.utils.ToastUtils;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by fanxl on 2016/1/9 0009.
 */
public class MapActivity extends BaseActivity{

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private BitmapDescriptor bitmap;
	private Marker markerA;
	private InfoWindow mInfoWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext
		//注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.map_activity);
		initToolbar();
		initView();
		initVarible();
	}

	private void initToolbar() {
		if (toolbar!=null){

			title.setText("地址详情");
			setSupportActionBar(toolbar);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	private void initVarible() {
		//定义Maker坐标点
		LatLng point = new LatLng(31.094042, 121.415128);
		//构建Marker图标
		bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_mark);
		//构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions()
				.position(point) //设置marker的位置
				.icon(bitmap) //设置marker图标
				.zIndex(9)  //设置marker所在层级
				.draggable(true);  //设置手势拖拽
		//在地图上添加Marker，并显示
		markerA = (Marker) mBaiduMap.addOverlay(option);
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(point);
		mBaiduMap.setMapStatus(msu);
		mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(final Marker marker) {
				Button button = new Button(getApplicationContext());
				button.setBackgroundResource(R.drawable.popup);
				button.setTextColor(Color.BLACK);
				button.setPadding(10, 10, 10, 10);
				OnInfoWindowClickListener listener = null;
				if (marker==markerA){
					button.setText("金仕堡健身(碧江路店)\n"+"上海市闵行区金平路598号3楼303室");
					listener = new OnInfoWindowClickListener() {
						public void onInfoWindowClick() {
							ToastUtils.showToast(MapActivity.this, "导航");
						}
					};
					LatLng ll = marker.getPosition();
					mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, listener);
					mBaiduMap.showInfoWindow(mInfoWindow);
				}
				return true;
			}
		});
	}

	private void initView() {
		mMapView = (MapView) findViewById(R.id.map_view);
		mBaiduMap = mMapView.getMap();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
		// 回收 bitmap 资源
		bitmap.recycle();
	}
	@Override
	protected void onResume() {
		super.onResume();
		//在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
		//在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}
}
