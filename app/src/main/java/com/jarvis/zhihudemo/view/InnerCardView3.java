package com.jarvis.zhihudemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.widgets.AdUtils;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/12/28 下午4:59
 * @changeRecord [修改记录] <br/>
 */

public class InnerCardView3 extends FrameLayout {

    private static final int DECELERATE_THRESHOLD = 120;
    private static final int DRAG_SWITCH_DISTANCE_THRESHOLD = 100;
    private static final int DRAG_SWITCH_VEL_THRESHOLD = 800;

    private View mTopView;
    private View mBottomView;

    private ViewDragHelper mViewDragHelper;

    private int mOriginX, mOriginY;
    private int mDragTopDest = 0;
    private int mTouchSlop = 5;

    private float mStartMoveX;
    private float mStartMoveY;
    private float mBottomVisiableHeight = 0;

    public InnerCardView3(@NonNull Context context) {
        super(context);
        init(context);
    }

    public InnerCardView3(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public InnerCardView3(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public InnerCardView3(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();

        mViewDragHelper = ViewDragHelper.create(this, 10f, new DragCallBack());
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_TOP);
    }

    private void init(Context context, AttributeSet set) {
        TypedArray array = getContext().obtainStyledAttributes(set, R.styleable.InnerCardView3);
        mBottomVisiableHeight = array.getDimension(R.styleable.InnerCardView3_bottomVisiableHeight, 0);
        array.recycle();
        init(context);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mOriginX = (int) mTopView.getX();
        this.mOriginY = (int) mTopView.getY();
        this.mDragTopDest = (int) (mBottomView.getBottom() - mBottomVisiableHeight - mTopView.getMeasuredHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int bottomMarginTop = (int) ((mBottomVisiableHeight + mTopView.getMeasuredHeight() / 2 - mBottomView.getMeasuredHeight() / 2) / 2);
        LayoutParams params = (LayoutParams) mBottomView.getLayoutParams();
        params.setMargins(0, bottomMarginTop, 0, 0);
        mBottomView.setLayoutParams(params);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTopView = getChildAt(1);
        mBottomView = getChildAt(0);
        mBottomView.setAlpha(0);
        mBottomView.setScaleX(mTopView.getScaleX() * 1.1f);
        mBottomView.setScaleY(mTopView.getScaleY() * 1.1f);
        mTopView.setOnClickListener(view -> {
            mViewDragHelper.smoothSlideViewTo(mTopView, mOriginX, mDragTopDest);
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean shouldIntercept = mViewDragHelper.shouldInterceptTouchEvent(ev);
        int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            mViewDragHelper.processTouchEvent(ev);
        }
        return shouldIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void processLinkageView() {
        if (mTopView.getTop() > mOriginY) {
            mBottomView.setAlpha(0f);
        } else {
            float persent = ((float) (mTopView.getTop() - mOriginY)) / ((float) (mDragTopDest - mOriginY));
            mBottomView.setAlpha(persent);
            float scalePersent = 1.1f * persent;
            mBottomView.setScaleX(scalePersent);
            mBottomView.setScaleY(scalePersent);
        }

    }

    public class DragCallBack extends ViewDragHelper.Callback {

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == mTopView) {
                processLinkageView();
            }
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mTopView;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int currentTop = child.getTop();
            if (top > child.getTop()) {
                return currentTop + (top - currentTop) / 2;
            }

            int result;
            if (currentTop > DECELERATE_THRESHOLD * 3) {
                result = currentTop + (top - currentTop) / 2;
            } else if (currentTop > DECELERATE_THRESHOLD * 2) {
                result = currentTop + (top - currentTop) / 4;
            } else if (currentTop > 0) {
                result = currentTop + (top - currentTop) / 8;
            } else if (currentTop > -DECELERATE_THRESHOLD) {
                result = currentTop + (top - currentTop) / 16;
            } else if (currentTop > -DECELERATE_THRESHOLD * 2) {
                result = currentTop + (top - currentTop) / 32;
            } else if (currentTop > -DECELERATE_THRESHOLD * 3) {
                result = currentTop + (top - currentTop) / 48;
            } else {
                result = currentTop + (top - currentTop) / 64;
            }
            return result;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return child.getLeft();
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return AdUtils.getScreenSizeX(getContext());
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return AdUtils.getScreenSizeY(getContext());
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int finalY = mOriginY;
            if (mOriginY - releasedChild.getTop() > DRAG_SWITCH_DISTANCE_THRESHOLD || yvel < -DRAG_SWITCH_VEL_THRESHOLD) {
                finalY = mDragTopDest;
            }

            if (mViewDragHelper.smoothSlideViewTo(releasedChild, mOriginX, finalY)) {
                ViewCompat.postInvalidateOnAnimation(InnerCardView3.this);
            }
        }
    }
}
