package com.agitation.sportman;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by fanwl on 2015/12/30.
 */
public class BaseFragment extends Fragment {

    protected BaseActivity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity){
            mActivity = (BaseActivity) context;
        }
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
