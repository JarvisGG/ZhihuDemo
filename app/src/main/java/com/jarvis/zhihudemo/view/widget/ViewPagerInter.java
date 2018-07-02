package com.jarvis.zhihudemo.view.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;

import com.jarvis.zhihudemo.fragment.TestFragment;
import com.jarvis.zhihudemo.view.VerticalViewPager2;

import java.util.ArrayList;
import java.util.List;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/2/1 下午8:38
 * @changeRecord [修改记录] <br/>
 */

public class ViewPagerInter extends VerticalViewPager2 {

    private float lastY = 0;
    private float lastX = 0;
    private float y = 0;
    private float x = 0;
    private int deltaY = 0;
    private int deltaX = 0;
    private boolean isIntercept = false;
    boolean isInit = false;
    int durection = 0;
    int oldTop;

    public List<BoundDragListener> mBoundDragListeners;

    OnLayoutChangeListener layoutChangeListener = new OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            ViewPagerInter.this.oldTop = oldTop;
            notifyOnBoundDragListener(top);
        }
    };

    public interface BoundDragListener {
        void onBoundDragCallback(int boundOffset);
    }

    public ViewPagerInter(Context context) {
        super(context);
    }

    public ViewPagerInter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.addOnLayoutChangeListener(layoutChangeListener);
        this.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels, int direction) {
                Log.e("onPageScrolled -----> ", "position: " + position + " positionOffset: " + positionOffset
                        + " positionOffsetPixels: " + positionOffsetPixels + " direction: " + (direction == PRE ? "PRE" : "NEXT"));
//                if (!isInit) {
//                    durection = position;
//                    isInit = true;
//                } else {
//                    if (position != direction) {
//                        isIntercept = true;
//                    }
//                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    boolean a = false;


    private int currentPosition = 0;
    private Rect mRect = new Rect();//用来记录初始位置
    private boolean handleDefault = true;
    private float preY = 0f;
    private static final float RATIO = 0.5f;//摩擦系数
    private static final float SCROLL_WIDTH = 10f;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == ACTION_DOWN) {
            preY = event.getY();
            currentPosition = getCurrentItem();
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case ACTION_DOWN:
                lastY = y = event.getRawY();
                lastX = x = event.getX();
                isInit = false;
                break;
            case ACTION_MOVE:
                if (!a) {
                    lastY = y = event.getRawY();
                    lastX = x = event.getX();
                    a = true;
                    onTouchActionUp();
                } else {
                    y = event.getRawY();
                    x = event.getX();
                    deltaY = (int) (lastY - y);
                    deltaX = (int) (lastX - x);
                    if (!isInit) {
                        isInit = true;
                        if (deltaY > 0) {
                            durection = 1;
                        } else {
                            durection = -1;
                        }
                    } else {
                        int res = 0;
                        if (deltaY > 0) {
                            res = 1;
                        } else {
                            res = -1;
                        }
                        if (res != durection) {
                            return true;
                        }
                    }
                }


                if (getAdapter().getCount() == 1) {
                    float nowY = event.getY();
                    float offset = nowY - preY;
                    preY = nowY;
                    if (offset > SCROLL_WIDTH) {
                        whetherConditionIsBottom(offset);
                    } else if (offset < -SCROLL_WIDTH) {
                        whetherConditionIsBottom(offset);
                    } else if (!handleDefault) {
                        if (getTop() + (int) (offset * RATIO) != mRect.top) {
                            layout(getLeft(), getTop() + (int) (offset * RATIO), getRight(), getBottom() + (int) (offset * RATIO));
                            notifyOnBoundDragListener(getTop() + (int) (offset * RATIO));
                        }
                    }
                } else if ((currentPosition == 0 || currentPosition == getAdapter().getCount() - 1)) {
                    float nowY = event.getY();
                    float offset = nowY - preY;
                    preY = nowY;

                    if (currentPosition == 0) {
                        if (offset > SCROLL_WIDTH) {
                            whetherConditionIsBottom(offset);
                        } else if (!handleDefault) {
                            if (getTop() + (int) (offset * RATIO) >= mRect.top) {
                                layout(getLeft(), getTop() + (int) (offset * RATIO), getRight(), getBottom() + (int) (offset * RATIO));
                                notifyOnBoundDragListener(getTop() + (int) (offset * RATIO));
                            }
                        }
                    } else {
                        if (offset < -SCROLL_WIDTH) {
                            whetherConditionIsBottom(offset);
                        } else if (!handleDefault) {
                            if (getBottom() + (int) (offset * RATIO) <= mRect.bottom) {
                                layout(getLeft(), getTop() + (int) (offset * RATIO), getRight(), getBottom() + (int) (offset * RATIO));
                                notifyOnBoundDragListener(getTop() + (int) (offset * RATIO));
                            }
                        }
                    }
                } else {
                    handleDefault = true;
                }

                if (!handleDefault) {
                    return true;
                }

                break;
            case ACTION_UP:
                isInit = false;
                a = false;
                onTouchActionUp();
                break;
            default:
                break;
        }
