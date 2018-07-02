package com.jarvis.zhihudemo.view.nested;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.jarvis.zhihudemo.view.VerticalViewPager2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/3/19 下午2:35
 * @changeRecord [修改记录] <br/>
 */

public class NestScrollingViewPager extends VerticalViewPager2 implements NestedScrollingParent {

    private static final String TAG = "NScrollView";

    private int maxDistance = 0;
    private int totalHeight = 0;

    private List<View> nestedScrollingChildList = new ArrayList<>();
    private NestedScrollingParentHelper mParentHelper;

    public NestScrollingViewPager(Context context) {
        super(context, null);
    }

    public NestScrollingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        mParentHelper = new NestedScrollingParentHelper(this);
    }

    @SuppressLint("NewApi") private void init() {
        mParentHelper = new NestedScrollingParentHelper(this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        int parentHeight = getMeasuredHeight();
        int top = t;
        int lastChildHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child =  getChildAt(i);
            VerticalViewPager2.LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            if (layoutParams.height == LayoutParams.MATCH_PARENT) {
                layoutParams.height = parentHeight;
            } else {
                int childMeasuredHeight = child.getMeasuredHeight();
                layoutParams.height = childMeasuredHeight;
            }
            child.setLayoutParams(layoutParams);
            child.layout(l, top, r, top + layoutParams.height);
            top += layoutParams.height;
            lastChildHeight = layoutParams.height;
        }
        maxDistance = top - lastChildHeight;
        totalHeight = top;
        //viewGroup.layout(l, t, r, top);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        nestedScrollingChildList.clear();
        for (int i = 0; i < getChildCount(); i++) {
            View child =  getChildAt(i);
            nestedScrollingChildList.add(child);
        }
    }

    private void consumeEvent(int dx, int dy, int[] consumed) {
        scrollBy(dx, dy);
        consumed[0] = 0;
        consumed[1] = dy;
    }

    public int getCurrentWrapline(View target) {
        int line = 0;
        for (View view : nestedScrollingChildList) {
            if (view == target) {
                return line;
            }
            line += view.getHeight();
        }
        return line;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int y = mScroller.getCurrY();
            scrollTo(0, y);
            invalidate();
        }
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
        int scrollY = getScrollY();
        if (scrollY + dyUnconsumed < 0 || scrollY + dyUnconsumed > maxDistance) {
            return;
        }
        scrollBy(dxUnconsumed, dyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        Log.d(TAG, "----父布局onNestedPreScroll----------------");
        int scrollY = getScrollY();
        int line = getCurrentWrapline(target);
        if (target instanceof NestScrollWebView) {
            boolean targetScrollDown = target.canScrollVertically(1);
            boolean targetScrollUp = target.canScrollVertically(-1);
            if (scrollY == line
                    && ((dy > 0 && targetScrollDown) || (dy < 0 && targetScrollUp))) {
                return;
            }

            if (scrollY + dy < 0 || scrollY + dy > maxDistance) {
                return;
            }
            consumeEvent(0, dy, consumed);
        }
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.d(TAG, "----父布局onNestedFling---------------- target -> " + target.toString());
        int scrollY = getScrollY();
        int line = getCurrentWrapline(target);
        if (scrollY != line) {
            fling((int) velocityY, scrollY);
            return true;
        }
        return false;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    private void fling(int velocityY, int scrollY) {
        if (getChildCount() > 0) {
            int height = getHeight() - getPaddingTop() - getPaddingBottom();
            int bottom = totalHeight;
            mScroller.fling(0, scrollY, 0, velocityY, 0, 0, 0,
                    Math.max(0, bottom - height));
            invalidate();
        }
    }

    @Override
    public int getNestedScrollAxes() {
        Log.d(TAG, "----父布局getNestedScrollAxes----------------");
        return mParentHelper.getNestedScrollAxes();
    }
}
