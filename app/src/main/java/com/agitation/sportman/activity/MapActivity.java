package com.agitation.sportman.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;

/**
 * Created by fanxl on 2016/1/9 0009.
 */
public class MapActivity extends BaseActivity{

	public static final String MAP_TARGET_NAME = "MAP_TARGET_NAME";
	public static final String MAP_TARGET_ADDRESS = "MAP_TARGET_ADDRESS";
	public static final String MAP_LONGITUDE = "MAP_LONGITUDE";
	public static final String MAP_LATIDUTE = "MAP_LATIDUTE";

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private BitmapDescriptor bitmap;
	private Marker markerA;
	private InfoWindow mInfoWindow;
	private MapStatus ms;
	private LatLng targetLat;
	private String targetName, targetAddress;

	// 定位相关
	private LocationClient mLocClient;
	private MyLocationListenner myListener = new MyLocationListenner();
	private boolean isFirstLoc = true;// 是否首次定位
	private LatLng currentLat;

	//弹出View相关的
	private TextView tv_name, tv_address;
	private View marker_pop_view;
	private OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
		public void onInfoWindowClick() {
			navTips();
		}
	};;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext
		//注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.map_activity);
		initView();
		initVarible();
		initToolbar();
	}

	private void initToolbar() {
		if (toolbar!=null){
			title.setText(targetName);
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

	private void initMaker(){
		//构建Marker图标
		bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark);
		//构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions()
				.position(targetLat) //设置marker的位置
				.icon(bitmap) //设置marker图标
				.zIndex(9)  //设置marker所在层级
				.draggable(true);  //设置手势拖拽
		//在地图上添加Marker，并显示
		markerA = (Marker) mBaiduMap.addOverlay(option);
		MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(ms);
		mBaiduMap.animateMapStatus(msu);
	}

	private void initVarible() {

		targetName = getIntent().getStringExtra(MAP_TARGET_NAME);
		targetAddress = getIntent().getStringExtra(MAP_TARGET_ADDRESS);
		double longitude = getIntent().getDoubleExtra(MAP_LONGITUDE, 0);
		double latitute = getIntent().getDoubleExtra(MAP_LATIDUTE, 0);

		//定义Maker坐标点
		targetLat = new LatLng(longitude, latitute);

		ms = new MapStatus.Builder().target(targetLat).zoom(14).build();
		mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(final Marker marker) {
				if (marker == markerA) {
					tv_name.setText(targetName);
					tv_address.setText(targetAddress);
				}
				LatLng ll = marker.getPosition();
				mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(marker_pop_view), ll, -47, listener);
				mBaiduMap.showInfoWindow(mInfoWindow);
				return true;
			}
		});

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption locationOption = new LocationClientOption();
		locationOption.setOpenGps(true);// 打开gps
		locationOption.setCoorType("bd09ll"); // 设置坐标类型
//		locationOption.setScanSpan(1000);
		mLocClient.setLocOption(locationOption);
		mLocClient.start();
	}

	private void initView() {
		mMapView = (MapView) findViewById(R.id.map_view);
		mBaiduMap = mMapView.getMap();

		marker_pop_view = View.inflate(this, R.layout.map_marker_view, null);
		tv_name = (TextView) marker_pop_view.findViewById(R.id.marker_tv_name);
		tv_address = (TextView) marker_pop_view.findViewById(R.id.marker_tv_address);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
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

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null) {
				initMaker();
				return;
			}
			currentLat = new LatLng(location.getLatitude(), location.getLongitude());
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					.direction(100).latitude(location.getLatitude()) // 此处设置开发者获取到的方向信息，顺时针0-360
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				initMaker();
			}
		}

	}

	/**
	 * 启动百度地图公交路线规划
	 */
	public void startRoutePlanTransit() {
		// 构建 route搜索参数
		RouteParaOption para = new RouteParaOption()
            .startPoint(currentLat)
			.startName("当前位置")
			.endPoint(targetLat)
			.endName(targetName)
			.busStrategyType(RouteParaOption.EBusStrategyType.bus_recommend_way);
		try {
			BaiduMapRoutePlan.openBaiduMapTransitRoute(para, this);
		} catch (Exception e) {
			e.printStackTrace();
			showDialog();
		}
	}

	private void navTips(){
		new MaterialDialog.Builder(this).title("提示")
				.content("要打开百度地图查询公交线路吗？")
				.positiveText("确定")
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
						startRoutePlanTransit();
					}
				})
				.negativeText("取消").build().show();
	}

	/**
	 * 提示未安装百度地图app或app版本过低
	 */
	public void showDialog() {
		new MaterialDialog.Builder(this).title("提示")
				.content("您尚未安装百度地图app或app版本过低，点击确认安装？")
		.positiveText("确定")
		.onPositive(new MaterialDialog.SingleButtonCallback() {
			@Override
			public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
				OpenClientUtil.getLatestBaiduMapApp(MapActivity.this);
			}
		})
		.negativeText("取消").build().show();
	}
}
