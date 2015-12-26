package com.agitation.sportman.activity;

import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.adapter.CourseListAdapter;
import com.agitation.sportman.adapter.LeftMenuAdapter;
import com.agitation.sportman.adapter.RightMenuAdapter;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.ScreenUtils;
import com.agitation.sportman.utils.ToastUtils;
import com.agitation.sportman.widget.DropdownButton;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanwl on 2015/11/25.
 */
public class CourseList extends BaseActivity implements View.OnClickListener {

    private ListView lv_list_course;
    View choose_bg;
    DropdownButton choose_db_time, choose_db_type, choose_db_positon, choose_db_sort;
    DropdownButton currentDb;
    Animation dropdown_in, dropdown_out, dropdown_mask_out;
    //排序的信息
    private Map<String, Object> retData;

    //店铺列表
    private AQuery aq;
    private DataHolder dataHolder;
    private String childCatalogId;
    private List<Map<String,Object>> catalogStoreList;
    private CourseListAdapter courseListAdapter;

    //定位操作
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private static final int UPDATE_TIME = 500;
    private String latitude="31.012832";
    private String lontitude="121.411235";

    //菜单筛选相关
    /**使用PopupWindow显示一级分类和二级分类*/
    private PopupWindow popupWindow;
    /**左侧和右侧两个ListView*/
    private ListView left_menu, right_menu;
    private LeftMenuAdapter leftMenuAdapter;
    private RightMenuAdapter rightMenuAdapter;
    private List<Map<String, Object>> rightDataList, leftDataList;
    private List<Map<String, Object>> timeList, typeList, positionList, sortList;

