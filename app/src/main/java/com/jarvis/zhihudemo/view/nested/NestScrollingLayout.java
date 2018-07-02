package com.jarvis.zhihudemo.view.nested;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/3/16 下午6:42
 * @changeRecord [修改记录] <br/>
 */

public class NestScrollingLayout extends FrameLayout implements NestedScrollingParent {

    private static final String TAG = "NestScrollingLayout";

    private NestedScrollingParentHelper mParentHelper;

    public NestScrollingLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public NestScrollingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestScrollingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NestScrollingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @SuppressLint("NewApi") private void init() {
        mParentHelper = new NestedScrollingParentHelper(this);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.d(TAG, "child==target:" + (child == target));

        Log.d(TAG, "----父布局onStartNestedScroll---------------- child -> " + child.toString() + " target -> " + target.toString());

        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        Log.d(TAG, "----父布局onNestedScrollAccepted--------------- child -> " + child.toString() + " target -> " + target.toString());
        mParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onStopNestedScroll(View child) {
        Log.d(TAG, "----父布局onStopNestedScroll---------------- target -> " + child.toString());
        mParentHelper.onStopNestedScroll(child);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.d(TAG, "----父布局onNestedScroll---------------- target -> " + target.toString());
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        scrollBy(0, -dy);

        consumed[0] = 0;
        consumed[1] = 10; // 把消费的距离放进去
        Log.d(TAG, "----父布局onNestedPreScroll----------------");
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.d(TAG, "----父布局onNestedFling---------------- target -> " + target.toString());
        return true;
    }

    @Override
    public int getNestedScrollAxes() {
        Log.d(TAG, "----父布局getNestedScrollAxes----------------");
        return mParentHelper.getNestedScrollAxes();
    }
}