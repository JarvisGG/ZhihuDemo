package com.jarvis.zhihudemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.MutableBoolean;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.jarvis.zhihudemo.R;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/11/2 下午4:28
 * @changeRecord [修改记录] <br/>
 */

public class GalleryCardView extends FrameLayout {



    private static final int DECELERATE_THRESHOLD = 120;

    private int originX, originY;
    private int mDragOffset;
    private int bottomDragVisibleHeight; // 滑动可见的高度
    private int bototmExtraIndicatorHeight; // 底部指示器的高度

    private static final int STATE_CLOSE = 1;
    private static final int STATE_EXPANDED = 2;
    private int mTouchSlop = 5;
    private int mDownState;

    private Context mContext;

    private View mTopView;
    private View mBottomView;
    private ViewDragHelper mDragHelper;
    private GestureDetectorCompat mGestureDetector;

    public GalleryCardView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public GalleryCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public GalleryCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public GalleryCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GalleryCardView, 0, 0);
        bottomDragVisibleHeight = (int) a.getDimension(R.styleable.GalleryCardView_bottomDragVisibleHeight, 0);
        bototmExtraIndicatorHeight = (int) a.getDimension(R.styleable.GalleryCardView_bototmExtraIndicatorHeight, 0);
        a.recycle();
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        this.mDragHelper = ViewDragHelper.create(this, 10f, new DragHelperCallback());
        this.mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_TOP);
        this.mGestureDetector = new GestureDetectorCompat(context, new MoveDetector());
        this.mGestureDetector.setIsLongpressEnabled(false); // 不处理长按事件
        // 滑动的距离阈值由系统提供
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    class MoveDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx,
                                float dy) {
            // 拖动了，touch不往下传递
            return Math.abs(dy) + Math.abs(dx) > mTouchSlop;
        }
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 1. detector和mDragHelper判断是否需要拦截
        boolean yScroll = mGestureDetector.onTouchEvent(ev);
        boolean shouldIntercept = false;
        try {
            shouldIntercept = mDragHelper.shouldInterceptTouchEvent(ev);
        } catch (Exception e) {
        }

        // 2. 触点按下的时候直接交给mDragHelper
        int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            mDownState = getCurrentState();
            mDragHelper.processTouchEvent(ev);
        }

        return shouldIntercept && yScroll;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // bottomMarginTop高度的计算，还是需要有一个清晰的数学模型才可以。
        // 实现的效果，是topView.top和bottomView.bottom展开前、与展开后都整体居中
        int bottomMarginTop = (bottomDragVisibleHeight + mTopView.getMeasuredHeight() / 2 - mBottomView.getMeasuredHeight() / 2) / 2 - bototmExtraIndicatorHeight;
        FrameLayout.LayoutParams lp1 = (LayoutParams) mBottomView.getLayoutParams();
        lp1.setMargins(0, bottomMarginTop, 0, 0);
        mBottomView.setLayoutParams(lp1);
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (child == mTopView) {
                return true;
            }
            return false;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == mTopView) {
                processLinkageView();
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int finalY = originY;
            if (mDownState == STATE_CLOSE) {
                if (originY - releasedChild.getTop() > 100 || yvel < -800) {
                    finalY = mDragOffset;
                }
            } else {
                boolean gotoBottom = releasedChild.getTop() - mDownState > 100 || yvel > 800;
                if (!gotoBottom) {
                    finalY = mDragOffset;
                    // 如果按下时已经展开，又向上拖动了，就进入详情页
                    if (mDragOffset - releasedChild.getTop() > mTouchSlop) {
//                        gotoDetailActivity();
                        postResetPosition();
                        return;
                    }
                }
            }

            if (mDragHelper.smoothSlideViewTo(releasedChild, originX, finalY)) {
                ViewCompat.postInvalidateOnAnimation(GalleryCardView.this);
            }
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return child.getLeft();
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int currentTop = child.getTop();
            if (top > currentTop) {
                return currentTop + (top - currentTop) / 2;
            }

            int result;
            if (currentTop > DECELERATE_THRESHOLD * 3) {
                result = currentTop + (top - currentTop) / 2;
            } else if (currentTop > DECELERATE_THRESHOLD * 2) {
                result = currentTop + (top - currentTop) / 3;
            } else if (currentTop > DECELERATE_THRESHOLD) {
                result = currentTop + (top - currentTop) / 4;
            } else if (currentTop > 0) {
                result = currentTop + (top - currentTop) / 8;
            } else {
                result = currentTop + (top - currentTop) / 16;
            }
            return result;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return 600;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return 600;
        }

        @Override
        public int getOrderedChildIndex(int index) {
            return super.getOrderedChildIndex(index);
        }
    }

    private void processLinkageView() {
        if (mTopView.getTop() > originY) {
            mBottomView.setAlpha(0f);
        } else {
            float alpha = (originY - mTopView.getTop()) * 0.01f;
            if (alpha > 1) {
                alpha = 1;
            }

            mBottomView.setAlpha(alpha);
            int maxDistance = originY - mDragOffset;
            int currentDistance = mTopView.getTop() - mDragOffset;
            float distanceRatio = (float) currentDistance / maxDistance;
            float scaleRatio = 1;
            if (currentDistance > 0) {
                scaleRatio = 0.5f + (1.0f - 0.5f) * (1 - distanceRatio);
            }
            mBottomView.setScaleX(scaleRatio);
            mBottomView.setScaleY(scaleRatio);
        }
    }

    private void postResetPosition() {
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTopView.offsetTopAndBottom(mDragOffset - mTopView.getTop());
            }
        }, 500);
    }

    private int getCurrentState() {
        int state;
        if (Math.abs(mTopView.getTop() - mDragOffset) <= mTouchSlop) {
            state = STATE_EXPANDED;
        } else {
            state = STATE_CLOSE;
        }
        return state;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        originX = (int) mTopView.getX();
        originY = (int) mTopView.getY();
        mDragOffset = mBottomView.getBottom() - mTopView.getMeasuredHeight() - bottomDragVisibleHeight;
    }
}