    //页面数据变量
    private Map<String,Object> param; //获取课程需要的参数
    private int currentPage = 1;
    private final int  PAGE_SIZE = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_list);
        Intent intent = getIntent();
        childCatalogId = intent.getStringExtra("childCatalogId");
        initToolbar();
        initVarible();
        initView();
        initPopup();
        getMenuData();
        getCourseList();
        getLocation();
    }


    private void initToolbar() {
        if (toolbar!=null){
            title.setText("正手");
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
        param = new HashMap<>();
        param.put("childCatalogId",childCatalogId);
        param.put("pageSize", PAGE_SIZE);
        param.put("latitude", latitude);
        param.put("longitude",lontitude);

        aq = new AQuery(this);
        dataHolder = DataHolder.getInstance();
        catalogStoreList = new ArrayList<>();
        courseListAdapter = new CourseListAdapter(this, catalogStoreList, R.layout.catalog_item);
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数

        //设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);        //是否打开GPS
        option.setCoorType("bd09ll");       //设置返回值的坐标类型。
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setProdName("LocationDemo"); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(UPDATE_TIME);    //设置定时定位的时间间隔。单位毫秒
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mLocationClient.setLocOption(option);
    }

    private void initView() {
        lv_list_course = (ListView) findViewById(R.id.lv_list_course);
        choose_bg = findViewById(R.id.choose_bg);
        choose_db_time = (DropdownButton) findViewById(R.id.choose_db_time);
        choose_db_type = (DropdownButton) findViewById(R.id.choose_db_type);
        choose_db_positon = (DropdownButton) findViewById(R.id.choose_db_positon);
        choose_db_sort = (DropdownButton) findViewById(R.id.choose_db_sort);
        choose_db_time.setText("时间");
        choose_db_time.setChecked(false);
        choose_db_type.setText("类型");
        choose_db_type.setChecked(false);
        choose_db_positon.setText("全城");
        choose_db_positon.setChecked(false);
        choose_db_sort.setText("默认");
        choose_db_sort.setChecked(false);
        choose_db_time.setOnClickListener(this);
        choose_db_type.setOnClickListener(this);
        choose_db_positon.setOnClickListener(this);
        choose_db_sort.setOnClickListener(this);

        dropdown_in = AnimationUtils.loadAnimation(this, R.anim.dropdown_in);
        dropdown_out = AnimationUtils.loadAnimation(this,R.anim.dropdown_out);
        dropdown_mask_out = AnimationUtils.loadAnimation(this,R.anim.dropdown_mask_out);


        lv_list_course.setAdapter(courseListAdapter);

        lv_list_course.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String courseId = catalogStoreList.get(position).get("id") + "";
                ToastUtils.showToast(CourseList.this, position + "");
                Intent detailIntent = new Intent(CourseList.this, CourseDetail.class);
                detailIntent.putExtra("courseId", courseId);
                startActivity(detailIntent);
            }
        });

        courseListAdapter.setOnCollectionClickListener(new CourseListAdapter.OnCollectionClickListener() {
            @Override
            public void onCollectionClickListener(Map<String, Object> item, int position) {
                String courseId = item.get("id") + "";
                if (item.get("collectionId") == null) {
                    savaCollection(courseId, position);
                } else {
                    String collectionId = item.get("collectionId") + "";
                    deleteCollection(collectionId, position);
                }
                courseListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initPopup() {
        popupWindow = new PopupWindow(this);
        View view = LayoutInflater.from(this).inflate(R.layout.menu_popup_layout, null);
        left_menu = (ListView) view.findViewById(R.id.pop_listview_left);
        right_menu = (ListView) view.findViewById(R.id.pop_listview_right);

        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(new PaintDrawable());
        popupWindow.setFocusable(true);

        popupWindow.setHeight(ScreenUtils.getScreenH(this) * 2 / 3);
        popupWindow.setWidth(ScreenUtils.getScreenW(this));

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                choose_bg.startAnimation(dropdown_mask_out);
                choose_bg.setVisibility(View.GONE);

                left_menu.setSelection(0);
                right_menu.setSelection(0);
                currentDb.setChecked(false);
            }
        });

        leftDataList = new ArrayList<>();
        rightDataList = new ArrayList<>();

        leftMenuAdapter = new LeftMenuAdapter(this, leftDataList, R.layout.left_menu_item);
        rightMenuAdapter = new RightMenuAdapter(this, rightDataList, R.layout.right_menu_item);

        //为了方便扩展，这里的两个ListView均使用BaseAdapter.如果分类名称只显示一个字符串，建议改为ArrayAdapter.
        //加载一级分类
        left_menu.setAdapter(leftMenuAdapter);

        //加载左侧第一行对应右侧二级分类
        right_menu.setAdapter(rightMenuAdapter);

        //左侧ListView点击事件
        left_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (leftDataList.get(position).get("child") != null) {
                    List<Map<String, Object>> secondList = (List<Map<String, Object>>) leftDataList.get(position).get("child");
                    //二级数据
                    if (secondList != null && secondList.size() > 0) {
                        LeftMenuAdapter adapter = (LeftMenuAdapter) (parent.getAdapter());
                        //如果上次点击的就是这一个item，则不进行任何操作
                        if (adapter.getSelectedPosition() == position) {
                            return;
                        }
                        //根据左侧一级分类选中情况，更新背景色
                        adapter.setSelectedPosition(position);
                        adapter.notifyDataSetChanged();
                        //显示右侧二级分类
                        updateRightMernu(secondList, rightMenuAdapter);
                    }
                    return;
                }

                switch (currentDb.getId()){
                    case R.id.choose_db_sort:
                        param.put("sort", leftDataList.get(position).get("sort"));
                        currentDb.setText(leftDataList.get(position).get("name") + "");
                        break;
                    case R.id.choose_db_positon:
                        param.put("areaId", leftDataList.get(position).get("id"));
                        currentDb.setText(leftDataList.get(position).get("name") + "");
                        break;
                    case R.id.choose_db_type:
                        param.put("courseTypeId", leftDataList.get(position).get("id"));
                        currentDb.setText(leftDataList.get(position).get("name") + "");
                        break;
                }

                getCourseList();

                //如果没有二级类，则直接跳转
                popupWindow.dismiss();

                String selectedName = leftDataList.get(position).get("name")+"";
                Toast.makeText(CourseList.this, selectedName, Toast.LENGTH_SHORT).show();
            }
        });

        //右侧ListView点击事件
        right_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //关闭popupWindow，显示用户选择的分类
                popupWindow.dismiss();
                int leftPosition = leftMenuAdapter.getSelectedPosition();
                List<Map<String, Object>> rightList = (List<Map<String, Object>>) leftDataList.get(leftPosition).get("child");

                switch (currentDb.getId()){
                    case R.id.choose_db_time:
                        param.put("startTime", rightList.get(position).get("startTime"));
                        param.put("endTime", rightList.get(position).get("endTime"));
                        currentDb.setText(rightList.get(position).get("showName")+"");
                        break;
                    case R.id.choose_db_positon:
                        if (position==0){
                            param.remove("areaId");
                        }
                        param.put("range", rightList.get(position).get("value"));
                        currentDb.setText(rightList.get(position).get("name")+"");
                        break;
                }
                getCourseList();
            }
        });
    }

    //收藏操作
    public void savaCollection(final String Id,final int position){
        String url = Mark.getServerIp() + "/api/v1/collect/save";
        Map<String, Object> param = new HashMap<>();
        param.put("courseId",Id);
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle())
                .ajax(url, param, Map.class, new AjaxCallback<Map>(){
                    @Override
                    public void callback(String url, Map info, AjaxStatus status) {
                        if (info!=null){
                            if (Boolean.parseBoolean(info.get("result")+"")){
                                ToastUtils.showToast(CourseList.this, "收藏成功");
                                retData = (Map<String, Object>) info.get("retData");
                                catalogStoreList.get(position).put("collectionId", retData.get("collectionId") + "");
                                courseListAdapter.setData(catalogStoreList);
                            }
                        }
                    }
                });
    }
    //取消收藏
    public void deleteCollection(String collectionId,final int position){
        String url = Mark.getServerIp() + "/api/v1/collect/deleteCourse";
        Map<String, Object> param = new HashMap<>();
        param.put("collectionId",collectionId);
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle())
                .ajax(url, param, Map.class, new AjaxCallback<Map>() {
                    @Override
                    public void callback(String url, Map info, AjaxStatus status) {
                        if (info != null) {
                            if (Boolean.parseBoolean(info.get("result") + "")) {
                                ToastUtils.showToast(CourseList.this, "取消收藏成功");
                                catalogStoreList.get(position).remove("collectionId");
                                courseListAdapter.setData(catalogStoreList);
                            }
                        }
                    }
                });
    }

    //获取筛选条件
    private void getMenuData(){
        String url = Mark.getServerIp() + "/api/v1/course/getMenuData";
        aq.transformer(new MapTransformer()).ajax(url, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                if (info != null) {
                    if (Boolean.parseBoolean(info.get("result") + "")) {
                        Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                        timeList = (List<Map<String, Object>>) retData.get("timeList");
                        typeList = (List<Map<String, Object>>) retData.get("courseTypes");
                        positionList = (List<Map<String, Object>>) retData.get("areas");
                        sortList = (List<Map<String, Object>>) retData.get("sortList");
                    }
                }
            }
        });
    }
    //获取课程列表
    private void getCourseList(){
        String url = Mark.getServerIp()+ "/api/v1/course/getCourseList";
        param.put("pageNumber", currentPage);
        aq.transformer(new MapTransformer()).ajax(url, param, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                if (info != null) {
                    if (Boolean.parseBoolean(info.get("result") + "")) {
                        Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                        catalogStoreList = (List<Map<String, Object>>) retData.get("courseList");
                        courseListAdapter.setData(catalogStoreList);
                    }
                }
            }
        });
    }

    public void getLocation(){
        if(mLocationClient == null)return;
        if (mLocationClient.isStarted()){
            mLocationClient.requestLocation();
        }else{
            mLocationClient.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.choose_db_time:
                leftDataList = timeList;
                dropDownClick(v);
                break;
            case R.id.choose_db_type:
                leftDataList = typeList;
                dropDownClick(v);
                break;
            case R.id.choose_db_positon:
                leftDataList = positionList;
                dropDownClick(v);
                break;
            case R.id.choose_db_sort:
                leftDataList = sortList;
                dropDownClick(v);
                break;
        }
    }

    private void dropDownClick(View v){
        currentDb = (DropdownButton)v;

        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            currentDb.setChecked(true);

            leftMenuAdapter.setData(leftDataList);
            List<Map<String, Object>> rightDatas = (List<Map<String, Object>>) leftDataList.get(0).get("child");
            if (rightDatas==null || rightDatas.size()==0){
                right_menu.setVisibility(View.GONE);
            }else{
                right_menu.setVisibility(View.VISIBLE);
                rightMenuAdapter.setData(rightDatas);
            }

            popupWindow.showAsDropDown(v);
            popupWindow.setAnimationStyle(-1);
            //背景变暗
            choose_bg.startAnimation(dropdown_in);
            choose_bg.setVisibility(View.VISIBLE);
        }

    }

    //定位操作
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
            }
            System.out.println(sb.toString());
        }
    }

    //刷新右侧ListView
    private void updateRightMernu(List<Map<String, Object>> rigtData, RightMenuAdapter rightAdapter) {
        rightDataList.clear();
        rightDataList.addAll(rigtData);
        rightAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
            mLocationClient = null;
        }
    }


}
