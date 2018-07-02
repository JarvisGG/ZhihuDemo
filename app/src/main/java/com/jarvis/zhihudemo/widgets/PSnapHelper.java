package com.jarvis.zhihudemo.widgets;

import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Scroller;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/11/2 下午2:31
 * @changeRecord [修改记录] <br/>
 */

public abstract class PSnapHelper extends RecyclerView.OnFlingListener {

    static final float MILLISECONDS_PER_INCH = 100f;
    private RecyclerView mRecyclerView;
    private Scroller mGravityScroller;

    private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {

        boolean mScrolled = false;
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE && mScrolled) {
                mScrolled = false;
                snapToTargetExistingView();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dx != 0 || dy != 0) {
                mScrolled = true;
            }
        }
    };

    private void snapToTargetExistingView() {
        if (mRecyclerView == null) {
            return;
        }
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        View snapView = findSnapView(layoutManager);
        if (snapView == null) {
            return;
        }
        int[] snapDistance = calculateDistanceToFinalSnap(layoutManager, snapView);
        if (snapDistance[0] != 0 || snapDistance[1] != 0) {
            mRecyclerView.smoothScrollBy(snapDistance[0], snapDistance[1]);
        }
    }

    @Override
    public boolean onFling(int velocityX, int velocityY) {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            return false;
        }
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter == null) {
            return false;
        }
        int minFlingVelocity = mRecyclerView.getMinFlingVelocity();
        return (Math.abs(velocityY) > minFlingVelocity || Math.abs(velocityX) > minFlingVelocity)
                && snapFromFling(layoutManager, velocityX, velocityY);
    }


    private boolean snapFromFling(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return false;
        }

        RecyclerView.SmoothScroller smoothScroller = createScroller(layoutManager);
        if (smoothScroller == null) {
            return false;
        }

        int targetPosition = findTargetSnapPosition(layoutManager, velocityX, velocityY);
        if (targetPosition == RecyclerView.NO_POSITION) {
            return false;
        }

        smoothScroller.setTargetPosition(targetPosition);
        layoutManager.startSmoothScroll(smoothScroller);
        return true;
    }

    private RecyclerView.SmoothScroller createScroller(RecyclerView.LayoutManager layoutManager) {
        return createSnapScroller(layoutManager);
    }

    protected LinearSmoothScroller createSnapScroller(RecyclerView.LayoutManager layoutManager) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return null;
        }
        return new LinearSmoothScroller(mRecyclerView.getContext()) {
            @Override
            protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {

                int[] snapDistances = calculateDistanceToFinalSnap(mRecyclerView.getLayoutManager(), targetView);
                final int dx = snapDistances[0];
                final int dy = snapDistances[1];
                final int time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)));
                if (time > 0) {
                    action.update(dx, dy, time, mDecelerateInterpolator);
                }
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }

            @Override
            protected int calculateTimeForDeceleration(int dx) {
                return super.calculateTimeForDeceleration(dx);
            }

            @Override
            protected int calculateTimeForScrolling(int dx) {
                return super.calculateTimeForScrolling(dx);
            }

            @Override
            public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference);
            }

            @Override
            public int calculateDxToMakeVisible(View view, int snapPreference) {
                return super.calculateDxToMakeVisible(view, snapPreference);
            }

            @Override
            public int calculateDyToMakeVisible(View view, int snapPreference) {
                return super.calculateDyToMakeVisible(view, snapPreference);
            }

            @Override
            protected void onChildAttachedToWindow(View child) {
                super.onChildAttachedToWindow(child);
            }

            @Override
            protected int getVerticalSnapPreference() {
                return super.getVerticalSnapPreference();
            }
        };
    }

    public abstract int[] calculateDistanceToFinalSnap(RecyclerView.LayoutManager layoutManager, View targetView);

    public abstract View findSnapView(RecyclerView.LayoutManager layoutManager);

    public abstract int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityx, int velocityy);

}
