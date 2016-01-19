package com.agitation.sportman.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.adapter.CourseListAdapter;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.ToastUtils;
import com.agitation.sportman.widget.ExpandTabView;
import com.agitation.sportman.widget.ScreenView;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

/**
 * Created by fanwl on 2015/11/25.
 */
public class CourseList extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate {

    //店铺列表
    private String childCatalogId;
    private ListView lv_list_course;
    private List<Map<String,Object>> catalogStoreList;
    private CourseListAdapter courseListAdapter;

    //菜单筛选相关
    private ArrayList<View> mViewArrayTest = new ArrayList<>();
    private ExpandTabView expandtab_view;
    private ScreenView timeSv, typeSv, areaSv, sortSv;

    private ScreenView.OnSelectListener selectTestListener = new ScreenView.OnSelectListener() {
        @Override
        public void getValue(Map<String, Object> param, ScreenView view) {
            onRefresh(view, param);
        }
    };

    //页面数据变量
    private Map<String,Object> param; //获取课程需要的参数
    private int currentPage = 1;
    private final int  PAGE_SIZE = 10;
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
        param.put("latitude", dataHolder.getLatitude());
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
        expandtab_view = (ExpandTabView) findViewById(R.id.expandtab_view);
        mRefreshLayout = (BGARefreshLayout)findViewById(R.id.rl_listview_refresh);
        lv_list_course = (ListView) findViewById(R.id.lv_list_course);

//        mRefreshLayout.setCustomHeaderView(menuView, true);

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
                                Map<String, Object> retData = (Map<String, Object>) info.get("retData");
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
                        initMenuData(retData);
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
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        isAutomaticRefresh = true;
        getCourseList();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        return false;
    }

    //筛选相关
    private void initMenuData(Map<String, Object> retData){
        List<Map<String, Object>> timeList = (List<Map<String, Object>>) retData.get("timeList");
        List<Map<String, Object>> typeList = (List<Map<String, Object>>) retData.get("courseTypes");
        List<Map<String, Object>> positionList = (List<Map<String, Object>>) retData.get("areas");
        List<Map<String, Object>> sortList = (List<Map<String, Object>>) retData.get("sortList");

        Map<String, Object> typeClear = new HashMap<>();
        typeClear.put("name", "全部类型");
        typeList.add(0, typeClear);

        timeSv = new ScreenView(this, timeList, -1);
        typeSv = new ScreenView(this, typeList, 0);
        areaSv = new ScreenView(this, positionList, 0);
        sortSv = new ScreenView(this, sortList, 0);

        timeSv.setOnSelectListener(selectTestListener);
        typeSv.setOnSelectListener(selectTestListener);
        areaSv.setOnSelectListener(selectTestListener);
        sortSv.setOnSelectListener(selectTestListener);

        mViewArrayTest.add(timeSv);
        mViewArrayTest.add(typeSv);
        mViewArrayTest.add(areaSv);
        mViewArrayTest.add(sortSv);

        ArrayList<String> mTextArray = new ArrayList<String>();
        mTextArray.add("时间");
        mTextArray.add("类型");
        mTextArray.add("区域");
        mTextArray.add("默认");

        expandtab_view.setValue(mTextArray, mViewArrayTest);
    }

    private void onRefresh(View view, Map<String, Object> item) {
        expandtab_view.onPressBack();
        String showText = item.get("name") + "";
        int position = getPositon(view);
        if (position >= 0 && !expandtab_view.getTitle(position).equals(showText)) {
            expandtab_view.setTitle(showText, position);
            switch (position){
                case 0:
                    param.put("startTime", item.get("startTime"));
                    param.put("endTime", item.get("endTime"));
                    break;
                case 1:
                    param.put("courseTypeId", item.get("id"));
                    break;
                case 2:
                    if (item.get("id")==null){
                        param.remove("areaId");
                        param.put("range", item.get("value"));
                    }else{
                        param.remove("range");
                        param.put("areaId", item.get("id"));
                    }
                    break;
                case 3:
                    param.put("sort", item.get("sort"));
                    break;
            }
            getCourseList();
        }
    }

    private int getPositon(View tView) {
        for (int i = 0; i < mViewArrayTest.size(); i++) {
            if (mViewArrayTest.get(i) == tView) {
                return i;
            }
        }
        return -1;
    }
}
