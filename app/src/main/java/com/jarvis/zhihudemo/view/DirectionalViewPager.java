package com.jarvis.zhihudemo.view;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.jarvis.zhihudemo.view.widget.ViewPagerInfo;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/19 下午3:44
 * @changeRecord [修改记录] <br/>
 */

public class DirectionalViewPager extends ViewPager {
    private static final String TAG = "DirectionalViewPager";
    private static final String XML_NS = "http://schemas.android.com/apk/res/android";
    private static final boolean DEBUG = false;

    private static final boolean USE_CACHE = false;

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Oriection {}

    public @Oriection int mOrientation = HORIZONTAL;

    public static final int PRE = 0x100;
    public static final int NEXT = 0x101;

    static class ItemInfo {
        Object object;
        int position;
        boolean scrolling;
    }

    private final ArrayList<ItemInfo> mItems = new ArrayList<ItemInfo>();

    private PagerAdapter mAdapter;
    private int mCurItem;
    private int mRestoredCurItem = -1;
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
    private float mInitialMotion;
    /**
     * Position of the last motion event.
     */
    private float mLastMotionX;
    private float mLastMotionY;
    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    private int mActivePointerId = INVALID_POINTER;
    /**
     * Sentinel value for no current active pointer.
     * Used by {@link #mActivePointerId}.
     */
    private static final int INVALID_POINTER = -1;

    private ViewPagerInfo mPagerInfo;

    /**
     * Determines speed during touch scrolling
     */
    private VelocityTracker mVelocityTracker;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private List<OnPageChangeListener> mOnPageChangeListeners =
            Collections.synchronizedList(new ArrayList<OnPageChangeListener>());

    private int mScrollState = SCROLL_STATE_IDLE;

    public DirectionalViewPager(Context context) {
        super(context);
        initViewPager();
    }

    public DirectionalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViewPager();

