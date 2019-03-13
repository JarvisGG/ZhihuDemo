package com.jarvis.zhihudemo.view.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.List;

/**
 * @author yyf @ Zhihu Inc.
 * @since 01-17-2019
 */
public class CustomTabLayout extends HorizontalScrollView {

    public static final int TAB_TAG = 0x123;

    public List<Tab> list;



    private SlidingTabStrip tabStrip;

    public CustomTabLayout(Context context) {
        this(context, null);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setHorizontalScrollBarEnabled(false);
        initView();
    }

    private void initView() {
        tabStrip = new SlidingTabStrip(getContext());
        super.addView(tabStrip, new HorizontalScrollView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
    }

    public void addList(List<Tab> list) {
        this.list = list;
        for (int i = 0; i < list.size(); i++) {
            TabView view = new TabView(getContext(), list.get(i));
            tabStrip.addView(view, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
        }
    }

    private class TabView extends FrameLayout {

        Tab tab;

        public TabView(Context context, Tab tab) {
            super(context);
            this.tab = tab;

            View innerView = LayoutInflater.from(getContext()).inflate(tab.layoutRes, this, false);
            addView(innerView, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
        }


    }

    private class SlidingTabStrip extends LinearLayout {

        private int selectedIndicatorHeight;
        private int indicatorLeft = -1;
        private int indicatorRight = -1;

        private ValueAnimator indicatorAnimator;

        private int selectedPosition = -1;
        private float selectionOffset;

        private Paint paint;

        public SlidingTabStrip(Context context) {
            super(context);
            initOperator();
        }

        private void initOperator() {
            setWillNotDraw(false);
            paint = new Paint();
        }

        void setSelectedIndicatorColor(int color) {
            if (paint.getColor() != color) {
                paint.setColor(color);
                postInvalidateOnAnimation();
            }
        }

        void setSelectedIndicatorHeight(int height) {
            if (selectedIndicatorHeight != height) {
                selectedIndicatorHeight = height;
                postInvalidateOnAnimation();
            }
        }

        void animateIndicatorToPosition(int position, int duration) {
            if (indicatorAnimator != null && indicatorAnimator.isRunning()) {
                indicatorAnimator.cancel();
            }

            View currentSelectView = getChildAt(selectedPosition);
            Tab currentTab = (Tab) currentSelectView.getTag(TAB_TAG);
            View nextSelectView = getChildAt(position);
            Tab nextTab = (Tab) nextSelectView.getTag(TAB_TAG);
            if (currentTab == null || nextTab == null) {
                return;
            }

            int currentLeft = currentSelectView.getLeft() + currentTab.leftBound;
            int currentRight = currentSelectView.getLeft() + currentSelectView.getMeasuredWidth() - currentTab.rightBound;
            int nextLeft = nextSelectView.getLeft() + nextTab.leftBound;
            int nextRight = nextSelectView.getLeft() + nextSelectView.getMeasuredWidth() - nextTab.rightBound;

            ValueAnimator animator = indicatorAnimator = new ValueAnimator();
            animator.setInterpolator(new FastOutSlowInInterpolator());
            animator.setDuration(duration);
            animator.setFloatValues(0, 1);
            animator.addUpdateListener(animator1 -> {
                final float fraction = animator1.getAnimatedFraction();
                setIndicatorPosition(
                        (int) (currentLeft + (fraction * (nextLeft - currentLeft))),
                        currentRight + Math.round(fraction * (nextRight - currentRight)));
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    selectedPosition = position;
                    selectionOffset = 0f;
                }
            });
            animator.start();
        }

        void setIndicatorPositionFromTabPosition(int position, float positionOffset) {
            if (indicatorAnimator != null && indicatorAnimator.isRunning()) {
                indicatorAnimator.cancel();
            }
            selectedPosition = position;
            selectionOffset = positionOffset;
            updateIndicatorPosition();
        }

        void updateIndicatorPosition() {
            if (selectionOffset <= 0f || selectedPosition >= getChildCount() - 1) {
                return;
            }

            int left, right;
            View currentSelectView = getChildAt(selectedPosition);
            Tab currentTab = (Tab) currentSelectView.getTag(TAB_TAG);
            View nextSelectView = getChildAt(selectedPosition + 1);
            Tab nextTab = (Tab) nextSelectView.getTag(TAB_TAG);
            if (currentTab == null || nextTab == null) {
                return;
            }
            int currentLeft = currentSelectView.getLeft() + currentTab.leftBound;
            int currentRight = currentSelectView.getLeft() + currentSelectView.getMeasuredWidth() - currentTab.rightBound;
            int nextLeft = nextSelectView.getLeft() + nextTab.leftBound;
            int nextRight = nextSelectView.getLeft() + nextSelectView.getMeasuredWidth() - nextTab.rightBound;

            left = (int) (currentLeft * selectionOffset + nextLeft * (1 - selectionOffset));
            right = (int) (currentRight * selectionOffset + nextRight * (1 - selectionOffset));

            setIndicatorPosition(left, right);
        }

        void setIndicatorPosition(int left, int right) {
            if (left != indicatorLeft || right != indicatorRight) {
                indicatorLeft = left;
                indicatorRight = right;
                postInvalidateOnAnimation();
            }
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (indicatorLeft >= 0 && indicatorRight > indicatorLeft) {
                canvas.drawRoundRect(
                        indicatorLeft,
                        getMeasuredHeight() - selectedIndicatorHeight,
                        indicatorRight,
                        getMeasuredHeight(),
                        0,
                        0,
                        paint);
            }
        }
    }

    private class Tab {
        int leftBound = -1;
        int rightBound = -1;
        @LayoutRes int layoutRes;
    }



}
