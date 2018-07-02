package com.jarvis.zhihudemo.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.jarvis.zhihudemo.view.widget.ZHViewPagerInfo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title Futureve
 * @description 该类主要功能描述
 * @create 2018/1/22 上午10:53
 * @changeRecord [修改记录] <br/>
 */

public class ZHDirectionalViewPager extends ViewPager {

    private static final String TAG = "ZHDirectionalViewPager";
    private static final String XML_NS = "http://schemas.android.com/apk/res/android";

    private static final boolean DEBUG = false;
    private static final boolean USE_CACHE = false;
    private static final int INVALID_POINTER = -1;

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Oriection {}

    public static final int PRE = 0x100;
    public static final int NEXT = 0x101;

    private @Oriection int mOrientation = HORIZONTAL;

    static class ItemInfo {
        Object object;
        int position;
        boolean scrolling;
    }

    private final ArrayList<ItemInfo> mItems = new ArrayList<>();

    private PagerAdapter mAdapter;
    private int mCurItem;
    private int mRestoredCurItem;
    private Parcelable mRestoredAdapterState = null;
    private ClassLoader mRestoredClassLoader = null;
    private Scroller mScroller;
    private DataSetObserver mObserver;

    private int mChildWidthMeasureSpec;
    private int mChildHeightMeasureSpec;

    private boolean mInLayout;
    private boolean mScrollingCacheEnabled;
    private boolean mPopulatePending;
    private boolean mScrolling;
    private boolean mIsBeingDragged;
    private boolean mIsUnableToDrag;

    private int mTouchSlop;
    private int mActivePointerId = INVALID_POINTER;

    private float mInitialMotion;
    private float mLastMotionX;
    private float mLastMotionY;

    private ZHViewPagerInfo mPagerInfo;
    private VelocityTracker mVelocityTracker;

    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private List<OnPageChangeListener> mOnPageChangeListeners =
            Collections.synchronizedList(new ArrayList<OnPageChangeListener>());

    private int mScrollState = SCROLL_STATE_IDLE;

    public ZHDirectionalViewPager(Context context) {
        super(context);
        initViewPager(context);
    }

