package com.agitation.sportman.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;

import com.agitation.sportman.R;
import com.agitation.sportman.adapter.CourseCatalogAdapter;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.ToastUtils;
import com.agitation.sportman.widget.DropdownButton;
import com.agitation.sportman.widget.DropdownItemObject;
import com.agitation.sportman.widget.DropdownListView;
import com.agitation.sportman.widget.TopicLabelObject;
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
public class CatalogList extends AppCompatActivity {

    private static final int ID_TYPE_ALL = 0;
    private static final int ID_TYPE_MY = 1;
    //title
    private static final String TYPE_ALL = "1V1";

    private static final String LABEL_ALL = "位置";
    private static final int ID_LABEL_ALL = -1;

    private static final String ORDER_REPLY_TIME = "默认";
    private static final String ORDER_PUBLISH_TIME = "发布时间排序";
    private static final String ORDER_HOT = "热门排序";
    private static final int ID_ORDER_REPLY_TIME = 51;
    private static final int ID_ORDER_PUBLISH_TIME = 49;
    private static final int ID_ORDER_HOT = 53;

    ListView catalog_list_lv;
    View mask;
    DropdownButton chooseType, chooseLabel, chooseOrder;
    DropdownListView dropdownType, dropdownLabel, dropdownOrder;

    Animation dropdown_in, dropdown_out, dropdown_mask_out;

    private List<TopicLabelObject> labels = new ArrayList<>();

    private DropdownButtonsController dropdownButtonsController = new DropdownButtonsController();

   //排序的信息
    private Map<String, Object> retData;


    //店铺列表
    private AQuery aq;
    private DataHolder dataHolder;
    private String childCatalogId;
    private List<Map<String,Object>> catalogStoreList;
    private CourseCatalogAdapter storeAdapter;

