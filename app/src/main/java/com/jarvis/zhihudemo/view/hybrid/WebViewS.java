package com.jarvis.zhihudemo.view.hybrid;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.webkit.WebView;
import android.widget.OverScroller;
import android.widget.ScrollView;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static android.view.MotionEvent.ACTION_CANCEL;

/**
 * Created by yye on 2017/12/7.
 */

public class WebViewS extends WebView implements NestedScrollingChild {

    private boolean mFirstScroll;
    private boolean mDragging;
    private ActionModeWebViewListener mListener;
    WebScrollViewCallbacks mCallbacks;
    WebScrollViewCallbacks.WebScrollViewEventCallbacks mWebScrollViewEventCallbacks;


//        AttributeHolder mHolder = null;

    /**
     * NestedScrollingChild
     */
    protected ArrayList<Rect> mInterruptRectList = new ArrayList<>();
    private NestedScrollingChildHelper mChildHelper;
    private int mLastMotionY;
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private int mNestedYOffset;
    private boolean mIsScrollFromBottom;

    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private VelocityTracker mVelocityTracker;

    private GestureDetector mGestureDetector;

    private OverScrolled overScrolled;

    public interface OverScrolled {
        void onFling(float velocityY);

        void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY);

        void onOverScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent);
    }

    public void registerOverScrolled(OverScrolled scrolled) {
        overScrolled = scrolled;
    }

    public WebViewS(Context context) {
        this(context, null);
    }

    public WebViewS(Context pContext, AttributeSet pAttributeSet) {
        this(pContext, pAttributeSet, 0);
    }

    public WebViewS(final Context pContext, final AttributeSet pAttributeSet, final int pDefaultStyle) {
        super(pContext, pAttributeSet, pDefaultStyle);
//            ThemeManager.updateConfigurationIfNeeded(pContext);
//            getHolder().save(pAttributeSet, pDefaultStyle);
        init();
    }

    private void init() {
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mGestureDetector = new GestureDetector(getContext(), sogl);
        this.setOverScrollMode(OVER_SCROLL_ALWAYS);
    }

    GestureDetector.SimpleOnGestureListener sogl = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e("onFling ------> ", "velocityY : " + velocityY);
            overScrolled.onFling(velocityY);
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };

    @Override
    public void flingScroll(int vx, int vy) {
        super.flingScroll(vx, vy);
        Log.e("flingScroll ------> ", "velocityY : " + vy);
    }

    @Override
    public void setBackgroundResource(@DrawableRes int resid) {
        if (resid < 0) {
            resid = 0;
        }
        super.setBackgroundResource(resid);
//            getHolder().setResId(com.zhihu.android.base.R.styleable.ThemedView_android_background, resid);
    }
    private ScrollState mScrollState;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        if (mCallbacks != null) {
            switch (event.getActionMasked()) {
                default:
                    break;
                case ACTION_CANCEL:
                    this.mDragging = false;
                    break;
            }
        }


        if (!mChildHelper.isNestedScrollingEnabled()) {
            return super.onTouchEvent(event);
        }

        boolean result = false;
        final int actionIndex = MotionEventCompat.getActionIndex(event);
        MotionEvent trackedEvent = MotionEvent.obtain(event);

        boolean eventAddedToVelocityTracker = false;
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        final int action = MotionEventCompat.getActionMasked(event);

        if (action == MotionEvent.ACTION_DOWN) {
            mNestedYOffset = 0;
        }

        int y = (int) event.getY();

        event.offsetLocation(0, mNestedYOffset);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;
                mIsScrollFromBottom = !ViewCompat.canScrollVertically(this, 1);
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                result = super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mIsScrollFromBottom) {
                    int deltaY = mLastMotionY - y;

                    if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                        deltaY -= mScrollConsumed[1];
                        trackedEvent.offsetLocation(0, mScrollOffset[1]);
                        mNestedYOffset += mScrollOffset[1];
                    }

                    int oldY = getScrollY();
                    mLastMotionY = y - mScrollOffset[1];
                    if (deltaY < 0) {
                        int newScrollY = Math.max(0, oldY + deltaY);
                        deltaY -= newScrollY - oldY;
                        if (dispatchNestedScroll(0, newScrollY - deltaY, 0, deltaY, mScrollOffset)) {
                            mLastMotionY -= mScrollOffset[1];
                            trackedEvent.offsetLocation(0, mScrollOffset[1]);
                            mNestedYOffset += mScrollOffset[1];
                        }
                    }
                }

                result = super.onTouchEvent(trackedEvent);
                trackedEvent.recycle();

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
//                this.mIntercepted = false;
                this.mDragging = false;
                if (mWebScrollViewEventCallbacks != null){
                    this.mWebScrollViewEventCallbacks.onUpOrCancelMotionEvent(this.mScrollState, event.getX(), y);
                }

                mVelocityTracker.addMovement(event);
                eventAddedToVelocityTracker = true;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int mScrollPointerId = MotionEventCompat.getPointerId(event, actionIndex);
                float vY = -VelocityTrackerCompat.getYVelocity(mVelocityTracker, mScrollPointerId);
                // 产生 fling 事件
                if (Math.abs(vY) > mMinimumVelocity && !dispatchNestedPreFling(0, vY)) {
                    dispatchNestedFling(0, vY, true);
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                stopNestedScroll();
                result = super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                stopNestedScroll();
                result = super.onTouchEvent(event);
                break;
            default:
                break;

        }
        if (!eventAddedToVelocityTracker) {
            mVelocityTracker.addMovement(event);
        }
        return result;
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    /**
     * See {@link SwipeRefreshLayout#requestDisallowInterceptTouchEvent(boolean)}
     */
    @Override
    public boolean isNestedScrollingEnabled() {
        return mInterruptRectList.size() > 0 || mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed,
                                        int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

//        @Override
//        public void resetStyle() {
//            getHolder().resetViewAttr();
//            getHolder().afterReset();
//        }
//
//        public AttributeHolder getHolder() {
//            if (mHolder == null) {
//                mHolder = new AttributeHolder(this);
//            }
//            return mHolder;
//        }

    public int getVerticalScrollRange() {
        return computeVerticalScrollRange();
    }

    public void setActionModeWebViewListener(ActionModeWebViewListener listener) {
        mListener = listener;
    }
    private int mPrevScrollY;

    @Override
    public int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    @Override
    public int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }

    @Override
    public int computeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mCallbacks != null) {
            mCallbacks.onScrollChanged(t, this.mFirstScroll, this.mDragging);
        }

        if (this.mFirstScroll) {
            this.mFirstScroll = false;
        }
        if(this.mPrevScrollY < t) {
            this.mScrollState = ScrollState.UP;
        } else if(t < this.mPrevScrollY) {
            this.mScrollState = ScrollState.DOWN;
        } else {
            this.mScrollState = ScrollState.STOP;
        }
        this.mPrevScrollY = t;
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        Log.e("overScrollBy -----> ", " deltaY : " + deltaY + " scrollY : " + scrollY + " scrollRangeY : " +
                scrollRangeY);
        overScrolled.onOverScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
        if (mCallbacks != null) {
            mCallbacks.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mCallbacks != null) {
            switch (ev.getActionMasked()) {
                case 0:
                    this.mFirstScroll = this.mDragging = true;
                    if (mWebScrollViewEventCallbacks != null){
                        this.mWebScrollViewEventCallbacks.onDownMotionEvent();
                    }
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public ActionMode startActionMode(final ActionMode.Callback callback) {

        // M 之后走 startActionMode(ActionMode.Callback callback, int type)
        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && mListener != null) {
            mListener.onActionModeStart();
        }

        return super.startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mListener.onCreateActionMode(mode, menu);
                return callback.onCreateActionMode(mode, menu);
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return callback.onPrepareActionMode(mode, menu);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return processWithShareItem(item) || callback.onActionItemClicked(mode, item);
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                callback.onDestroyActionMode(mode);
                if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && mListener != null) {
                    mListener.onActionModeDestroy();
                }

            }
        });
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public ActionMode startActionMode(final ActionMode.Callback callback, int type) {
        ViewParent viewParent = this.getParent();
        if (viewParent != null && viewParent instanceof SwipeRefreshLayout) {
            ((SwipeRefreshLayout) viewParent).setEnabled(false);
        }

        if (mListener != null) {
            mListener.onActionModeStart();
        }

        return super.startActionMode(new ActionMode.Callback2() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mListener.onCreateActionMode(mode, menu);
                return callback.onCreateActionMode(mode, menu);
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return callback.onPrepareActionMode(mode, menu);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return processWithShareItem(item) || callback.onActionItemClicked(mode, item);
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                callback.onDestroyActionMode(mode);
                if (mListener != null) {
                    mListener.onActionModeDestroy();
                }

            }

            @Override
            public void onGetContentRect(ActionMode mode, View view, Rect outRect) {
                if (callback instanceof ActionMode.Callback2) {
                    ((ActionMode.Callback2) callback).onGetContentRect(mode, view, outRect);
                } else {
                    super.onGetContentRect(mode, view, outRect);
                }
            }
        }, type);
    }

    protected boolean processWithShareItem(MenuItem item) {
        int shareItemStringId = Resources.getSystem().getIdentifier("share", "string", "android");
        if (shareItemStringId == 0) {
            return false;
        }
        String shareItemString = Resources.getSystem().getString(shareItemStringId);
        if (item.getTitle().equals(shareItemString)) {

            if (mListener != null) {
                mListener.onActionModeShare();
                return true;
            }
//            String jsGetSelectionText = getContext().getString(R.string.hybrid_get_selection_text);
//
//            this.evaluateJavascript(jsGetSelectionText, new ValueCallback<String>() {
//                @Override
//                public void onReceiveValue(String value) {
//                    if (!TextUtils.isEmpty(value)) {
//                        //
////                            WebViewS.this.post(() ->
////                                    BaseFragmentActivity.from(getContext()).startFragment(ShareFragment.buildIntent(getContext().getString(R.string.share_subject_app), value)));
//
//                    }
//                }
//            });
            return true;
        }

        return false;
    }


    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        Log.e("onOverScrolled ------> ", "scrollX : " + scrollX + " scrollY : " + scrollY + " clampedX : " + clampedX + " clampedY : " + clampedY);
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        overScrolled.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }



    public enum ScrollState {
        STOP,
        UP,
        DOWN;

        private ScrollState() {
        }
    }

    public interface WebScrollViewCallbacks {
        void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging);
        boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent);

        interface WebScrollViewEventCallbacks {
            void onDownMotionEvent();
            void onUpOrCancelMotionEvent(ScrollState scrollState, float touchX, float touchY);
        }
    }

    public interface ActionModeWebViewListener {
        void onActionModeStart();
        void onActionModeShare();
        void onActionModeDestroy();
        boolean onCreateActionMode(ActionMode mode, Menu menu);
    }

}