        //We default to horizontal, only change if a value is explicitly specified
        int orientation = attrs.getAttributeIntValue(XML_NS, "orientation", -1);
        if (orientation != -1) {
            setOrientation(orientation);
        }
    }

    void initViewPager() {
        setWillNotDraw(false);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
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
            VerticalViewPagerCompat.setDataSetObserver(mAdapter, null);
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
            mAdapter.registerDataSetObserver(mObserver);
            mPopulatePending = false;
            if (mRestoredCurItem >= 0) {
                mAdapter.restoreState(mRestoredAdapterState, mRestoredClassLoader);
                setCurrentItemInternal(mRestoredCurItem, false, true);
                mRestoredCurItem = -1;
                mRestoredAdapterState = null;
                mRestoredClassLoader = null;
            } else {
                populate();
            }
        }
    }

    public void bind(ViewPagerInfo pagerInfo) {
        this.mPagerInfo = pagerInfo;
        this.setOffscreenPageLimit(pagerInfo.offsetScreenPageLimit);
        this.setOrientation(pagerInfo.oriection);
        this.mScroller = new Scroller(getContext(), pagerInfo.interpolator);
    }

    @Override
    public PagerAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setCurrentItem(int item) {
        mPopulatePending = false;
        setCurrentItemInternal(item, true, false);
    }

    private void setCurrentItemInternal(int item, boolean smoothScroll, boolean always) {
        setCurrentItemInternal(item, smoothScroll, always, false, -1, -1);
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
        if (item > (mCurItem+1) || item < (mCurItem-1)) {
            // We are doing a jump by more than one page.  To avoid
            // glitches, we want to keep all current pages in the view
            // until the scroll ends.
            for (int i=0; i<mItems.size(); i++) {
                mItems.get(i).scrolling = true;
            }
        }
        final boolean dispatchSelected = mCurItem != item;
        mCurItem = item;
        populate();
        if (smoothScroll) {
            if (mOrientation == HORIZONTAL) {
                if (reset) {
                    if (direction == PRE) {
                        smoothScrollTo(getWidth() * item - offset, 0);
                    } else if (direction == NEXT) {
                        smoothScrollTo(getWidth() * item + offset, 0);
                    }
                } else {
                    smoothScrollTo(getWidth() * item, 0);
                }
            } else {
                if (reset) {
                    if (direction == PRE) {
                        smoothScrollTo(0, getHeight() * item - offset);
                    } else if (direction == NEXT) {
                        smoothScrollTo(0, getHeight() * item + offset);
                    }
                } else {
                    smoothScrollTo(0, getHeight() * item);
                }
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
                scrollTo(getWidth()*item, 0);
            } else {
                scrollTo(0, getHeight()*item);
            }
        }
    }

    public void addDirectionOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListeners.add(listener);
    }

    /**
     * Like {@link View#scrollBy}, but scroll smoothly instead of immediately.
     *
     * @param x the number of pixels to scroll by on the X axis
     * @param y the number of pixels to scroll by on the Y axis
     */
    void smoothScrollTo(int x, int y) {
        if (getChildCount() == 0) {
            // Nothing to do.
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
        mScrolling = true;
        setScrollState(SCROLL_STATE_SETTLING);
        mScroller.startScroll(sx, sy, dx, dy, mPagerInfo.time);
        invalidate();
    }

    void addNewItem(int position, int index) {
        ItemInfo ii = new ItemInfo();
        ii.position = position;
        ii.object = mAdapter.instantiateItem(this, position);
        if (index < 0) {
            mItems.add(ii);
        } else {
            mItems.add(index, ii);
        }
    }

    void dataSetChanged() {
        // This method only gets called if our observer is attached, so mAdapter is non-null.

        boolean needPopulate = mItems.isEmpty() && mAdapter.getCount() > 0;
        int newCurrItem = -1;

        for (int i = 0; i < mItems.size(); i++) {
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
                    // Keep the current item in the valid range
                    newCurrItem = Math.max(0, Math.min(mCurItem, mAdapter.getCount() - 1));
                }
                continue;
            }

            if (ii.position != newPos) {
                if (ii.position == mCurItem) {
                    // Our current item changed position. Follow it.
                    newCurrItem = newPos;
                }

                ii.position = newPos;
                needPopulate = true;
            }
        }

        if (newCurrItem >= 0) {
            // TODO This currently causes a jump.
            setCurrentItemInternal(newCurrItem, false, true);
            needPopulate = true;
        }
        if (needPopulate) {
            populate();
            requestLayout();
        }
    }

    void populate() {
        if (mAdapter == null) {
            return;
        }

        // Bail now if we are waiting to populate.  This is to hold off
        // on creating views from the time the user releases their finger to
        // fling to a new position until we have finished the scroll to
        // that position, avoiding glitches from happening at that point.
        if (mPopulatePending) {
            if (DEBUG) {
                Log.i(TAG, "populate is pending, skipping for now...");
            }
            return;
        }

        // Also, don't populate until we are attached to a window.  This is to
        // avoid trying to populate before we have restored our view hierarchy
        // state and conflicting with what is restored.
        if (getWindowToken() == null) {
            return;
        }

        mAdapter.startUpdate(this);

        final int startPos = mCurItem > 0 ? mCurItem - 1 : mCurItem;
        final int count = mAdapter.getCount();
        final int endPos = mCurItem < (count-1) ? mCurItem+1 : count-1;

        if (DEBUG) {
            Log.v(TAG, "populating: startPos=" + startPos + " endPos=" + endPos);
        }

        // Add and remove pages in the existing list.
        int lastPos = -1;
        for (int i=0; i<mItems.size(); i++) {
            ItemInfo ii = mItems.get(i);
            if ((ii.position < startPos || ii.position > endPos) && !ii.scrolling) {
                if (DEBUG) {
                    Log.i(TAG, "removing: " + ii.position + " @ " + i);
                }
                mItems.remove(i);
                i--;
                mAdapter.destroyItem(this, ii.position, ii.object);
            } else if (lastPos < endPos && ii.position > startPos) {
                // The next item is outside of our range, but we have a gap
                // between it and the last item where we want to have a page
                // shown.  Fill in the gap.
                lastPos++;
                if (lastPos < startPos) {
                    lastPos = startPos;
                }
                while (lastPos <= endPos && lastPos < ii.position) {
                    if (DEBUG) {
                        Log.i(TAG, "inserting: " + lastPos + " @ " + i);
                    }
                    addNewItem(lastPos, i);
                    lastPos++;
                    i++;
                }
            }
            lastPos = ii.position;
        }

        // Add any new pages we need at the end.
        lastPos = mItems.size() > 0 ? mItems.get(mItems.size()-1).position : -1;
        if (lastPos < endPos) {
            lastPos++;
            lastPos = lastPos > startPos ? lastPos : startPos;
            while (lastPos <= endPos) {
                if (DEBUG) {
                    Log.i(TAG, "appending: " + lastPos);
                }
                addNewItem(lastPos, -1);
                lastPos++;
            }
        }

        if (DEBUG) {
            Log.i(TAG, "Current page list:");
            for (int i=0; i<mItems.size(); i++) {
                Log.i(TAG, "#" + i + ": page " + mItems.get(i).position);
            }
        }

        mAdapter.finishUpdate(this);
    }

    public static class SavedState extends BaseSavedState {
        int position;
        Parcelable adapterState;
        ClassLoader loader;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(position);
            out.writeParcelable(adapterState, flags);
        }

        @Override
        public String toString() {
            return "FragmentPager.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " position=" + position + "}";
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }
            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        });

        SavedState(Parcel in, ClassLoader loader) {
            super(in);
            if (loader == null) {
                loader = getClass().getClassLoader();
            }
            position = in.readInt();
            adapterState = in.readParcelable(loader);
            this.loader = loader;
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

        SavedState ss = (SavedState)state;
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

    public int getOrientation() {
        return mOrientation;
    }

    public void setOrientation(@Oriection int orientation) {
        switch (orientation) {
            case HORIZONTAL:
            case VERTICAL:
                break;

            default:
                throw new IllegalArgumentException("Only HORIZONTAL and VERTICAL are valid orientations.");
        }

        if (orientation == mOrientation) {
            return;
        }

        //Complete any scroll we are currently in the middle of
        completeScroll();

        //Reset values
        mInitialMotion = 0;
        mLastMotionX = 0;
        mLastMotionY = 0;
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }

        //Adjust scroll for new orientation
        mOrientation = orientation;
        if (mOrientation == HORIZONTAL) {
            scrollTo(mCurItem * getWidth(), 0);
        } else {
            scrollTo(0, mCurItem * getHeight());
        }
        requestLayout();
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

    ItemInfo infoForChild(View child) {
        for (int i=0; i<mItems.size(); i++) {
            ItemInfo ii = mItems.get(i);
            if (mAdapter.isViewFromObject(child, ii.object)) {
                return ii;
            }
        }
        return null;
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
        // For simple implementation, or internal size is always 0.
        // We depend on the container to specify the layout size of
        // our view.  We can't really know what it is since we will be
        // adding and removing different arbitrary views and do not
        // want the layout to change as this happens.
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(0, heightMeasureSpec));

        // Children are just made to fill our space.
        mChildWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() -
                getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
        mChildHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() -
                getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);

        // Make sure we have created all fragments that we need to have shown.
        mInLayout = true;
        populate();
        mInLayout = false;

        // Make sure all children have been properly measured.
        final int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                if (DEBUG) {
                    Log.v(TAG, "Measuring #" + i + " " + child
                            + ": " + mChildWidthMeasureSpec + " x " + mChildHeightMeasureSpec);
                }
                child.measure(mChildWidthMeasureSpec, mChildHeightMeasureSpec);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Make sure scroll position is set correctly.
        if (mOrientation == HORIZONTAL) {
            int scrollPos = mCurItem*w;
            if (scrollPos != getScrollX()) {
                completeScroll();
                scrollTo(scrollPos, getScrollY());
            }
        } else {
            int scrollPos = mCurItem*h;
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
            if (child.getVisibility() != GONE && (ii=infoForChild(child)) != null) {
                int off = size*ii.position;
                int childLeft = getPaddingLeft();
                int childTop = getPaddingTop();
                if (mOrientation == HORIZONTAL) {
                    childLeft += off;
                } else {
                    childTop += off;
                }
                if (DEBUG) {
                    Log.v(TAG, "Positioning #" + i + " " + child + " f=" + ii.object
                            + ":" + childLeft + "," + childTop + " " + child.getMeasuredWidth()
                            + "x" + child.getMeasuredHeight());
                }
                child.layout(childLeft, childTop,
                        childLeft + child.getMeasuredWidth(),
                        childTop + child.getMeasuredHeight());
            }
        }
    }

    @Override
    public void computeScroll() {
        if (DEBUG) {
            Log.i(TAG, "computeScroll: finished=" + mScroller.isFinished());
        }
        if (!mScroller.isFinished()) {
            if (mScroller.computeScrollOffset()) {
                if (DEBUG) {
                    Log.i(TAG, "computeScroll: still scrolling");
                }
                int oldX = getScrollX();
                int oldY = getScrollY();
                int x = mScroller.getCurrX();
                int y = mScroller.getCurrY();

                int duration = -1;
                if (mOrientation == HORIZONTAL) {
                    if (oldX < x) {
                        duration = NEXT;
                    } else {
                        duration = PRE;
                    }
                } else {
                    if (oldY < y) {
                        duration = NEXT;
                    } else {
                        duration = PRE;
                    }
                }

                if (oldX != x || oldY != y) {
                    scrollTo(x, y);
                }

                if (mOnPageChangeListeners != null) {
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
                    notifyPageScrolled(position, offset, offsetPixels, duration, true);
                }

                // Keep on drawing until the animation has finished.
                invalidate();
                return;
            }
        }

        // Done with scroll, clean up state.
        completeScroll();
    }

    private void completeScroll() {
        boolean needPopulate;
        if ((needPopulate=mScrolling)) {
            // Done with scroll, no longer want to cache view drawing.
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
        for (int i=0; i<mItems.size(); i++) {
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
                        int currentScrollX = getScrollX() - mCurItem * getWidth();
                        if (currentScrollX > 0) {
                            if (currentScrollX >= mPagerInfo.nextOffset) {
                                scroll = currentScrollX + ((mLastMotionX - x) * mPagerInfo.fator) + mCurItem * getWidth();
                            } else {
                                scroll = currentScrollX + mLastMotionX - x + mCurItem * getWidth();
                            }
                        } else {
                            if (-currentScrollX >= mPagerInfo.preOffset) {
                                scroll = currentScrollX + ((mLastMotionX - x) * mPagerInfo.fator) + mCurItem * getWidth();
                            } else {
                                scroll = currentScrollX + mLastMotionX - x + mCurItem * getWidth();
                            }
                        }
                        mLastMotionX = x;
                    } else {
                        size = getHeight();
                        int currentScrollY = getScrollY() - mCurItem * getHeight();
                        if (currentScrollY > 0) {
                            if (currentScrollY >= mPagerInfo.nextOffset) {
                                scroll = currentScrollY + ((mLastMotionY - y) * mPagerInfo.fator) + mCurItem * getHeight();
                            } else {
                                scroll = currentScrollY + (mLastMotionY - y) + mCurItem * getHeight();
                            }
                        } else {
                            if (-currentScrollY >= mPagerInfo.preOffset) {
                                scroll = currentScrollY + ((mLastMotionY - y) * mPagerInfo.fator) + mCurItem * getHeight();
                            } else {
                                scroll = currentScrollY + (mLastMotionY - y) + mCurItem * getHeight();
                            }
                        }
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
                    int duration = -1;
                    if (mOrientation == HORIZONTAL) {
                        mLastMotionX += scroll - (int) scroll;
                        if (getScrollX() < scroll) {
                            duration = NEXT;
                        } else {
                            duration = PRE;
                        }
                        scrollTo((int) scroll, getScrollY());
                    } else {
                        if (getScrollY() < scroll) {
                            duration = NEXT;
                        } else {
                            duration = PRE;
                        }
                        mLastMotionY += scroll - (int) scroll;
                        scrollTo(getScrollX(), (int) scroll);
                    }
                    if (mOnPageChangeListeners != null) {
                        final int position = (int) scroll / size;
                        final int positionOffsetPixels = (int) scroll % size;
                        final float positionOffset = (float) positionOffsetPixels / size;
                        notifyPageScrolled(position, positionOffset,
                                positionOffsetPixels, duration, false);
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
                        sizeOverThree = (int) (getWidth() * mPagerInfo.switchMultiple);
                    } else {
                        initialVelocity = (int) velocityTracker.getYVelocity(mActivePointerId);
                        lastMotion = mLastMotionY;
                        sizeOverThree = (int) (getHeight() * mPagerInfo.switchMultiple);
                    }

                    mPopulatePending = true;
                    int currentScrollY = getScrollY() - mCurItem * getHeight();
                    if (currentScrollY > 0) {
                        if (currentScrollY > mPagerInfo.nextOffset) {
                            excuteActionUpMotion(initialVelocity, lastMotion, sizeOverThree, NEXT);
                        }
                    } else {
                        if (-currentScrollY > mPagerInfo.preOffset) {
                            excuteActionUpMotion(initialVelocity, lastMotion, sizeOverThree, PRE);
                        }
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

    private void excuteActionUpMotion(int initialVelocity, float lastMotion, int sizeOverThree, int direction) {
//        if ((Math.abs(initialVelocity) > mMinimumVelocity)
//                || Math.abs(mInitialMotion - lastMotion) >= sizeOverThree)
        if (Math.abs(mInitialMotion - lastMotion) >= sizeOverThree) {
            if (lastMotion > mInitialMotion) {
                setCurrentItemInternal(mCurItem - 1, true, true);
            } else {
                setCurrentItemInternal(mCurItem + 1, true, true);
            }
        } else {
            setCurrentItemInternal(mCurItem,
                    true,
                    true,
                    true,
                    direction,
                    direction == NEXT ? mPagerInfo.nextOffset : mPagerInfo.preOffset);
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            if (mOrientation == HORIZONTAL) {
                mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
            } else {
                mLastMotionY = MotionEventCompat.getY(ev, newPointerIndex);
            }
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
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

    private void setScrollingCacheEnabled(boolean enabled) {
        if (mScrollingCacheEnabled != enabled) {
            mScrollingCacheEnabled = enabled;
            if (USE_CACHE) {
                final int size = getChildCount();
                for (int i = 0; i < size; ++i) {
                    final View child = getChildAt(i);
                    if (child.getVisibility() != GONE) {
                        child.setDrawingCacheEnabled(enabled);
                    }
                }
            }
        }
    }

    public void notifyPageSelected(int position) {
        for (OnPageChangeListener listener : mOnPageChangeListeners) {
            listener.onPageSelected(position);
        }
    }

    public void notifyPageScrolled(int position, float positionOffset, int positionOffsetPixels, int duration, boolean isScrolling) {
        for (OnPageChangeListener listener : mOnPageChangeListeners) {
            listener.onPageScrolled(position, positionOffset, positionOffsetPixels, duration, isScrolling);
        }
    }

    public void notifyPageScrollStateChanged(int state) {
        for (OnPageChangeListener listener : mOnPageChangeListeners) {
            listener.onPageScrollStateChanged(state);
        }
    }

    public interface OnPageChangeListener {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels, int duration, boolean isScrolling);
        void onPageSelected(int position);
        void onPageScrollStateChanged(int state);
    }

}