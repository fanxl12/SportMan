package com.agitation.sportman.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.adapter.CourseListAdapter;
import com.agitation.sportman.adapter.LeftMenuAdapter;
import com.agitation.sportman.adapter.RightMenuAdapter;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.ScreenUtils;
import com.agitation.sportman.utils.ToastUtils;
import com.agitation.sportman.widget.DropdownButton;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

/**
 * Created by fanwl on 2015/11/25.
 */
public class CourseList extends BaseActivity implements View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate {

    private ListView lv_list_course;
    View choose_bg;
    DropdownButton choose_db_time, choose_db_type, choose_db_positon, choose_db_sort;
    DropdownButton currentDb;
    Animation dropdown_in, dropdown_out, dropdown_mask_out;
    //排序的信息
    private Map<String, Object> retData;

    //店铺列表
    private String childCatalogId;
    private List<Map<String,Object>> catalogStoreList;
    private CourseListAdapter courseListAdapter;

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
    private BGARefreshLayout mRefreshLayout;

    private boolean isAutomaticRefresh = false;

    private Handler refreshHandler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==120){
                mRefreshLayout.endRefreshing();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_list);
        Intent intent = getIntent();
        childCatalogId = intent.getStringExtra("childCatalogId");
        String childCatalogName = intent.getStringExtra("childCatalogName");
        initToolbar(childCatalogName);
        initVarible();
        initView();
        processLogic();
        initPopup();
        getMenuData();
        getCourseList();
    }


    private void initToolbar(String name) {
        if (toolbar!=null){
            title.setText(name);
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
        param.put("latitude",  dataHolder.getLatitude());
        param.put("longitude", dataHolder.getLongitude());

        catalogStoreList = new ArrayList<>();
        courseListAdapter = new CourseListAdapter(this, catalogStoreList, R.layout.catalog_item);
    }

    protected void processLogic() {
        BGAStickinessRefreshViewHolder stickinessRefreshViewHolder = new BGAStickinessRefreshViewHolder(this, false);
        stickinessRefreshViewHolder.setStickinessColor(R.color.colorPrimary);
        stickinessRefreshViewHolder.setRotateImage(R.mipmap.bga_refresh_stickiness);
        mRefreshLayout.setRefreshViewHolder(stickinessRefreshViewHolder);
        mRefreshLayout.setDelegate(this);
    }

    private void initView() {

        mRefreshLayout = (BGARefreshLayout)findViewById(R.id.rl_listview_refresh);
        lv_list_course = (ListView) findViewById(R.id.lv_list_course);
        choose_bg = findViewById(R.id.choose_bg);

        View menuView = View.inflate(this, R.layout.incould_screen_menu, null);

        mRefreshLayout.setCustomHeaderView(menuView, true);

        choose_db_time = (DropdownButton) menuView.findViewById(R.id.choose_db_time);
        choose_db_type = (DropdownButton) menuView.findViewById(R.id.choose_db_type);
        choose_db_positon = (DropdownButton) menuView.findViewById(R.id.choose_db_positon);
        choose_db_sort = (DropdownButton) menuView.findViewById(R.id.choose_db_sort);
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
                Intent detailIntent = new Intent(CourseList.this, CourseDetail.class);
                detailIntent.putExtra("courseId", courseId);
                startActivity(detailIntent);
            }
        });

        courseListAdapter.setOnCollectionClickListener(new CourseListAdapter.OnCollectionClickListener() {
            @Override
            public void onCollectionClickListener(Map<String, Object> item, int position) {
                if (dataHolder.isLogin()){
                    String courseId = item.get("id") + "";
                    if (item.get("collectionId") == null) {
                        savaCollection(courseId, position);
                    } else {
                        String collectionId = item.get("collectionId") + "";
                        deleteCollection(collectionId, position);
                    }
                    courseListAdapter.notifyDataSetChanged();
                }else {
                    goTOLogin();
                }
            }
        });
    }

    /**
     * 收藏前没有登录的操作
     */
    private void goTOLogin(){
        new AlertDialogWrapper.Builder(this)
                .setMessage("请登录")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       Intent intent = new Intent(CourseList.this, Login.class);
                        intent.putExtra("isNormalLogin", false);
                        startActivity(intent);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

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

//                choose_bg.startAnimation(dropdown_mask_out);
//                choose_bg.setVisibility(View.GONE);

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

                switch (currentDb.getId()) {
                    case R.id.choose_db_time:
                        param.put("startTime", rightList.get(position).get("startTime"));
                        param.put("endTime", rightList.get(position).get("endTime"));
                        currentDb.setText(rightList.get(position).get("showName") + "");
                        break;
                    case R.id.choose_db_positon:
                        if (position == 0) {
                            param.remove("areaId");
                        }
                        param.put("range", rightList.get(position).get("value"));
                        currentDb.setText(rightList.get(position).get("name") + "");
                        break;
                }
                getCourseList();
            }
        });
    }

    /**
     * @param Id
     * @param position
     * 收藏操作
     */
    public void savaCollection(final String Id,final int position){
        String url = Mark.getServerIp() + "/api/v1/collect/save";
        Map<String, Object> param = new HashMap<>();
        param.put("courseId",Id);
        showLoadingDialog();
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle())
                .ajax(url, param, Map.class, new AjaxCallback<Map>() {
                    @Override
                    public void callback(String url, Map info, AjaxStatus status) {
                        dismissLoadingDialog();
                        if (info != null) {
                            if (Boolean.parseBoolean(info.get("result") + "")) {
                                ToastUtils.showToast(CourseList.this, "收藏成功");
                                retData = (Map<String, Object>) info.get("retData");
                                catalogStoreList.get(position).put("collectionId", retData.get("collectionId") + "");
                                courseListAdapter.setData(catalogStoreList);
                            }
                        }
                    }
                });
    }

    /**
     * @param collectionId
     * @param position
     * 取消收藏操作
     */
    public void deleteCollection(String collectionId,final int position){
        String url = Mark.getServerIp() + "/api/v1/collect/deleteCourse";
        Map<String, Object> param = new HashMap<>();
        param.put("collectionId",collectionId);
        showLoadingDialog();
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle())
                .ajax(url, param, Map.class, new AjaxCallback<Map>() {
                    @Override
                    public void callback(String url, Map info, AjaxStatus status) {
                        dismissLoadingDialog();
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
    /**
     * 获取筛选条件信息
     */
    private void getMenuData(){
        String url = Mark.getServerIp() + "/api/v1/course/getMenuData";
        showLoadingDialog();
        aq.transformer(new MapTransformer()).ajax(url, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                dismissLoadingDialog();
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

    /**
     * 获取课程列表
     */
    private void getCourseList(){
        String url = Mark.getServerIp()+ "/api/v1/course/getCourseList";
        param.put("pageNumber", currentPage);
        if (dataHolder.isLogin()){
            param.put("userId", dataHolder.getUserData().get("id"));
        }
        showLoadingDialog();
        aq.transformer(new MapTransformer()).ajax(url, param, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                dismissLoadingDialog();
                if (info != null) {
                    if (Boolean.parseBoolean(info.get("result") + "")) {
                        Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                        catalogStoreList = (List<Map<String, Object>>) retData.get("courseList");
                        courseListAdapter.setData(catalogStoreList);
                        if (isAutomaticRefresh)refreshHandler.sendEmptyMessageDelayed(Mark.DATA_REFRESH_SUCCEED, 1000);
                    }
                }
            }
        });
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
//            choose_bg.startAnimation(dropdown_in);
//            choose_bg.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        isAutomaticRefresh = true;
        getCourseList();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        return false;
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
}
