package com.jarvis.zhihudemo.view1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.jarvis.zhihudemo.view.hybrid.WebViewS;
import com.jarvis.zhihudemo.widgets.animation.DynamicAnimation;
import com.jarvis.zhihudemo.widgets.animation.FlingAnimation;

/**
 * @author yyf @ Zhihu Inc.
 * @since 06-26-2018
 */
public class InterceptFrameLayout extends FrameLayout implements NestedScrollingParent {

    private View mChildView;

    private ObjectAnimator mTransYAnim;
    private ObjectAnimator currentAnimator;

    private float mLatestTouchDownY;
    private float mTouchSlop;

    private VelocityTracker velocityTracker;
    private float minFlingVelocity;

    private float downY;

    private float downX;

    private float downSheetTranslation;

    private int mWebViewContentHeight;

    public boolean bottomSheetOwnsTouch;
    private boolean sheetViewOwnsTouch;

    private static float mFlingFriction = ViewConfiguration.getScrollFriction();
    private static float DECELERATION_RATE = (float) (Math.log(0.78) / Math.log(0.9));
    private static float mPhysicalCoeff;
    private static final float INFLEXION = 0.35f;

    private double getSplineDeceleration(int velocity) {
        return Math.log(INFLEXION * Math.abs(velocity) / (mFlingFriction * mPhysicalCoeff));
    }

    private static double getSplineDecelerationByDistance(double distance) {
        final double decelMinusOne = DECELERATION_RATE - 1.0;
        return decelMinusOne * (Math.log(distance / (mFlingFriction * mPhysicalCoeff))) / DECELERATION_RATE;
    }

    public static int getVelocityByDistance(double distance) {
        final double l = getSplineDecelerationByDistance(distance);
        int velocity = (int) (Math.exp(l) * mFlingFriction * mPhysicalCoeff / INFLEXION);
        return Math.abs(velocity);
    }


    private boolean hasIntercepted;

    private boolean isHoldTouch = true;

    private float sheetTranslation;

    private ObjectAnimator objectAnimator;


    private final Property<InterceptFrameLayout, Float> SHEET_TRANSLATION = new Property<InterceptFrameLayout, Float>(Float.class, "sheetTranslation") {
        @Override
        public Float get(InterceptFrameLayout object) {
            return getHeight() - object.sheetTranslation;
        }

        @Override
        public void set(InterceptFrameLayout object, Float value) {
            Log.e("setSheetTranslation2", value +"");
            object.setTranslation(value);
        }
    };

    private int mDeltaY = 0;

    public DynamicAnimation.ViewProperty INTER_TRANSLATION_Y = new DynamicAnimation.ViewProperty("translationY") {
        @Override
        public void setValue(View view, float value) {
            view.setTranslationY(value);
        }

        @Override
        public float getValue(View view) {
            return view.getTranslationY();
        }
    };

    private WebViewS.OverScrolled overScrolled = new WebViewS.OverScrolled() {
        @Override
        public void onFling(float velocityY) {
            FlingAnimation flingAnimation = new FlingAnimation(InterceptFrameLayout.this, INTER_TRANSLATION_Y);
        }

        @Override
        public void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
            if (clampedY && (objectAnimator == null || !objectAnimator.isRunning())) {
                objectAnimator = ObjectAnimator.ofFloat(InterceptFrameLayout.this, "translationY", 0, -mDeltaY * 2, 0);
                objectAnimator.setDuration(500);
                objectAnimator.setInterpolator(new DecelerateInterpolator(2));
                objectAnimator.start();
//                InterceptFrameLayout.this
//                        .animate()
//                        .translationY(mDeltaY)
//                        .setDuration(500)
//                        .setInterpolator(new DecelerateInterpolator(2))
//                        .start();

            }
        }

