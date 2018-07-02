package com.jarvis.zhihudemo.view1;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.jarvis.library.widget.ZhihuRecyclerView;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/4/11 下午3:14
 * @changeRecord [修改记录] <br/>
 */

public class ScrollRecyclerView extends ZhihuRecyclerView {

    private static final int DELAY_MILLIS = 100;

    public static final int BROUND = -0x111111;

    private Scroller mGravityScroller;

    public interface OnBoundFlingListener {
        public void onFlingStarted(int velocityY);
        public void onFlingStopped(int velocityY);
    }

    private OnBoundFlingListener mFlingListener;
    private Runnable mScrollChecker;
    private int mPreviousPosition;

    public ScrollRecyclerView(Context context) {
        super(context,null);
    }

    public ScrollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public ScrollRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

//        mScrollChecker = () -> {
//            int position = getScrollY();
//            if (mPreviousPosition - position == 0) {
//                if (mGravityScroller == null) {
////                        mFlingListener.onFlingStopped((int) mGravityScroller.getCurrVelocity());
//                    dispatchNestedPreFling(-1, mGravityScroller.getCurrVelocity());
//                }
//                removeCallbacks(mScrollChecker);
//            } else {
//                mPreviousPosition = getScrollY();
//                postDelayed(mScrollChecker, DELAY_MILLIS);
//            }
//        };
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mGravityScroller = new Scroller(getContext(),
                new DecelerateInterpolator());
        this.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public static final String TAG = "ScrollRecyclerView";

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e(TAG, "-----------onScrollStateChanged-----------");
                Log.e(TAG, "newState: " + newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e(TAG, "-----------onScrolled-----------");
                Log.e(TAG, "dx: " + dx);
                Log.e(TAG, "dy: " + dy);
                Log.e(TAG, "CHECK_SCROLL_UP: " + recyclerView.canScrollVertically(-1));
                Log.e(TAG, "CHECK_SCROLL_DOWN: " + recyclerView.canScrollVertically(1));
                if ((dy < 0 && !recyclerView.canScrollVertically(-1)) ||
                        (dy > 0 && !recyclerView.canScrollVertically(1))) {
                    dispatchNestedPreFling(BROUND, mGravityScroller.getCurrVelocity());
                }
            }
        });
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        if (mFlingListener != null) {
            mFlingListener.onFlingStarted(velocityY);
            calculateScrollDistance(velocityX, velocityY);
            mGravityScroller.getDuration();
        }
        return super.fling(velocityX, velocityY);
    }

    public int[] calculateScrollDistance(int velocityX, int velocityY) {
        int[] outDist = new int[2];
        mGravityScroller.fling(0, 0, velocityX, velocityY,
                Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        outDist[0] = mGravityScroller.getFinalX();
        outDist[1] = mGravityScroller.getFinalY();
        return outDist;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mGravityScroller.computeScrollOffset()) {
            scrollTo(mGravityScroller.getCurrX(), mGravityScroller.getCurrY());
            invalidate();
        }
    }

    public void setBoundFlingListener(OnBoundFlingListener listener) {
        this.mFlingListener = listener;
    }
}