    public ZHDirectionalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViewPager(context);
    }

    private void initViewPager(Context context) {
        setWillNotDraw(false);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mMinimumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
    }

    public void bind(ZHViewPagerInfo pagerInfo) {
        this.mPagerInfo = pagerInfo;
        this.mScroller = new Scroller(getContext(), pagerInfo.interpolator);
        this.setOffscreenPageLimit(pagerInfo.offsetScreenPageLimit);
        this.setOrientation(pagerInfo.oriection);
    }

    public void setOrientation(@Oriection int orientation) {
        switch (orientation) {
            case HORIZONTAL:
            case VERTICAL:
                break;

            default:
                throw new IllegalArgumentException("");
        }

        if (orientation == mOrientation) {
            return;
        }

        completeScroll();

        mInitialMotion = 0;
        mLastMotionX = 0;
        mLastMotionY = 0;
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }

        this.mOrientation = orientation;

        if (mOrientation == HORIZONTAL) {
            scrollTo(mCurItem * getWidth(), 0);
        } else {
            scrollTo(0, mCurItem * getHeight());
        }
        requestLayout();
    }

    private void setScrollState(int newState) {
        if (mScrollState == newState) {
            return;
        }
        mScrollState = newState;
        if (mOnPageChangeListeners != null) {
            notifyPageScrollStateChanged(newState);
        }
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (mAdapter != null) {
            bindDataSetObserver(mAdapter, null);
        }

        mAdapter = adapter;

        if (mAdapter != null) {
            if (mObserver == null) {
                mObserver = new DataSetObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        dataSetChanged();
                    }

                    @Override
                    public void onInvalidated() {
                        super.onInvalidated();
                    }
                };
            }
        }
    }

    @Override
    public PagerAdapter getAdapter() {
        return mAdapter;
    }

    private void dataSetChanged() {
        boolean needPopulate = mItems.isEmpty() && mAdapter.getCount() > 0;
        int newCurrItem = -1;
        for (int i = 0; i < mItems.size(); i ++) {
            final ItemInfo ii = mItems.get(i);
            final int newPos = mAdapter.getItemPosition(ii.object);

            if (newPos == PagerAdapter.POSITION_UNCHANGED) {
                continue;
            }

            if (newPos == PagerAdapter.POSITION_NONE) {
                mItems.remove(i);
                i--;
                mAdapter.destroyItem(this, ii.position, ii.object);
                needPopulate = true;

                if (mCurItem == ii.position) {
                    newCurrItem = Math.max(0, Math.min(mCurItem, mAdapter.getCount() - 1));
                }
                continue;
            }

            if (ii.position != newPos) {
                if (ii.position == mCurItem) {
                    newCurrItem = newPos;
                }
                ii.position = newPos;
                needPopulate = true;
            }
        }

        if (newCurrItem >= 0) {
            setCurrentItemInternal(newCurrItem, false, true);
            needPopulate = true;
        }
        if (needPopulate) {
            populate();
            requestLayout();
        }
    }

    @Override
    public void setCurrentItem(int item) {
        mPopulatePending = false;
        setCurrentItemInternal(item, true, false);
    }

    private void addNewItem(int position, int index) {
        ItemInfo ii = new ItemInfo();
        ii.position = position;
        ii.object = mAdapter.instantiateItem(this, position);
        if (index < 0) {
            mItems.add(ii);
        } else {
            mItems.add(index, ii);
        }
    }

    private void smoothScrollTo(int x, int y) {
        if (getChildCount() == 0) {
            setScrollingCacheEnabled(false);
            return;
        }
        int sx = getScrollX();
        int sy = getScrollY();
        int dx = x - sx;
        int dy = y - sy;
        if (dx == 0 && dy == 0) {
            completeScroll();
            return;
        }

        setScrollingCacheEnabled(true);
        setScrollState(SCROLL_STATE_IDLE);
        mScrolling = true;
        mScroller.startScroll(sx, sy, dx, dy, mPagerInfo.time);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (!mScroller.isFinished()) {
            if (mScroller.computeScrollOffset()) {
                int oldX = getScrollX();
                int oldY = getScrollY();
                int x = mScroller.getCurrX();
                int y = mScroller.getFinalY();

                if (oldX != x || oldY != y) {
                    scrollTo(x, y);
                }

                int size;
                int value;
                if (mOrientation == HORIZONTAL) {
                    size = getWidth();
                    value = x;
                } else {
                    size = getHeight();
                    value = y;
                }

                final int position = value / size;
                final int offsetPixels = value % size;
                final float offset = (float) offsetPixels / size;
                notifyPageScrolled(position, offset, offsetPixels);

                invalidate();
                return;
            }
        }

        completeScroll();
    }

    private void completeScroll() {
        boolean needPopulate;
        if (needPopulate = mScrolling) {
            setScrollingCacheEnabled(false);
            mScroller.abortAnimation();
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            if (oldX != x || oldY != y) {
                scrollTo(x, y);
            }
            setScrollState(SCROLL_STATE_IDLE);
        }
        mPopulatePending = false;
        mScrolling = false;
        for (int i = 0; i < mItems.size(); i++) {
            ItemInfo ii = mItems.get(i);
            if (ii.scrolling) {
                needPopulate = true;
                ii.scrolling = false;
            }
        }
        if (needPopulate) {
            populate();
        }

    }

    private void setCurrentItemInternal(int item, boolean smoothScroll, boolean always) {
        setCurrentItemInternal(item, smoothScroll, always, false, -1, -1);
    }

    private void setCurrentItemInternal(int item, boolean smoothScroll, boolean always, boolean reset, int direction) {
        if (reset) {
            if (direction == PRE) {
            }
        } else {
        }
    }

    private void setCurrentItemInternal(int item,
                                        boolean smoothScroll,
                                        boolean always,
                                        boolean reset,
                                        int direction,
                                        int offset) {
        if (mAdapter == null || mAdapter.getCount() <= 0) {
            setScrollingCacheEnabled(false);
            return;
        }
        if (!always && mCurItem == item && mItems.size() != 0) {
            setScrollingCacheEnabled(false);
            return;
        }
        if (item < 0) {
            item = 0;
        } else if (item >= mAdapter.getCount()) {
            item = mAdapter.getCount() - 1;
        }
        if (item > (mCurItem + 1) || item < (mCurItem - 1)) {
            for (int i = 0; i < mItems.size(); i++) {
                mItems.get(i).scrolling = true;
            }
        }
        final boolean dispatchSelected = mCurItem != item;
        mCurItem = item;
        populate();
        if (smoothScroll) {
            if (mOrientation == HORIZONTAL) {
                if (direction == PRE) {
                    smoothScrollTo(getWidth() * item + mPagerInfo.preOffset, 0);
                }
            } else {
                smoothScrollTo(0, getHeight() * item);
            }
            if (dispatchSelected && mOnPageChangeListeners != null) {
                notifyPageSelected(item);
            }
        } else {
            if (dispatchSelected && mOnPageChangeListeners != null) {
                notifyPageSelected(item);
            }
            completeScroll();
            if (mOrientation == HORIZONTAL) {
                scrollTo(getWidth() * item, 0);
            } else {
                scrollTo(0, getHeight() * item);
            }
        }
    }

    private void populate() {
        if (mAdapter == null) {
            return;
        }
        if (mPopulatePending) {
            return;
        }
        if (getWindowToken() == null) {
            return;
        }
        mAdapter.startUpdate(this);
        final int startPos = mCurItem > 0 ? mCurItem - 1 : mCurItem;
        final int count = mAdapter.getCount();
        final int endPos = mCurItem < (count - 1) ? mCurItem + 1 : mCurItem - 1;

        int lastPos = -1;
        for (int i = 0; i < mItems.size(); i++) {
            ItemInfo ii = mItems.get(i);
            if ((ii.position < startPos || ii.position > endPos) && !ii.scrolling) {
                mItems.remove(i);
                i--;
                mAdapter.destroyItem(this, ii.position, ii.object);
            } else if (lastPos < endPos && ii.position > startPos) {
                lastPos++;
                if (lastPos < startPos) {
                    lastPos = startPos;
                }
                while (lastPos <= endPos && lastPos < ii.position) {
                    addNewItem(lastPos, i);
                    lastPos++;
                    i++;
                }
            }
            lastPos = ii.position;
        }

        lastPos = mItems.size() > 0 ? mItems.get(mItems.size() - 1).position : -1;
        if (lastPos < endPos) {
            lastPos++;
            lastPos = lastPos > startPos ? lastPos : startPos;
            while (lastPos <= endPos) {
                addNewItem(lastPos, -1);
                lastPos++;
            }
        }

        if (DEBUG) {
            Log.i(TAG, "Current page list:");
            for (int i = 0; i < mItems.size(); i++) {
                Log.i(TAG, "#" + i + ": page " + mItems.get(i).position);
            }
        }
        mAdapter.finishUpdate(this);
    }

    private void setScrollingCacheEnabled(boolean enabled) {
        if (mScrollingCacheEnabled != enabled) {
            mScrollingCacheEnabled = enabled;
            if (USE_CACHE) {
                final int size = getChildCount();
                for (int i = 0; i < size; i++) {
                    final View child = getChildAt(i);
                    child.setDrawingCacheEnabled(enabled);
                }
            }
        }
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListeners.add(listener);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (mInLayout) {
            addViewInLayout(child, index, params);
            child.measure(mChildWidthMeasureSpec, mChildHeightMeasureSpec);
        } else {
            super.addView(child, index, params);
        }

        if (USE_CACHE) {
            if (child.getVisibility() != GONE) {
                child.setDrawingCacheEnabled(mScrollingCacheEnabled);
            } else {
                child.setDrawingCacheEnabled(false);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAdapter != null) {
            populate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(0, heightMeasureSpec));
        mChildWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() -
                getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
        mChildHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() -
                getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
        mInLayout = true;
        populate();
        mInLayout = false;
        final int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.measure(mChildWidthMeasureSpec, mChildHeightMeasureSpec);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mOrientation == HORIZONTAL) {
            int scrollPos = mCurItem * w;
            if (scrollPos != getScrollX()) {
                completeScroll();
                scrollTo(scrollPos, getScrollY());
            }
        } else {
            int scrollPos = mCurItem * h;
            if (scrollPos != getScrollY()) {
                completeScroll();
                scrollTo(getScrollX(), scrollPos);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mInLayout = true;
        populate();
        mInLayout = false;

        final int count = getChildCount();
        final int size = (mOrientation == HORIZONTAL) ? r-l : b-t;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            ItemInfo ii;
            if (child.getVisibility() != GONE && (ii = inforForChild(child)) != null) {
                int off = size * ii.position;
                int childLeft = getPaddingLeft();
                int childTop = getPaddingTop();
                if (mOrientation == HORIZONTAL) {
                    childLeft += off;
                } else {
                    childTop += off;
                }
                child.layout(childLeft, childTop,
                        childLeft + child.getMeasuredWidth(),
                        childTop + child.getMeasuredHeight());
            }
        }
    }

    ItemInfo inforForChild(View child) {
        for (int i = 0; i < mItems.size(); i++) {
            ItemInfo ii = mItems.get(i);
            if (mAdapter.isViewFromObject(child, ii.object)) {
                return ii;
            }
        }
        return null;
    }

    public void notifyPageSelected(int position) {
        for (OnPageChangeListener listener : mOnPageChangeListeners) {
            listener.onPageSelected(position);
        }
    }

    public void notifyPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        for (OnPageChangeListener listener : mOnPageChangeListeners) {
            listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    public void notifyPageScrollStateChanged(int state) {
        for (OnPageChangeListener listener : mOnPageChangeListeners) {
            listener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsBeingDragged = false;
            mIsUnableToDrag = false;
            mActivePointerId = INVALID_POINTER;
            return false;
        }

        if (action != MotionEvent.ACTION_DOWN) {
            if (mIsBeingDragged) {
                return true;
            }
            if (mIsUnableToDrag) {
                return false;
            }
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    break;
                }

                final float x = ev.getX();
                final float y = ev.getY();
                final float xDiff = Math.abs(x - mLastMotionX);
                final float yDiff = Math.abs(y - mLastMotionY);
                float primaryDiff;
                float secondaryDiff;

                if (mOrientation == HORIZONTAL) {
                    primaryDiff = xDiff;
                    secondaryDiff = yDiff;
                } else {
                    primaryDiff = yDiff;
                    secondaryDiff = xDiff;
                }

                if (primaryDiff > mTouchSlop && primaryDiff > secondaryDiff) {
                    mIsBeingDragged = true;
                    setScrollState(SCROLL_STATE_DRAGGING);
                    if (mOrientation == HORIZONTAL) {
                        mLastMotionX = x;
                    } else {
                        mLastMotionY = y;
                    }
                    setScrollingCacheEnabled(true);
                } else {
                    if (secondaryDiff > mTouchSlop) {
                        mIsUnableToDrag = true;
                    }
                }
                break;

            case MotionEvent.ACTION_DOWN:
                if (mOrientation == HORIZONTAL) {
                    mLastMotionX = mInitialMotion = ev.getX();
                    mLastMotionY = ev.getY();
                } else {
                    mLastMotionX = ev.getX();
                    mLastMotionY = mInitialMotion = ev.getY();
                }
                if (mScrollState == SCROLL_STATE_SETTLING) {
                    mIsBeingDragged = true;
                    mIsUnableToDrag = false;
                    setScrollState(SCROLL_STATE_DRAGGING);
                } else {
                    completeScroll();
                    mIsBeingDragged = false;
                    mIsUnableToDrag = false;
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            default:
                break;
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
            return false;
        }

        if (mAdapter == null || mAdapter.getCount() == 0) {
            return false;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                completeScroll();

                if (mOrientation == HORIZONTAL) {
                    mLastMotionX = mInitialMotion = ev.getX();
                } else {
                    mLastMotionY = mInitialMotion = ev.getY();
                }
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            case MotionEvent.ACTION_MOVE: {
                if (!mIsBeingDragged) {
                    final float x = ev.getX();
                    final float y = ev.getY();
                    final float xDiff = Math.abs(x - mLastMotionX);
                    final float yDiff = Math.abs(y - mLastMotionY);
                    float primaryDiff;
                    float secondaryDiff;

                    if (mOrientation == HORIZONTAL) {
                        primaryDiff = xDiff;
                        secondaryDiff = yDiff;
                    } else {
                        primaryDiff = yDiff;
                        secondaryDiff = xDiff;
                    }


                    if (primaryDiff > mTouchSlop && primaryDiff > secondaryDiff) {
                        mIsBeingDragged = true;
                        if (mOrientation == HORIZONTAL) {
                            mLastMotionX = x;
                        } else {
                            mLastMotionY = y;
                        }
                        setScrollState(SCROLL_STATE_DRAGGING);
                        setScrollingCacheEnabled(true);
                    }
                }
                if (mIsBeingDragged) {
                    final float x = ev.getX();
                    final float y = ev.getY();

                    int size;
                    float scroll;

                    if (mOrientation == HORIZONTAL) {
                        size = getWidth();
                        scroll = getScrollX() + ((mLastMotionX - x) * 1);
                        mLastMotionX = x;
                    } else {
                        size = getHeight();
                        scroll = getScrollY() + ((mLastMotionY - y) * 1);
                        mLastMotionY = y;
                    }

                    final float lowerBound = Math.max(0, (mCurItem - 1) * size);
                    final float upperBound =
                            Math.min(mCurItem + 1, mAdapter.getCount() - 1) * size;
                    if (scroll < lowerBound) {
                        scroll = lowerBound;
                    } else if (scroll > upperBound) {
                        scroll = upperBound;
                    }
                    if (mOrientation == HORIZONTAL) {
                        // Don't lose the rounded component
                        mLastMotionX += scroll - (int) scroll;
                        scrollTo((int) scroll, getScrollY());
                    } else {
                        // Don't lose the rounded component
                        mLastMotionY += scroll - (int) scroll;
                        scrollTo(getScrollX(), (int) scroll);
                    }
                    if (mOnPageChangeListeners != null) {
                        final int position = (int) scroll / size;
                        final int positionOffsetPixels = (int) scroll % size;
                        final float positionOffset = (float) positionOffsetPixels / size;
                        notifyPageScrolled(position, positionOffset,
                                positionOffsetPixels);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int initialVelocity;
                    float lastMotion;
                    int sizeOverThree;

                    if (mOrientation == HORIZONTAL) {
                        initialVelocity = (int) velocityTracker.getXVelocity(mActivePointerId);
                        lastMotion = mLastMotionX;
                        sizeOverThree = getWidth() / 1;
                    } else {
                        initialVelocity = (int) velocityTracker.getYVelocity(mActivePointerId);
                        lastMotion = mLastMotionY;
                        sizeOverThree = getHeight() / 1;
                    }

                    mPopulatePending = true;
                    if ((Math.abs(initialVelocity) > mMinimumVelocity)
                            || Math.abs(mInitialMotion-lastMotion) >= sizeOverThree) {
                        if (lastMotion > mInitialMotion) {
                            setCurrentItemInternal(mCurItem-1, true, true);
                        } else {
                            setCurrentItemInternal(mCurItem+1, true, true);
                        }
                    } else {
                        setCurrentItemInternal(mCurItem, true, true);
                    }

                    mActivePointerId = INVALID_POINTER;
                    endDrag();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
                    setCurrentItemInternal(mCurItem, true, true);
                    mActivePointerId = INVALID_POINTER;
                    endDrag();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                if (mOrientation == HORIZONTAL) {
                    mLastMotionX = ev.getX();
                } else {
                    mLastMotionY = ev.getY();
                }
                final int index = ev.getActionIndex();
                mActivePointerId = ev.getPointerId(index);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                if (mOrientation == HORIZONTAL) {
                    mLastMotionX = ev.getX();
                } else {
                    mLastMotionY = ev.getY();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            if (mOrientation == HORIZONTAL) {
                mLastMotionX = ev.getX();
            } else {
                mLastMotionY = ev.getY();
            }
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    private void endDrag() {
        mIsBeingDragged = false;
        mIsUnableToDrag = false;

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public static void bindDataSetObserver(PagerAdapter adapter, DataSetObserver observer) {
        adapter.registerDataSetObserver(observer);
    }

    public static class SavedState extends BaseSavedState {
        int position;
        Parcelable adapterState;
        ClassLoader loader;

        public SavedState(Parcelable superState) {
            super(superState);

        }

        @Override
        public void writeToParcel (Parcel out,int flags){
            super.writeToParcel(out, flags);
            out.writeInt(position);
            out.writeParcelable(adapterState, flags);
        }

        @Override
        public String toString () {
            return super.toString();
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.position = mCurItem;
        ss.adapterState = mAdapter.saveState();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        if (mAdapter != null) {
            mAdapter.restoreState(ss.adapterState, ss.loader);
            setCurrentItemInternal(ss.position, false, true);
        } else {
            mRestoredCurItem = ss.position;
            mRestoredAdapterState = ss.adapterState;
            mRestoredClassLoader = ss.loader;
        }
    }
}