        @Override
        public void onOverScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
            mDeltaY = deltaY;
        }
    };

    public InterceptFrameLayout(@NonNull Context context) {
        super(context);
    }

    public InterceptFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public InterceptFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 1) {
            throw new IllegalStateException("child must be 1!!!");
        }
        mChildView = getChildAt(0);

        if (mChildView instanceof WebViewS) {
            ((WebViewS) mChildView).registerOverScrolled(overScrolled);
        }

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        float ppi = getContext().getResources().getDisplayMetrics().density * 160.0f;
        mPhysicalCoeff = SensorManager.GRAVITY_EARTH // g (m/s^2)
                * 39.37f // inch/meter
                * ppi
                * 0.84f;


    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        velocityTracker = VelocityTracker.obtain();
        post(() -> {
            sheetTranslation = getMeasuredHeight();
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        velocityTracker.clear();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean res = super.dispatchTouchEvent(ev);
        Log.e("parentLayout --> ", "dispatchTouchEvent -> " + res);
        return res;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                if (mTransYAnim != null && mTransYAnim.isRunning()) {
//                    mTransYAnim.cancel();
//                }
//                mLatestTouchDownY = event.getRawY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (Math.abs(event.getRawY() - mLatestTouchDownY) > mTouchSlop) {
//                    if (mChildView.getVisibility() == View.VISIBLE) {
//                        if (event.getRawY() - mLatestTouchDownY > 0.0F
//                                && !mChildView.canScrollVertically(-1)) {
//                            mLatestTouchDownY = event.getRawY();
//                            return true;
//                        } else if (event.getRawY() - mLatestTouchDownY < 0.0F
//                                && !mChildView.canScrollVertically(1)) {
//                            mLatestTouchDownY = event.getRawY();
//                            return true;
//                        }
//                    } else {
//                        mLatestTouchDownY = event.getRawY();
//                        return true;
//
//                    }
//                }
//                break;
//            default:
//                break;
//        }
//
//        return false;
        boolean downAction = ev.getActionMasked() == MotionEvent.ACTION_DOWN;

        if (ev.getY() > getHeight() - sheetTranslation) {
            hasIntercepted = true;
        } else {
            hasIntercepted = true;
        }
        return hasIntercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                break;
//            case MotionEvent.ACTION_MOVE:
//                onActionMove(event);
//                break;
//            case MotionEvent.ACTION_CANCEL:
//            case MotionEvent.ACTION_UP:
//                onActionRelease(event);
//                break;
//            default:
//                break;
//        }
//        Log.e("parentLayout --> ", "onTouchEvent -> false");

        if (isAnimating()) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            bottomSheetOwnsTouch = false;
            sheetViewOwnsTouch = false;
            downY = event.getY();
            downX = event.getX();
            downSheetTranslation = sheetTranslation;
            velocityTracker.clear();

            if (mChildView instanceof WebViewS) {
                mWebViewContentHeight = (int) (((WebViewS)mChildView).getContentHeight() * ((WebViewS)mChildView).getScale());
            }
        }
        getParent().requestDisallowInterceptTouchEvent(true);

        velocityTracker.addMovement(event);

        float maxSheetTranslation = getMeasuredHeight();

        float deltaY = downY - event.getY();
        float deltaX = downX - event.getX();

        if (!bottomSheetOwnsTouch && !sheetViewOwnsTouch) {
            bottomSheetOwnsTouch = Math.abs(deltaY) > mTouchSlop;
            sheetViewOwnsTouch = Math.abs(deltaX) > mTouchSlop;

            if (bottomSheetOwnsTouch) {

                sheetViewOwnsTouch = false;
                downY = event.getY();
                downX = event.getX();
                deltaY = 0;
                deltaX = 0;
            }
        }

        float newSheetTranslation = downSheetTranslation + deltaY;

        if (bottomSheetOwnsTouch) {
            boolean scrollingDown = deltaY < 0;
            boolean canScrollDown = canScrollDown(getChildAt(0), event.getX(), event.getY() + (sheetTranslation - getHeight()));
            boolean scrollingUp = deltaY > 0;
            boolean canScrollUp = canScrollUp(getChildAt(0), event.getX(), event.getY() + (sheetTranslation - getHeight()));

            Log.e("onTouch --->", "scrollingDown : " + scrollingDown + " canScrollDown : " + canScrollDown + " scrollingUp : " + scrollingUp + " canScrollUp : " + canScrollUp);

            if (isHoldTouch && ((scrollingDown && !canScrollUp) || (scrollingUp && !canScrollDown))) {
                downY = event.getY();
                velocityTracker.clear();
                isHoldTouch = false;
                newSheetTranslation = sheetTranslation;

                MotionEvent cancelEvent = MotionEvent.obtain(event);
                cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                getChildAt(0).dispatchTouchEvent(cancelEvent);
                cancelEvent.recycle();
            }

            if (!isHoldTouch &&
                    (((scrollingUp && !canScrollUp) && newSheetTranslation > maxSheetTranslation) ||
                            ((scrollingDown && !canScrollDown) && newSheetTranslation < maxSheetTranslation))) {
                setSheetTranslation(maxSheetTranslation);

                if ((scrollingUp && !canScrollUp) && newSheetTranslation > maxSheetTranslation) {
                    newSheetTranslation = Math.min(maxSheetTranslation, newSheetTranslation);
                }
                if ((scrollingDown && !canScrollDown) && newSheetTranslation < maxSheetTranslation) {
                    newSheetTranslation = Math.max(maxSheetTranslation, newSheetTranslation);
                }
                MotionEvent downEvent = MotionEvent.obtain(event);
                downEvent.setAction(MotionEvent.ACTION_DOWN);
                getChildAt(0).dispatchTouchEvent(downEvent);
                downEvent.recycle();
                isHoldTouch = true;
            }

            if (isHoldTouch) {
                event.offsetLocation(0, sheetTranslation - getMeasuredHeight());
                getChildAt(0).dispatchTouchEvent(event);
            } else {
                setSheetTranslation(newSheetTranslation);

                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    isHoldTouch = true;
                    getParent().requestDisallowInterceptTouchEvent(false);
                    velocityTracker.computeCurrentVelocity(1000);
                    float velocityY = velocityTracker.getYVelocity();
                    if (Math.abs(velocityY) < minFlingVelocity) {
                        if (sheetTranslation > getHeight() / 2) {
                        } else {
                        }
                    } else {
                        if (velocityY < 0) {
                        } else {
                        }
                    }
                    recover();
                }
            }
        } else {
            event.offsetLocation(0, sheetTranslation - getMeasuredHeight());
            getChildAt(0).dispatchTouchEvent(event);
        }
        return true;
    }

    private boolean canScrollUp(View view, float x, float y) {

        if (view instanceof WebViewS) {
            return canWebViewScrollUp();
        }

        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                int childLeft = child.getLeft() - view.getScrollX();
                int childTop = child.getTop() - view.getScrollY();
                int childRight = child.getRight() - view.getScrollX();
                int childBottom = child.getBottom() - view.getScrollY();
                boolean intersects = x > childLeft && x < childRight && y > childTop && y < childBottom;
                if (intersects && canScrollUp(child, x - childLeft, y - childTop)) {
                    return true;
                }
            }
        }
        return view.canScrollVertically(-1);
    }

    private boolean canScrollDown(View view, float x, float y) {

        if (view instanceof WebViewS) {
            return canWebViewScrollDown();
        }

        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                int childLeft = child.getLeft() - view.getScrollX();
                int childTop = child.getTop() - view.getScrollY();
                int childRight = child.getRight() - view.getScrollX();
                int childBottom = child.getBottom() - view.getScrollY();
                boolean intersects = x > childLeft && x < childRight && y > childTop && y < childBottom;
                if (intersects && canScrollUp(child, x - childLeft, y - childTop)) {
                    return true;
                }
            }
        }
        return view.canScrollVertically(1);
    }

    private boolean canWebViewScrollUp() {
        final int offset = mChildView.getScrollY();
        final int range = mWebViewContentHeight - mChildView.getHeight();
        if (range == 0) {
            return false;
        }
        return offset > 0;
    }

    private boolean canWebViewScrollDown() {
        final int offset = mChildView.getScrollY();
        final int range = mWebViewContentHeight - mChildView.getHeight();
        if (range == 0) {
            return false;
        }
        return offset < range - 1;
    }
    private void setSheetTranslation(float newTranslation) {
        this.sheetTranslation = newTranslation;
        int bottomClip = (int) (getHeight() - Math.ceil(sheetTranslation));
        Log.e("setSheetTranslation1", bottomClip +"");

        getChildAt(0).setTranslationY(bottomClip);
    }

    public void setTranslation(float transY) {
        this.sheetTranslation = getHeight() - transY;
        getChildAt(0).setTranslationY(transY);
    }

    public void recover() {
        currentAnimator = ObjectAnimator.ofFloat(this, SHEET_TRANSLATION, 0);
        currentAnimator.setDuration(300);
        currentAnimator.setInterpolator(new DecelerateInterpolator(1.6f));
        currentAnimator.addListener(new CancelDetectionAnimationListener() {
            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                if (!canceled) {
                    currentAnimator = null;
                }
            }
        });
        currentAnimator.start();

    }

    private boolean isAnimating() {
        return currentAnimator != null;
    }

    private static class CancelDetectionAnimationListener extends AnimatorListenerAdapter {

        protected boolean canceled;

        @Override
        public void onAnimationCancel(Animator animation) {
            canceled = true;
        }

    }

    private void onActionMove(MotionEvent event) {
        float distance = countDragDistanceFromMotionEvent(event);
        mChildView.setTranslationY(distance);
    }

    private void onActionRelease(MotionEvent event) {
        float distance = countDragDistanceFromMotionEvent(event);
        if (mTransYAnim != null && mTransYAnim.isRunning()) {
            mTransYAnim.cancel();
        }

        mTransYAnim = ObjectAnimator.ofFloat(mChildView, View.TRANSLATION_Y,
                mChildView.getTranslationY(), 0.0F);
        mTransYAnim.setDuration(200L);
        mTransYAnim.setInterpolator(PathInterpolatorCompat.create(0.4F, 0.0F, 0.2F, 1.0F));

        mTransYAnim.addUpdateListener(animator -> {

        });

        mTransYAnim.start();
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    private float countDragDistanceFromMotionEvent(@NonNull MotionEvent event) {
        float distance = event.getRawY() - mLatestTouchDownY;

        return distance;
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.e("UpdatePager --> ", "onStartNestedScroll -> child : " + child.getTransitionName() + " target : " + target.getTransitionName());
        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        Log.e("UpdatePager --> ", "onNestedScrollAccepted -> child : " + child.getTransitionName() + " target : " + target.getTransitionName());
        super.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        Log.e("UpdatePager --> ", "onNestedPreScroll -> target : " + target.getTransitionName() + " dx : " + dx + " dy : " + dy + " consumed : x : " + consumed[0] + " y : " + consumed[1]);
        super.onNestedPreScroll(target, dx, dy, consumed);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.e("UpdatePager --> ", "onNestedScroll -> target : " + target.getTransitionName() + " dxConsumed : " + dxConsumed + " dyConsumed : " + dyConsumed + " dxUnconsumed : " + dxUnconsumed + " dyUnconsumed : " + dyUnconsumed);
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.e("UpdatePager --> ", "onNestedPreFling -> target : " + target.getTransitionName() + " velocityX : " + velocityX + " velocityY : " + velocityY);
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.e("UpdatePager --> ", "onNestedFling -> target : " + target.getTransitionName() + " velocityX : " + velocityX + " velocityY : " + velocityY + " consumed : " + consumed);
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }


    @Override
    public void onStopNestedScroll(View child) {
        Log.e("UpdatePager --> ", "onStopNestedScroll -> child : " + child.getTransitionName());
        super.onStopNestedScroll(child);
    }

    @Override
    public int getNestedScrollAxes() {
        Log.e("UpdatePager --> ", "getNestedScrollAxes");
        return super.getNestedScrollAxes();
    }
}