//        switch (event.getAction()) {
//            case ACTION_DOWN:
//                lastY = y = event.getY();
//                lastX = x = event.getX();
//
//                Object obj = getAdapter().instantiateItem(this, getCurrentItem());
//                if (obj instanceof TestFragment) {
//                    WebView webView = ((TestFragment) obj).getWebView();
//                    webView.dispatchTouchEvent(event);
//                    super.onTouchEvent(event);
//                }
//
//                break;
//            case ACTION_MOVE:
//                y = event.getY();
//                x = event.getX();
//                deltaY = (int) (lastY - y);
//                deltaX = (int) (lastX - x);
//
//                Object ob = getAdapter().instantiateItem(this, getCurrentItem());
//                if (ob instanceof TestFragment) {
//                    WebView webView = ((TestFragment) ob).getWebView();
//                    if (!webView.canScrollVertically(-1) && deltaY < 0) {
//                        return super.onTouchEvent(event);
//                    } else if (!webView.canScrollVertically(1) && deltaY > 0) {
//                        return super.onTouchEvent(event);
//                    } else {
//                        return webView.dispatchTouchEvent(event);
//                    }
//                }
//                break;
//            case ACTION_UP:
//                Object obj1 = getAdapter().instantiateItem(this, getCurrentItem());
//                if (obj1 instanceof TestFragment) {
//                    WebView webView = ((TestFragment) obj1).getWebView();
//                    if (!webView.canScrollVertically(-1) && deltaY < 0) {
//                        return super.onTouchEvent(event);
//                    } else if (!webView.canScrollVertically(1) && deltaY > 0) {
//                        return super.onTouchEvent(event);
//                    } else {
//                        return webView.dispatchTouchEvent(event);
//                    }
//                }
//                break;
//            default:
//                break;
//
//        }

        return super.onTouchEvent(event);
    }

    private void whetherConditionIsBottom(float offset) {
        if (mRect.isEmpty()) {
            mRect.set(getLeft(), getTop(), getRight(), getBottom());
        }
        handleDefault = false;
        layout(getLeft(), getTop() + (int) (offset * RATIO), getRight(), getBottom() + (int) (offset * RATIO));
    }

    private void onTouchActionUp() {
        if (!mRect.isEmpty()) {
            recoveryPosition();
        }
    }

    private void recoveryPosition() {

        ValueAnimator animator = ValueAnimator.ofFloat(oldTop, 0);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                layout(getLeft(),
                        (int)(0 + (float)valueAnimator.getAnimatedValue()),
                        getRight(),
                        (int)(getMeasuredHeight() + (float)valueAnimator.getAnimatedValue()));
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                oldTop = 0;
                mRect.setEmpty();
                handleDefault = true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();

//        ExTranslateAnimation ta = new ExTranslateAnimation(getLeft(), mRect.left, getTop(), mRect.top);
//        ta.setDuration(300);
//        startAnimation(ta);
//        ta.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        layout(mRect.left, mRect.top, mRect.right, mRect.bottom);
//        mRect.setEmpty();
//        handleDefault = true;
    }




    private boolean isOnBottom() {
        boolean res = false;

        Fragment fragment = ((FragmentPagerAdapter)this.getAdapter()).getItem(getCurrentItem());

        View view = fragment.getView();

//
//        if (fragment != null) {
//            int height = fragment.getContentHeight();
//            res = height <= fragment.getAppView().getView().getScrollY() + DisplayUtils.getScreenHeightPixels(getContext()) + 10;
//        }
        return res;
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dy, int x, int y) {
        return super.canScroll(v, checkV, dy, x, y);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return super.canScrollVertically(direction);
    }

    public void addOnBoundDragListener(BoundDragListener listener) {
        if (mBoundDragListeners == null) {
            mBoundDragListeners = new ArrayList<>();
        }
        mBoundDragListeners.add(listener);
    }

    public void removeOnBoundDragListener(BoundDragListener listener) {
        if (mBoundDragListeners != null) {
            mBoundDragListeners.remove(listener);
        }
    }

    public void clearOnBoundDragListener() {
        if (mBoundDragListeners != null) {
            mBoundDragListeners.clear();
        }
    }

    private void notifyOnBoundDragListener(int bottomOffset) {
        for (BoundDragListener listener : mBoundDragListeners) {
            listener.onBoundDragCallback(bottomOffset);
        }
    }

    class ExTranslateAnimation extends TranslateAnimation {

        public ExTranslateAnimation(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ExTranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
            super(fromXDelta, toXDelta, fromYDelta, toYDelta);
        }

        public ExTranslateAnimation(int fromXType, float fromXValue, int toXType, float toXValue, int fromYType, float fromYValue, int toYType, float toYValue) {
            super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType, toYValue);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            String key = t.toShortString();

            notifyOnBoundDragListener((int) (oldTop * (1 - interpolatedTime)));

            Log.e("applyTransformation -> ", "interpolatedTime : " + (oldTop - interpolatedTime * oldTop) + " Transformation : " + t.toString());
        }

    }



}
