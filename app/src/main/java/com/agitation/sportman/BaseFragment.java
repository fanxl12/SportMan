package com.agitation.sportman;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by fanwl on 2015/12/30.
 */
public class BaseFragment extends Fragment {

    protected BaseActivity mActivity;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (BaseActivity) activity;
    }

    protected void showLoadingDialog() {
        mActivity.showLoadingDialog();
    }

    protected void dismissLoadingDialog() {
        if (isVisible()) {
            mActivity.dismissLoadingDialog();
        }
    }

}
