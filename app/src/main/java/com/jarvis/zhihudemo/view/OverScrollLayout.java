package com.jarvis.zhihudemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.jarvis.library.widget.ZhihuRecyclerView;
import com.jarvis.zhihudemo.R;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/16 下午3:00
 * @changeRecord [修改记录] <br/>
 */

public class OverScrollLayout extends FrameLayout {

    private static final float DEFAULT_FATOR = 1;

    private static final int HOVER_TAP_SLOP = 20;
    private static final int DIRECTION_LEFT = 0x0001;
    private static final int DIRECTION_TOP = 0x0010;
    private static final int DIRECTION_RIGHT = 0x0100;
    private static final int DIRECTION_BOTTOM = 0x1000;

    private static final int DIRECTION_DEFAULT = DIRECTION_TOP;

    private int mTargetDirection;

    private float mFator = DEFAULT_FATOR;

    private MotionEvent mLastMotionEvent, mLastInterceptMotionEvent;

    private int mLastDirection;

    private int mTotaldx = 0;

    private int mTotaldy = 0;

    private OnDampingCallback mOnDampingCallback;

    private OnDragOffsetCallback mOnDragOffsetCallback;

    private Scroller mScroller;

    public OverScrollLayout(@NonNull Context context) {
        super(context);
        initView(context, null);
    }