    //定位操作
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private static final int UPDATE_TIME = 500;
    private String latitude="31.012832";
    private String lontitude="121.411235";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_list);
        Intent intent = getIntent();
        childCatalogId = intent.getStringExtra("childCatalogId");
        initVarible();
        initView();
        getMenuData();
        getStoreInfo();
        getLocation();
    }

    private void initVarible() {
        aq = new AQuery(this);
        dataHolder = DataHolder.getInstance();
        catalogStoreList = new ArrayList<>();
        storeAdapter = new CourseCatalogAdapter(catalogStoreList,this);
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数

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
        catalog_list_lv = (ListView) findViewById(R.id.catalog_list_lv);
        mask = findViewById(R.id.mask);
        chooseType = (DropdownButton) findViewById(R.id.chooseType);
        chooseLabel = (DropdownButton) findViewById(R.id.chooseLabel);
        chooseOrder = (DropdownButton) findViewById(R.id.chooseOrder);
        dropdownType = (DropdownListView) findViewById(R.id.dropdownType);
        dropdownLabel = (DropdownListView) findViewById(R.id.dropdownLabel);
        dropdownOrder = (DropdownListView) findViewById(R.id.dropdownOrder);

        dropdown_in = AnimationUtils.loadAnimation(this, R.anim.dropdown_in);
        dropdown_out = AnimationUtils.loadAnimation(this,R.anim.dropdown_out);
        dropdown_mask_out = AnimationUtils.loadAnimation(this,R.anim.dropdown_mask_out);

        dropdownButtonsController.init();

        //id count name
        TopicLabelObject topicLabelObject1 =  new TopicLabelObject(1,1,"闵行区");
        labels.add(topicLabelObject1);
        TopicLabelObject topicLabelObject2 =new TopicLabelObject(2,1,"黄浦区");
        labels.add(topicLabelObject2);
        TopicLabelObject topicLabelObject3 =new TopicLabelObject(2,1,"虹口区");
        labels.add(topicLabelObject3);
        TopicLabelObject topicLabelObject4 =new TopicLabelObject(2,1,"杨浦区");
        labels.add(topicLabelObject4);
        TopicLabelObject topicLabelObject5 =new TopicLabelObject(2,1,"普陀区");
        labels.add(topicLabelObject5);

        mask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropdownButtonsController.hide();
            }
        });

        dropdownButtonsController.flushCounts();
        dropdownButtonsController.flushAllLabels();
        dropdownButtonsController.flushMyLabels();
        catalog_list_lv.setAdapter(storeAdapter);

        catalog_list_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String courseId = catalogStoreList.get(position).get("id") + "";
                ToastUtils.showToast(CatalogList.this, position + "");
                Intent detailIntent = new Intent(CatalogList.this, CourseDetail.class);
                detailIntent.putExtra("courseId", courseId);
                startActivity(detailIntent);
            }
        });

        storeAdapter.setOnCollectionClickListener(new CourseCatalogAdapter.OnCollectionClickListener() {
            @Override
            public void onCollectionClickListener(Map<String, Object> item, int position) {
                String courseId = item.get("id")+"";
//                ToastUtils.showToast(CatalogList.this,position+"");
                if (item.get("collectionId")==null) {
                    savaCollection(courseId, position);
                } else {
                    String collectionId = item.get("collectionId")+"";
                    deleteCollection(collectionId,position);
                }
                storeAdapter.notifyDataSetChanged();
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
                                ToastUtils.showToast(CatalogList.this, "收藏成功");
                                retData = (Map<String, Object>) info.get("retData");
                                catalogStoreList.get(position).put("collectionId", retData.get("collectionId") + "");
                                storeAdapter.setCourseCatalogInfo(catalogStoreList);
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
                                ToastUtils.showToast(CatalogList.this, "取消收藏成功");
                                catalogStoreList.get(position).remove("collectionId");
                                storeAdapter.setCourseCatalogInfo(catalogStoreList);
                            }
                        }
                    }
                });
    }


    private class DropdownButtonsController implements DropdownListView.Container {
        private DropdownListView currentDropdownList;
        private List<DropdownItemObject> datasetType = new ArrayList<>(2);//全部讨论
        private List<DropdownItemObject> datasetAllLabel = new ArrayList<>();//全部标签
        private List<DropdownItemObject> datasetMyLabel = new ArrayList<>();//我的标签
        private List<DropdownItemObject> datasetLabel = datasetAllLabel;//标签集合   默认是全部标签
        private List<DropdownItemObject> datasetOrder = new ArrayList<>(3);//评论排序

        @Override
        public void show(DropdownListView view) {
            if (currentDropdownList != null) {
                currentDropdownList.clearAnimation();
                currentDropdownList.startAnimation(dropdown_out);
                currentDropdownList.setVisibility(View.GONE);
                currentDropdownList.button.setChecked(false);
            }
            currentDropdownList = view;
            mask.clearAnimation();
            mask.setVisibility(View.VISIBLE);
            currentDropdownList.clearAnimation();
            currentDropdownList.startAnimation(dropdown_in);
            currentDropdownList.setVisibility(View.VISIBLE);
            currentDropdownList.button.setChecked(true);
        }

        @Override
        public void hide() {
            if (currentDropdownList != null) {
                currentDropdownList.clearAnimation();
                currentDropdownList.startAnimation(dropdown_out);
                currentDropdownList.button.setChecked(false);
                mask.clearAnimation();
                mask.startAnimation(dropdown_mask_out);
            }
            currentDropdownList = null;
        }

        @Override
        public void onSelectionChanged(DropdownListView view) {
            if (view == dropdownType) {
                updateLabels(getCurrentLabels());
            }

        }

        void reset() {
            chooseType.setChecked(false);
            chooseLabel.setChecked(false);
            chooseOrder.setChecked(false);

            dropdownType.setVisibility(View.GONE);
            dropdownLabel.setVisibility(View.GONE);
            dropdownOrder.setVisibility(View.GONE);
            mask.setVisibility(View.GONE);

            dropdownType.clearAnimation();
            dropdownLabel.clearAnimation();
            dropdownOrder.clearAnimation();
            mask.clearAnimation();
        }

        void init() {
            reset();
            datasetType.add(new DropdownItemObject(TYPE_ALL, ID_TYPE_ALL, "all"));
            datasetType.add(new DropdownItemObject("1V2", ID_TYPE_MY, "my"));
            datasetType.add(new DropdownItemObject("1V3", ID_TYPE_MY, "my"));
            datasetType.add(new DropdownItemObject("1V4", ID_TYPE_MY, "my"));

            dropdownType.bind(datasetType, chooseType, this, ID_TYPE_ALL);

            datasetAllLabel.add(new DropdownItemObject(LABEL_ALL, ID_LABEL_ALL, null) {
                @Override
                public String getSuffix() {
                    return dropdownType.current == null ? "" : dropdownType.current.getSuffix();
                }
            });
            datasetMyLabel.add(new DropdownItemObject(LABEL_ALL, ID_LABEL_ALL, null));
            datasetLabel = datasetAllLabel;
            dropdownLabel.bind(datasetLabel, chooseLabel, this, ID_LABEL_ALL);

            datasetOrder.add(new DropdownItemObject(ORDER_REPLY_TIME, ID_ORDER_REPLY_TIME, "51"));
            datasetOrder.add(new DropdownItemObject(ORDER_PUBLISH_TIME, ID_ORDER_PUBLISH_TIME, "49"));
            datasetOrder.add(new DropdownItemObject(ORDER_HOT, ID_ORDER_HOT, "53"));
            dropdownOrder.bind(datasetOrder, chooseOrder, this, ID_ORDER_REPLY_TIME);

            dropdown_mask_out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (currentDropdownList == null) {
                        reset();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        private List<DropdownItemObject> getCurrentLabels() {
            return dropdownType.current != null && dropdownType.current.id == ID_TYPE_MY ? datasetMyLabel : datasetAllLabel;
        }

        void updateLabels(List<DropdownItemObject> targetList) {
            if (targetList == getCurrentLabels()) {
                datasetLabel = targetList;
                dropdownLabel.bind(datasetLabel, chooseLabel, this, dropdownLabel.current.id);
            }
        }



        public void flushCounts() {

            datasetType.get(ID_TYPE_ALL).setSuffix(" (" + "5" + ")");
            datasetType.get(ID_TYPE_MY).setSuffix(" (" + "3" + ")");
            dropdownType.flush();
            dropdownLabel.flush();
        }

        void flushAllLabels() {
            flushLabels(datasetAllLabel);
        }

        void flushMyLabels() {
            flushLabels(datasetMyLabel);
        }

        private void flushLabels(List<DropdownItemObject> targetList) {

            while (targetList.size() > 1) targetList.remove(targetList.size() - 1);
            for (int i = 0, n = 5; i < n; i++) {

                int id = labels.get(i).getId();
                String name = labels.get(i).getName();
                if (TextUtils.isEmpty(name)) continue;
                int topicsCount = labels.get(i).getCount();
                // 只有all才做0数量过滤，因为my的返回数据总是0
                if (topicsCount == 0 && targetList == datasetAllLabel) continue;
                DropdownItemObject item = new DropdownItemObject(name, id, String.valueOf(id));
                if (targetList == datasetAllLabel)
                    item.setSuffix("(5)");
                targetList.add(item);
            }
            updateLabels(targetList);
        }
    }
    //获取筛选条件
    private void getMenuData(){
        String url = Mark.getServerIp() + "/api/v1/course/getMenuData";
        aq.transformer(new MapTransformer()).ajax(url, Map.class, new AjaxCallback<Map>(){
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                if (info!=null){
                    if (Boolean.parseBoolean(info.get("result") + "")) {
                        Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                    }
                }
            }
        });
    }
    //获取课程小类商家列表
    private void getStoreInfo(){
        String url = Mark.getServerIp()+ "/api/v1/course/getCourseList";
        Map<String,Object> param = new HashMap<>();
        param.put("childCatalogId",childCatalogId);
        param.put("latitude ",latitude );
        param.put("longitude",lontitude);
        param.put("pageNumber","1");
        param.put("pageSize","10");
        aq.transformer(new MapTransformer()).ajax(url, param, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                if (info != null) {
                    if (Boolean.parseBoolean(info.get("result") + "")) {
                        Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                        catalogStoreList = (List<Map<String, Object>>) retData.get("courseList");
                        storeAdapter.setCourseCatalogInfo(catalogStoreList);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
            mLocationClient = null;
        }
    }
}
