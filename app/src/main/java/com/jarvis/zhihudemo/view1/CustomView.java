package com.jarvis.zhihudemo.view1;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;

import com.jarvis.zhihudemo.R;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/3/21 下午6:11
 * @changeRecord [修改记录] <br/>
 */

public class CustomView extends ViewGroup {

    private Scroller mScroller;

    private VelocityTracker mVelocityTracker;

    private ViewConfiguration mConfiguration;

    private int mTouchSlop;

    private float mDownX;

    private float mMoveX;

    private float mLastMoveX;

    private int leftBorder;

    private int rightBorder;

    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initOperator(context, attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initOperator(context, attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initOperator(context, attrs);
    }

    private void initOperator(Context context, AttributeSet attrs) {
        mScroller = new Scroller(context);
        mConfiguration = ViewConfiguration.get(context);
        mTouchSlop = mConfiguration.getScaledPagingTouchSlop();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
        super.computeScroll();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                childView.layout(
                        i * childView.getMeasuredWidth(),
                        0,
                        (i + 1) * childView.getMeasuredWidth(),
                        childView.getMeasuredHeight());
            }
            leftBorder = getChildAt(0).getLeft();
            rightBorder = getChildAt(getChildCount() - 1).getRight();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (ev.getAction()) {
            case ACTION_DOWN:
                mDownX = ev.getRawX();
                mLastMoveX = mDownX;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case ACTION_MOVE:
                mMoveX = ev.getRawX();
                float offsetX = Math.abs(mMoveX - mDownX);
                mLastMoveX = mMoveX;
                if (offsetX > mTouchSlop) {
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case ACTION_MOVE:
                mMoveX = event.getRawX();
                int offsetX = (int) (mLastMoveX - mMoveX);
                if (getScrollX() + offsetX < leftBorder) {
                    scrollTo(leftBorder, 0);
                    return true;
                } else if (getScrollX() + getWidth() + offsetX > rightBorder) {
                    scrollTo(rightBorder - getWidth(), 0);
                    return true;
                }
                scrollBy(offsetX, 0);
                mLastMoveX = mMoveX;
                break;
            case ACTION_UP:
                mMoveX = event.getRawX();
                mVelocityTracker.computeCurrentVelocity(1000);
                int xVelocity = (int) mVelocityTracker.getXVelocity();
                if (Math.abs(xVelocity) > mConfiguration.getScaledMinimumFlingVelocity()) {
                    mScroller.fling(getScrollX(), 0, -xVelocity, 0, leftBorder, rightBorder - getWidth(), 0, 0);
                    invalidate();
                }
                break;
            default:
                break;

        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ImageView view = new ImageView(getContext());
        view.setImageResource(R.drawable.zh_answer_righthand);
        view.draw(canvas);
    }
}
