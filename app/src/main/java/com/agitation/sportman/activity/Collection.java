package com.agitation.sportman.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.adapter.CollectionAdapter;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.ToastUtils;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanwl on 2015/12/4.
 */
public class Collection extends BaseActivity {

    private ListView collection_list;
    private CollectionAdapter collectionAdapter;
    private List<Map<String, Object>> collectionList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection);
        initToolbar();
        initVarible();
        initView();
        getCollectionInfo();
    }

    private void initToolbar() {
        if (toolbar!=null){
            title.setText("我的收藏");
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
        collectionList = new ArrayList<>();
        collectionAdapter = new CollectionAdapter(collectionList, this);
    }

    private void initView() {
        collection_list = (ListView) findViewById(R.id.collection_list);
        collection_list.setAdapter(collectionAdapter);
        collection_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.showToast(Collection.this,position+"");
            }
        });
        collectionAdapter.setOnIconClickListener(new CollectionAdapter.OnIconClickListener() {
            @Override
            public void onIconClickListener(Map<String, Object> item, int position) {
                deleteCollection(item.get("id")+"",position);
            }
        });
    }

    //取消收藏
    public void deleteCollection(String collectionId,final int position){
        String url = Mark.getServerIp() + "/api/v1/collect/deleteCourse";
        Map<String, Object> param = new HashMap<>();
        param.put("collectionId", collectionId);
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle())
                .ajax(url, param, Map.class, new AjaxCallback<Map>() {
            @Override
            public void callback(String url, Map info, AjaxStatus status) {
                if (info != null) {
                    if (Boolean.parseBoolean(info.get("result") + "")) {
                        ToastUtils.showToast(Collection.this, "取消收藏成功");
                        collectionList.remove(position);
                        collectionAdapter.setCollectionList(collectionList);
                    }
                }
            }
        });
    }

    public void getCollectionInfo(){
        String url = Mark.getServerIp() + "/api/v1/collect/getCollectList";
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle()).
            ajax(url, Map.class, new AjaxCallback<Map>(){
                    @Override
                public void callback(String url, Map info, AjaxStatus status) {
                if (info!=null){
                    if (Boolean.parseBoolean(info.get("result")+"")){
                        Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                        collectionList = (List<Map<String, Object>>) retData.get("collects");
                        collectionAdapter.setCollectionList(collectionList);
                    }
                }
                }
            });
    }
}
