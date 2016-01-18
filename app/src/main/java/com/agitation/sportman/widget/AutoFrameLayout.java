package com.agitation.sportman.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.zhy.autolayout.utils.AutoLayoutHelper;

/**
 * Created by Fanxl on 2016/1/18.
 */
public class AutoFrameLayout extends FrameLayout {

    private final AutoLayoutHelper mHelper = new AutoLayoutHelper(this);

    public AutoFrameLayout(Context context) {
        super(context);
    }

    public AutoFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public AutoFrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new AutoFrameLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!isInEditMode()) {
            mHelper.adjustChildren();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
