package com.jarvis.zhihudemo.view1;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.AttributeSet;
import android.view.VelocityTracker;
import android.view.View;
import android.webkit.WebView;
import android.widget.Scroller;

import com.jarvis.zhihudemo.view.hybrid.WebViewS;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;

import static com.jarvis.zhihudemo.view1.ScrollRecyclerView.BROUND;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/4/2 下午3:49
 * @changeRecord [修改记录] <br/>
 */

public class VerticalPager extends VerticalViewPager implements NestedScrollingParent {


    private NestedScrollingParentHelper mNestedScrollingParentHelper;

    public VerticalPager(Context context) {
        super(context);
        init();
    }

    public VerticalPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if (target instanceof NestedScrollingChild) {
            return true;
        }
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(target, dx, dy, consumed);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (velocityX == BROUND) {

            mScroller.fling(0, getScrollY(), 0, (int) velocityY,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            invalidate();
//            int currentPage = getCurrentItem();
//            int nextPage = determineTargetPage(currentPage, 0, (int) velocityY,
//                    ExplosionUtils.getScreenSizeY(getContext()) / 2);
//            setCurrentItemInternal(nextPage, true, true, (int) velocityY);
//            if (velocityY > 0) {
//                setCurrentItemInternal(currentPage + 1, true, true, (int) velocityY);
//            } else {
//                setCurrentItemInternal(currentPage - 1, true, true, (int) velocityY);
//            }
        }
        return false;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {

        return false;
    }
}
