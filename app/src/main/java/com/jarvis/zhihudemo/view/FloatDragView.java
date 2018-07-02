package com.jarvis.zhihudemo.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;

import java.lang.ref.WeakReference;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/11/16 上午10:37
 * @changeRecord [修改记录] <br/>
 */

public class FloatDragView extends FrameLayout {

    /**
     * 当前拖动的方向
     */
    public static final int NONE = 1;
    public static final int HORIZONTAL = 1 << 1;
    public static final int VERTICAL = 1 << 2;

    private int mDragDirect = NONE;

    /**
     * 最终组件滑向的方向
     */
    public static final int SLIDE_RESTORE_ORIGINAL = 1;
    public static final int SLIDE_TO_LEFT = 1 << 1;
    public static final int SLIDE_TO_RIGHT = 1 << 2;

    private int mDisappearDirect = SLIDE_RESTORE_ORIGINAL;

    /**
     * 垂直方向的拖动范围
     */
    private int mVerticalRange;

    /**
     * 水平方向的拖动范围
     */
    private int mHorizontalRange;

    /**
     * 屏幕宽度
     */
    private int mScreenWidth = 0;

    /**
     * 屏幕高度
     */
    private int mScreenHeight = 0;

    private int mTop;

    private int mLeft;

    /**
     * 垂直拖动时的偏移量
     */
    private float mVerticalOffset = 1f;

    /**
     * 水平拖动的偏移量
     */
    private float mHorizontalOffset = 1f;

    private int mDownX;
    private int mDownY;

    private Context mContext;

    private View mContainerView;
    private ViewDragHelper mDragHelper;

    public FloatDragView(Context context) {
        super(context);
        initView(context);
    }

    public FloatDragView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FloatDragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
        this.mContext = context;
        this.mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
        this.mScreenWidth = ExplosionUtils.getScreenSizeY(mContext);
        this.mScreenHeight = ExplosionUtils.getScreenSizeX(mContext);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHorizontalRange = MeasureSpec.getSize(widthMeasureSpec);
        mVerticalRange = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                mDownY = (int) event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (mDragDirect == NONE) {
                    int dx = Math.abs(mDownX - (int) event.getX());
                    int dy = Math.abs(mDownY - (int) event.getY());
                    int slop = mDragHelper.getTouchSlop();

                    if (Math.sqrt(dx * dx + dy * dy) >= slop) {
                        if (dy >= dx) {
                            mDragDirect = VERTICAL;
                        } else {
                            mDragDirect = HORIZONTAL;
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mDragDirect == NONE) {
                    int dx = Math.abs(mDownX - (int) event.getX());
                    int dy = Math.abs(mDownY - (int) event.getY());
                    int slop = mDragHelper.getTouchSlop();

                    if (Math.sqrt(dx * dx + dy * dy) < slop) {
                    }
                }
                break;

            default:
                break;
        }

        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            mContainerView = getChildAt(0);
        }
    }

    @Override
    protected void attachViewToParent(View child, int index, ViewGroup.LayoutParams params) {
        super.attachViewToParent(child, index, params);
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mContainerView;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (state == ViewDragHelper.STATE_IDLE) {
                if (mDragDirect == HORIZONTAL && mDisappearDirect == SLIDE_TO_RIGHT) {
                    mDisappearDirect = SLIDE_RESTORE_ORIGINAL;
                    setVisibility(GONE);
                }
                mDragDirect = NONE;
            }
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mVerticalRange;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mHorizontalRange;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int topBound = getPaddingTop();
            int bottomBound = getHeight() - child.getHeight() - topBound;
            return Math.min(Math.max(top, topBound), bottomBound);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
//            边界限制
//            int leftBound = getPaddingLeft();
//            int rightBound = getWidth() - child.getWidth() - leftBound;
//            return Math.min(Math.max(left, leftBound), rightBound);
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (mDragDirect == VERTICAL) {
                mTop = top;
                mVerticalOffset = ((float) mTop - mContainerView.getHeight() / 2) / mVerticalRange;
            } else {
                mLeft = left;
                mHorizontalOffset = ((float) mLeft + mContainerView.getWidth() / 2) / mHorizontalRange;
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (mDragDirect == VERTICAL) {
                if (yvel > 0 || (yvel == 0 && mVerticalOffset >= 0.5f)) {
                    slideVerticalTo(1f);
                } else if (yvel < 0 || (yvel == 0 && mVerticalOffset < 0.5f)) {
                    slideVerticalTo(0f);
                }
            } else if (mDragDirect == HORIZONTAL) {
//                if (mHorizontalOffset < 0.5) {
//                    slideHorizontalTo(0f);
//                } else if (mHorizontalOffset >= 0.5) {
//                    slideHorizontalTo(1f);
//                }
                if (mLeft > mScreenWidth / 2 || (mLeft > mScreenWidth / 6 && xvel > 0)) {
                    slideHorizontalTo(1f);
                } else {
                    slideHorizontalTo(0f);
                }
            }
        }
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void slideVerticalTo(float slideOffset) {
        int y = Math.min((int) slideOffset * mVerticalRange,
                mVerticalRange - mContainerView.getHeight());
        if (mDragHelper.smoothSlideViewTo(mContainerView, mLeft, y)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private boolean slideHorizontalTo(float slideOffset) {
        int x = 0;
        if (slideOffset == 1) {
            x = mScreenWidth;
            mDisappearDirect = SLIDE_TO_RIGHT;
        } else {
            x = Math.min((int) slideOffset * mHorizontalRange,
                    mHorizontalRange - mContainerView.getWidth());
            mDisappearDirect = SLIDE_TO_LEFT;
        }
        if (mDragHelper.smoothSlideViewTo(mContainerView, x, mTop)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    public void bindView(View containerView) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, ExplosionUtils.dp2Px(74)
        );
        params.gravity = Gravity.CENTER;
        mContainerView = containerView;
        this.addView(containerView, params);
    }
}
