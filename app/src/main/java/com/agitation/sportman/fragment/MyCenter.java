package com.agitation.sportman.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agitation.sportman.R;
import com.agitation.sportman.activity.CenterDataEdit;
import com.agitation.sportman.activity.Login;
import com.agitation.sportman.widget.CircleImageView;

/**
 * Created by fanwl on 2015/10/25.
 */
public class MyCenter extends Fragment implements View.OnClickListener {

    private View rootView;
    private CircleImageView mycenter_head;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) parent.removeView(rootView);
        } else {
            rootView = inflater.inflate(R.layout.my_center, container, false);
            initView();
        }
        return rootView;
    }

    private void initView() {
        mycenter_head = (CircleImageView) rootView.findViewById(R.id.mycenter_head);
        mycenter_head.setOnClickListener(this);
        rootView.findViewById(R.id.mycenter_data_edit).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mycenter_head:
                startActivity(new Intent(getActivity(), Login.class));
                break;
            case R.id.mycenter_data_edit:
                startActivity(new Intent(getActivity(), CenterDataEdit.class));
                break;

        }
    }
}

