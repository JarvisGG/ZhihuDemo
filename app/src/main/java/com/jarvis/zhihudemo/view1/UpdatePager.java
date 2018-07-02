package com.jarvis.zhihudemo.view1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author yyf @ Zhihu Inc.
 * @since 06-26-2018
 */
public class UpdatePager extends VerticalViewPager {

    public UpdatePager(Context context) {
        super(context);
    }

    public UpdatePager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }
}
