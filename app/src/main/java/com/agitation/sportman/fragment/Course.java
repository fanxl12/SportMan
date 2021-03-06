package com.agitation.sportman.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.agitation.sportman.BaseFragment;
import com.agitation.sportman.R;
import com.agitation.sportman.activity.CourseDetail;
import com.agitation.sportman.activity.CourseSubCatalog;
import com.agitation.sportman.activity.PastCourse;
import com.agitation.sportman.activity.WebActivity;
import com.agitation.sportman.adapter.CourseAdapter;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

/**
 * Created by fanwl on 2015/10/25.
 */
public class Course extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate, View.OnClickListener {

    private View rootView;
    private ListView course_lv;
    private CourseAdapter courseAdapter;
    private List<Map<String,Object>> parentCatalogsList;
    private List<Map<String, Object>> pastCourses;
    private AQuery aq;
    private DataHolder dataHolder;
    private BGARefreshLayout mRefreshLayout;
    private boolean isAutomaticRefresh = false;
    private ImageLoader imageLoader;
    private ImageView course_iv_past, course_iv_one, course_iv_two, course_iv_three,course_iv_four,course_iv_five;


    private Handler refreshHandler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==120){
                mRefreshLayout.endRefreshing();
            }
        }
    };

    private View footer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView !=null){
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent!=null)parent.removeView(rootView);
        }else {
            rootView = inflater.inflate(R.layout.course,container,false);
            initView();
            initVarble();
            processLogic();
            course_lv.removeFooterView(footer);
            CourseParentCatalog();
        }
        return rootView;
    }

    private void initVarble() {
        dataHolder = DataHolder.getInstance();
        aq = new AQuery(getContext());
        parentCatalogsList = new ArrayList<>();
        courseAdapter = new CourseAdapter(getActivity(), parentCatalogsList, R.layout.course_item);

        footer = View.inflate(getActivity(), R.layout.course_footer, null);

        course_lv.addFooterView(footer);

        course_lv.setAdapter(courseAdapter);
        course_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), CourseSubCatalog.class);
                intent.putExtra("parentCatalogId", parentCatalogsList.get(position).get("id") + "");
                intent.putExtra("subTitle", parentCatalogsList.get(position).get("name") + "");
                startActivity(intent);
            }
        });
        imageLoader = ImageLoader.getInstance();
        course_iv_past = (ImageView) footer.findViewById(R.id.course_iv_past);
        course_iv_one = (ImageView) footer.findViewById(R.id.course_iv_past_one);
        course_iv_two = (ImageView) footer.findViewById(R.id.course_iv_past_two);
        course_iv_three = (ImageView) footer.findViewById(R.id.course_iv_past_three);
        course_iv_four = (ImageView) footer.findViewById(R.id.course_iv_past_four);
        course_iv_five = (ImageView) footer.findViewById(R.id.course_iv_past_five);
        course_iv_past.setOnClickListener(this);
        course_iv_one.setOnClickListener(this);
        course_iv_two.setOnClickListener(this);
        course_iv_three.setOnClickListener(this);
        course_iv_four.setOnClickListener(this);
        course_iv_five.setOnClickListener(this);
    }

    private void initView() {
        mRefreshLayout = (BGARefreshLayout) rootView.findViewById(R.id.rl_listview_refresh);
        course_lv = (ListView) rootView.findViewById(R.id.course_lv);
    }

    /**
     * 初始化刷新样式
     */
    protected void processLogic() {
        BGAStickinessRefreshViewHolder stickinessRefreshViewHolder = new BGAStickinessRefreshViewHolder(getActivity(), false);
        stickinessRefreshViewHolder.setStickinessColor(R.color.colorPrimary);
        stickinessRefreshViewHolder.setRotateImage(R.mipmap.bga_refresh_stickiness);
        mRefreshLayout.setRefreshViewHolder(stickinessRefreshViewHolder);
        mRefreshLayout.setDelegate(this);
    }

    /*
    获取课程首页广告和课程的数据
     */
    public void CourseParentCatalog(){
        if (!isAutomaticRefresh)mActivity.showLoadingDialog();
        String url = Mark.getServerIp()+"/api/v1/course/getCourseParentCatalog";
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle()).ajax(url, Map.class,
                new AjaxCallback<Map>() {
                    @Override
                    public void callback(String url, Map info, AjaxStatus status) {
                        if (!isAutomaticRefresh) mActivity.dismissLoadingDialog();
                        if (info != null) {
                            if (Boolean.parseBoolean(info.get("result") + "")) {
                                Map<String, Object> retData = (Map<String, Object>) info.get("retData");
                                dataHolder.setImageProfix(retData.get("imageProfix") + "");
                                parentCatalogsList = (List<Map<String, Object>>) retData.get("parentCatalogs");
                                List<Map<String, Object>> adversitementsList = (List<Map<String, Object>>) retData.get("adversitements");
                                pastCourses = (List<Map<String, Object>>) retData.get("pastCourses");
                                courseAdapter.setData(parentCatalogsList);
                                if (isAutomaticRefresh) {
                                    refreshHandler.sendEmptyMessageDelayed(Mark.DATA_REFRESH_SUCCEED, 1000);
                                }
                                addCarouselView(adversitementsList);
                                setCourseFooterInfo();
                            }
                        }
                    }
                });
    }

    private void setCourseFooterInfo() {

        if (pastCourses==null || pastCourses.size()==0){
            return;
        }

        course_lv.addFooterView(footer);
        courseAdapter.notifyDataSetChanged();

        course_iv_past.setVisibility(View.VISIBLE);

        switch (pastCourses.size()){
            case 5:
                course_iv_five.setVisibility(View.VISIBLE);
            case 4:
                course_iv_four.setVisibility(View.VISIBLE);
            case 3:
                course_iv_three.setVisibility(View.VISIBLE);
            case 2:
                course_iv_two.setVisibility(View.VISIBLE);
            case 1:
                course_iv_one.setVisibility(View.VISIBLE);

        }
    }

    /**
     * 添加轮播广告数据
     * @param datas
     */
    private void addCarouselView(List<Map<String, Object>> datas){
        if (datas==null || datas.size()==0)return;
        View headerView = View.inflate(getActivity(), R.layout.view_custom_header, null);
        BGABanner banner = (BGABanner) headerView.findViewById(R.id.banner);
        List<View> views = new ArrayList<>();
        for (final Map<String, Object> item : datas){
            ImageView iv = (ImageView) View.inflate(getActivity(), R.layout.view_image, null);
            imageLoader.displayImage(dataHolder.getImageProfix() + item.get("url")+"", iv);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String webSite = item.get("website") + "";
                    if (!TextUtils.isEmpty(webSite)) {
                        Intent intent = new Intent(getActivity(), WebActivity.class);
                        intent.putExtra(WebActivity.URL_NAME, webSite);
                        startActivity(intent);
                    }
                }
            });
            views.add(iv);
        }
        if (views.size()>1){
            banner.setViews(views);
            mRefreshLayout.setCustomHeaderView(headerView, true);
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        isAutomaticRefresh = true;
        CourseParentCatalog();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), CourseDetail.class);
        intent.putExtra("showPayBtn", false);
        switch (v.getId()){
            case R.id.course_iv_past:
                startActivity(new Intent(getActivity(), PastCourse.class));
            break;
            case R.id.course_iv_past_one:
                intent.putExtra("courseId", pastCourses.get(1).get("id") + "");
                startActivity(intent);
            break;
            case R.id.course_iv_past_two:
                intent.putExtra("courseId", pastCourses.get(2).get("id") + "");
                startActivity(intent);
            break;
            case R.id.course_iv_past_three:
                intent.putExtra("courseId", pastCourses.get(3).get("id") + "");
                startActivity(intent);
            break;
            case R.id.course_iv_past_four:
                intent.putExtra("courseId", pastCourses.get(4).get("id") + "");
                startActivity(intent);
            break;
            case R.id.course_iv_past_five:
                intent.putExtra("courseId", pastCourses.get(5).get("id") + "");
                startActivity(intent);
            break;
        }
    }
}
