package com.jarvis.zhihudemo.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.view.helper.AnswerDragHelper;
import com.jarvis.zhihudemo.view.widget.ZHDragViewInfo;
import com.jarvis.zhihudemo.widgets.AdUtils;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/25 下午3:59
 * @changeRecord [修改记录] <br/>
 */

public class DragCommentView extends FrameLayout {

    private static final int CLOSE = 0x100;

    private static final int MIDDLE = 0x101;

    private static final int OPEN = 0x102;

    private static final int DRAGING = 0x103;

    private final static int Y_VELOCITY = 600;

    @IntDef({CLOSE, MIDDLE, OPEN, DRAGING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {}

    public @Status int mCuttentStatus = OPEN;
    /**
     * 上一个非拖拽状态
     */
    public @Status int mPreStatus;

    private Context mContext;

    private View mContainerView;

    private View mFellowView;

    private View mTitleView;

    private ViewDragHelper mDragHelper;

    private ZHDragViewInfo mDragInfo;

    private int mCurrentStatus = ViewDragHelper.STATE_IDLE;

    private float lastY = 0;
    private float lastX = 0;
    private float y = 0;
    private float x = 0;
    private int deltaY = 0;
    private int deltaX = 0;

    /**
     * 3 种非拖拽状态临界值
     */
    private int mCloseLimit;

    private int mOpenLimit;

    private int mMiddleLimit;

    private int mClose2MiddleHalf;

    private int mMiddle2OpenHalf;

    private boolean mConfigurationChangedFlag = true;

    public DragCommentView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public DragCommentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragCommentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DragCommentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        initOperator();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView() {

        this.mContainerView = getChildAt(2);

        LayoutParams containerParams = (LayoutParams) mContainerView.getLayoutParams();
        containerParams.topMargin = ExplosionUtils.dp2Px(80);

        this.mTitleView = this.mContainerView.findViewById(R.id.main_comment);
        LinearLayout.LayoutParams titleParams = (LinearLayout.LayoutParams) mTitleView.getLayoutParams();
        titleParams.height = ExplosionUtils.dp2Px(112);
        mTitleView.setBackgroundColor(Color.parseColor("#48D1CC"));

        this.mFellowView = this.mContainerView.findViewById(R.id.main_rv);
        LinearLayout.LayoutParams fellowParams = (LinearLayout.LayoutParams) this.mFellowView.getLayoutParams();
        fellowParams.height = AdUtils.getScreenSizeY(mContext) - ExplosionUtils.dp2Px(192);

    }

    private void initOperator() {
        mDragHelper = ViewDragHelper.create(this, new DragHelperCallback());
    }

    public void bind(ZHDragViewInfo info) {
        this.mDragInfo = info;
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mContainerView;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            if (child == mContainerView) {
                return getMeasuredHeight();
            }
            return super.getViewVerticalDragRange(child);
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return super.getViewHorizontalDragRange(child);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (child == mContainerView) {
                return operatorTop(top);
            }
            return top;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return super.clampViewPositionHorizontal(child, left, dx);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == mContainerView) {
                updateOperator(top);
                invalidate();
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (releasedChild == mContainerView) {
                int top = mContainerView.getTop();
                if (mPreStatus == CLOSE && yvel < -Y_VELOCITY) {
                    middle(true);
                } else if (mPreStatus == MIDDLE && yvel > Y_VELOCITY) {
                    close(true);
                } else if (mPreStatus == MIDDLE && yvel < -Y_VELOCITY) {
                    open(true);
                } else if (mPreStatus == OPEN && yvel > Y_VELOCITY) {
                    middle(true);
                } else if (top >= mMiddleLimit && top <= mCloseLimit) {
                    if (top > mClose2MiddleHalf) {
                        close(true);
                    } else {
                        middle(true);
                    }
                } else if (top >= mOpenLimit && top <= mMiddleLimit) {
                    if (top > mMiddle2OpenHalf) {
                        middle(true);
                    } else {
                        open(true);
                    }
                }
            }
        }
    }

    private int operatorTop(int top) {
        if (top > mCloseLimit) {
            return mCloseLimit;
        } else if (top < mOpenLimit) {
            return mOpenLimit;
        } else {
            return top;
        }
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void updateOperator(int top) {
        updateView(top);
        dispatchDragEvent(top);
    }

    private void dispatchDragEvent(int top) {
        if (mCurrentStatus != DRAGING) {
            mPreStatus = mCurrentStatus;
        }

        @Status int status = mCuttentStatus;
        mCuttentStatus = updateStatus(top);
        switch (mCuttentStatus) {
            case CLOSE:
                break;
            case DRAGING:
                if (mCurrentStatus != status) {
                    // TODO 滑动时
                }
                break;
            case MIDDLE:
                break;
            case OPEN:
                break;
            default:
                break;
        }
    }

    private @Status int updateStatus(int top) {
        if (top == mCloseLimit) {
            return CLOSE;
        } else if (top == mOpenLimit) {
            return OPEN;
        } else if (top == mMiddleLimit) {
            return MIDDLE;
        }
        return DRAGING;
    }

    private void updateView(int top) {
        FrameLayout.LayoutParams containerParams = (LayoutParams) mContainerView.getLayoutParams();
        containerParams.topMargin = top;
        updateViewLayout(mContainerView, containerParams);

//        LayoutParams fellowParams = (LayoutParams) mFellowView.getLayoutParams();
//        fellowParams.topMargin = containerParams.topMargin + mContainerView.getMeasuredHeight();
//        updateViewLayout(mFellowView, fellowParams);
    }

    @Override
    public void updateViewLayout(View view, ViewGroup.LayoutParams params) {
        if (params == null) {
            throw new IllegalArgumentException("params is null!");
        }
        view.setLayoutParams(params);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /**
         * 旋转屏幕
         */
        this.mConfigurationChangedFlag = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mConfigurationChangedFlag) {
            mConfigurationChangedFlag = false;
            mCloseLimit = getMeasuredHeight();
            mOpenLimit = 0;
            mMiddleLimit = (int) ((getMeasuredHeight() - mContainerView.getMeasuredHeight()) * mDragInfo.mMiddlePrecent);
            mClose2MiddleHalf = (mCloseLimit - mMiddleLimit) / 2 + mMiddleLimit;
            mMiddle2OpenHalf = (mMiddleLimit - mOpenLimit) / 2 + mOpenLimit;
            switch (mCurrentStatus) {
                case CLOSE:
                    close(false);
                    break;
                case OPEN:
                    open(false);
                    break;
                case MIDDLE:
                    middle(false);
                    break;
                default:
                    break;
            }
        }
    }


    public void close(boolean smooth) {
        if (smooth) {
            animateHandler(mCloseLimit);
        } else {
            updateView(mCloseLimit);
            mCurrentStatus = CLOSE;
        }
    }

    public void open(boolean smooth) {
        if (smooth) {
            mCurrentStatus = OPEN;
            animateHandler(mOpenLimit);
        } else {
            updateView(mCloseLimit);
            mCurrentStatus = OPEN;
        }
    }

    private void middle(boolean smooth) {
        if (smooth) {
            animateHandler(mMiddleLimit);
        } else {
            updateView(mCloseLimit);
            mCurrentStatus = MIDDLE;
        }
    }

    private void animateHandler(int top) {
        if (mDragHelper.smoothSlideViewTo(mContainerView, 0, top)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    private boolean operatorTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case ACTION_DOWN:
                lastY = y = event.getY();
                lastX = x = event.getX();
                break;
            case ACTION_MOVE:
                y = event.getY();
                x = event.getX();
                deltaY = (int) (lastY - y);
                deltaX = (int) (lastX - x);
                if (Math.abs(deltaX) < Math.abs(deltaY)) {
                    if (deltaY < 0 && !mFellowView.canScrollVertically(-1)) {
                        Log.e("operatorTouchEvent -> ", "deltaY < 0");
//                        mFellowView.layout(
//                                mFellowView.getLeft(),
//                                mFellowView.getTop() - deltaY,
//                                mFellowView.getLeft() + mFellowView.getMeasuredWidth(),
//                                mFellowView.getTop() + mFellowView.getMeasuredHeight()
//                        );
                    } else if (deltaY > 0) {
                        Log.e("operatorTouchEvent -> ", "deltaY > 0");
                    } else {
                        Log.e("operatorTouchEvent -> ", "canScrollVertically");
                    }
                } else {
                    Log.e("operatorTouchEvent -> ", "error");
                }
            case ACTION_UP:
                break;
            default:
                break;
        }
        return false;
    }
}
