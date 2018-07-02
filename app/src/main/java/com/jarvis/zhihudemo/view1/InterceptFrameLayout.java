package com.jarvis.zhihudemo.view1;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.webkit.WebView;
import android.widget.FrameLayout;

/**
 * @author yyf @ Zhihu Inc.
 * @since 06-26-2018
 */
public class InterceptFrameLayout extends FrameLayout {

    private View mChildView;

    private ObjectAnimator mTransYAnim;

    private float mLatestTouchDownY;
    private float mTouchSlop;

    private VelocityTracker velocityTracker;
    private float minFlingVelocity;

    /** Snapshot of the touch's y position on a down event */
    private float downY;

    /** Snapshot of the touch's x position on a down event */
    private float downX;

    /** Snapshot of the sheet's translation at the time of the last down event */
    private float downSheetTranslation;


    public boolean bottomSheetOwnsTouch;
    private boolean sheetViewOwnsTouch;


    private boolean hasIntercepted;

    private boolean isHoldTouch = true;

    private float sheetTranslation;

    private float currentTranslation;


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

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
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

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            bottomSheetOwnsTouch = false;
            sheetViewOwnsTouch = false;
            downY = event.getY();
            downX = event.getX();
            downSheetTranslation = sheetTranslation;
            velocityTracker.clear();
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
                    (((scrollingUp && !canScrollUp) && newSheetTranslation > maxSheetTranslation) || ((scrollingDown && !canScrollDown) && newSheetTranslation < maxSheetTranslation))) {
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
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, SHEET_TRANSLATION, 0);
        anim.setDuration(300);
        anim.setInterpolator(new DecelerateInterpolator(1.6f));

        anim.start();

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

    private float countDragDistanceFromMotionEvent(@NonNull MotionEvent event) {
        float distance = event.getRawY() - mLatestTouchDownY;

        return distance;
    }
}