    public OverScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public OverScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public OverScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.OverScrollLayout);
            int count = array.getIndexCount();
            for (int i = 0; i < count; i++) {
                int index = array.getIndex(i);
                switch (index) {
                    case R.styleable.OverScrollLayout_dampingDirection:
                        mTargetDirection |= array.getInt(index, DIRECTION_DEFAULT);
                        break;
                    case R.styleable.OverScrollLayout_dampingFactor:
                        mFator = array.getFloat(index, DEFAULT_FATOR);
                        break;
                    default:
                        break;
                }
            }
            array.recycle();
        }

        mScroller = new Scroller(context, new DecelerateInterpolator(1));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept;
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            intercept = false;
        } else {
            if (null == mOnDampingCallback) {
                mOnDampingCallback = try2GetOnDampingCallback();
            }
            intercept = isMoving(ev, mLastInterceptMotionEvent) && mOnDampingCallback.needDamping(ev, mLastInterceptMotionEvent);
        }
        mLastInterceptMotionEvent = MotionEvent.obtain(ev);
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int currentDirection = -1;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionEvent = event;
                break;
            case MotionEvent.ACTION_MOVE:
                if (null == mLastMotionEvent) {
                    mLastMotionEvent = mLastInterceptMotionEvent;
                }

                int dx = (int) (event.getRawX() - mLastMotionEvent.getRawX());
                int dy = (int) (event.getRawY() - mLastMotionEvent.getRawY());
                if (Math.abs(dx) > Math.abs(dy)) {
                    if (dx > 0 && (mTargetDirection & DIRECTION_LEFT) == DIRECTION_LEFT) {
                        currentDirection = DIRECTION_LEFT;
                    }
                    else if (dx < 0 && (mTargetDirection & DIRECTION_RIGHT) == DIRECTION_RIGHT) {
                        currentDirection = DIRECTION_RIGHT;
                    }
                    boolean isHorizonal = mLastDirection == DIRECTION_LEFT || mLastDirection == DIRECTION_RIGHT;
                    boolean isDirectionEnable = currentDirection != -1 && (mTargetDirection & currentDirection) == currentDirection;
                    if (mLastDirection == 0 || isHorizonal && isDirectionEnable) {
                        mLastDirection = currentDirection;
                        mTotaldx += (-(int) (dx * mFator));
                        smoothScrollBy(-(int) (dx * mFator), 0);
                        if (mOnDragOffsetCallback != null) {
                            mOnDragOffsetCallback.onDragOffset(mTotaldx, 0);
                        }
                    }
                } else {
                    if (dy > 0 && (mTargetDirection & DIRECTION_TOP) == DIRECTION_TOP) {
                        currentDirection = DIRECTION_TOP;
                    }
                    else if (dy < 0 && (mTargetDirection & DIRECTION_BOTTOM) == DIRECTION_BOTTOM) {
                        currentDirection = DIRECTION_BOTTOM;
                    }
                    boolean isVertical = mLastDirection == DIRECTION_TOP || mLastDirection == DIRECTION_BOTTOM;
                    boolean isDirectionEnable = currentDirection != -1 && (mTargetDirection & currentDirection) == currentDirection;
                    if (mLastDirection == 0 || isVertical && isDirectionEnable) {
                        mLastDirection = currentDirection;
                        mTotaldy += (-(int) (dy * mFator));
                        smoothScrollBy(0, -(int) (dy * mFator));
                        if (mOnDragOffsetCallback != null) {
                            mOnDragOffsetCallback.onDragOffset(0, mTotaldy);
                        }
                    }
                }
                mLastMotionEvent = MotionEvent.obtain(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                mLastDirection = 0;
                mLastMotionEvent = null;
                mTotaldx = mTotaldy = 0;
                smoothScrollTo(0, 0);
                mOnDragOffsetCallback.onDragOver();
                break;
            default:
                break;
        }

        return true;

    }

    private void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
        invalidate();
    }

    private void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }

    private boolean isMoving(MotionEvent newMotionEvent, MotionEvent oldMotionEvent) {
        int dx = (int) (newMotionEvent.getRawX() - oldMotionEvent.getRawX());
        int dy = (int) (newMotionEvent.getRawY() - oldMotionEvent.getRawY());
        return Math.abs(dx) > HOVER_TAP_SLOP || Math.abs(dy) > HOVER_TAP_SLOP;
    }

    private OnDampingCallback try2GetOnDampingCallback() {
        View child = getChildAt(0);
        if (child instanceof RecyclerView) {
            return new RecyclerViewOnDampingCallback((RecyclerView) child);
        } else if (child instanceof ViewPager) {
            return new ViewPagerDampingCallback((ViewPager) child);
        }
        return new SimpleDampingCallback();
    }

    public static boolean isMoving2Left(MotionEvent newMotionEvent, MotionEvent oldMotionEvent) {
        int dx = (int) (newMotionEvent.getRawX() - oldMotionEvent.getRawX());
        int dy = (int) (newMotionEvent.getRawY() - oldMotionEvent.getRawY());
        return dx > 0 && Math.abs(dx) > Math.abs(dy);
    }

    public static boolean isMoving2Top(MotionEvent newMotionEvent, MotionEvent oldMotionEvent) {
        int dx = (int) (newMotionEvent.getRawX() - oldMotionEvent.getRawX());
        int dy = (int) (newMotionEvent.getRawY() - oldMotionEvent.getRawY());
        return dy > 0 && Math.abs(dx) < Math.abs(dy);
    }

    public static boolean isMoving2Right(MotionEvent newMotionEvent, MotionEvent oldMotionEvent) {
        int dx = (int) (newMotionEvent.getRawX() - oldMotionEvent.getRawX());
        int dy = (int) (newMotionEvent.getRawY() - oldMotionEvent.getRawY());
        return dx < 0 && Math.abs(dx) > Math.abs(dy);
    }

    public static boolean isMoving2Bottom(MotionEvent newMotionEvent, MotionEvent oldMotionEvent) {
        int dx = (int) (newMotionEvent.getRawX() - oldMotionEvent.getRawX());
        int dy = (int) (newMotionEvent.getRawY() - oldMotionEvent.getRawY());
        return dy < 0 && Math.abs(dx) < Math.abs(dy);
    }

    public interface OnDampingCallback {
        boolean needDamping(MotionEvent newMotionEvent, MotionEvent oldMotionEvent);
    }

    public static class SimpleDampingCallback implements OnDampingCallback {

        @Override
        public boolean needDamping(MotionEvent newMotionEvent, MotionEvent oldMotionEvent) {
            return true;
        }
    }

    public static class RecyclerViewOnDampingCallback implements OnDampingCallback {

        private RecyclerView mRecyclerView;

        public RecyclerViewOnDampingCallback(RecyclerView recyclerView) {
            this.mRecyclerView = recyclerView;
        }

        @Override
        public boolean needDamping(MotionEvent newMotionEvent, MotionEvent oldMotionEvent) {
            if (null == newMotionEvent || null == oldMotionEvent) {
                return false;
            }
            boolean isTopDamping = isMoving2Top(newMotionEvent, oldMotionEvent) && !(mRecyclerView.canScrollVertically(-1));
            boolean isBottomDamping = isMoving2Bottom(newMotionEvent, oldMotionEvent) && !mRecyclerView.canScrollVertically(1);
            return isTopDamping || isBottomDamping;
        }
    }

    public static class ViewPagerDampingCallback implements OnDampingCallback {

        private ViewPager mViewPager;

        public ViewPagerDampingCallback(ViewPager viewPager) {

            mViewPager = viewPager;
        }

        @Override
        public boolean needDamping(MotionEvent newMotionEvent, MotionEvent oldMotionEvent) {

            if (null == newMotionEvent || null == oldMotionEvent) {
                return false;
            }
            boolean isTopDamping = isMoving2Top(newMotionEvent, oldMotionEvent) && mViewPager.getCurrentItem() == 0;
            boolean isBottomDamping = isMoving2Bottom(newMotionEvent, oldMotionEvent) && mViewPager.getCurrentItem() == mViewPager.getAdapter().getCount() - 1;
            return isTopDamping || isBottomDamping;
        }
    }

    public interface OnDragOffsetCallback {
        void onDragOffset(int dx, int dy);
        void onDragOver();
    }

    public void setOnDragOffsetCallback(OnDragOffsetCallback callback) {
        this.mOnDragOffsetCallback = callback;
    }

}
