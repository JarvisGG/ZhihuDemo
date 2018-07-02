package com.jarvis.zhihudemo.view.nested;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.webkit.WebView;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/3/19 下午2:14
 * @changeRecord [修改记录] <br/>
 */

public class NestScrollWebView extends WebView implements NestedScrollingChild {

    public static final String TAG = NestScrollWebView.class.getSimpleName();
    public static final int UP = 1;
    public static final int DOWN = -1;
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private final int mTouchSlop;
    private final int mMinimumVelocity;
    private final int mMaximumVelocity;
    private int direction = DOWN;
    private int mLastMotionY;
    private int mNestedYOffset;
    private VelocityTracker mVelocityTracker;
    private NestedScrollingChildHelper mChildHelper;
    private boolean allowFly;
    private int downY;
    private float mDownY = -1;

    public NestScrollWebView(Context context) {
        this(context, null);
    }

    public NestScrollWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestScrollWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean eventAddToVelocityTracker = false;
        final int action = event.getAction();
        final int actionIndex = event.getActionIndex();
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                allowFly = false;
                downY = (int) event.getRawY();
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                break;

            case MotionEvent.ACTION_MOVE:
                int moveY = (int) event.getRawY();
                int dy = - (moveY - downY);
                downY = moveY;
                if (dispatchNestedPreScroll(0, dy, mScrollConsumed, mScrollOffset)) {
                    dy -= mScrollConsumed[1];
                    scrollBy(0, dy);
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.addMovement(event);
                eventAddToVelocityTracker = true;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int mScrollPointerId = event.getPointerId(actionIndex);
                float vY = -mVelocityTracker.getYVelocity(mScrollPointerId);
                if (Math.abs(vY) > mMinimumVelocity && !dispatchNestedPreFling(0, vY)) {
                    dispatchNestedFling(0, vY, true);
                }
                resetTouch();
                break;

            default:
                break;
        }

        if (!eventAddToVelocityTracker) {
            mVelocityTracker.addMovement(event);
        }

        return true;
    }

    private void resetTouch() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }
        stopNestedScroll();
    }


    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }


    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

}
