package com.jarvis.zhihudemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Interpolator;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.view.widget.ScrollableViewHelper;
import com.jarvis.zhihudemo.view.widget.ViewDragHelper;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/27 上午10:51
 * @changeRecord [修改记录] <br/>
 */

public class SlidingUpPanelLayout extends ViewGroup {

    private static final String TAG = SlidingUpPanelLayout.class.getSimpleName();

    private static final int DEFAULT_PANEL_HEIGHT = 68;

    private static final float DEFAULT_ANCHOR_POINT = 1.0f;

    private static PanelState DEFAULT_SLIDE_STATE = PanelState.COLLAPSED;

    private static final int DEFAULT_SHADOW_HEIGHT = 4;

    private static final int DEFAULT_PANEL_TOP_LIMIT = 0;

    private static final int DEFAULT_FADE_COLOR = 0x99000000;

    private static final int DEFAULT_MIN_FLING_VELOCITY = 400;
    private static final boolean DEFAULT_OVERLAY_FLAG = false;
    private static final boolean DEFAULT_CLIP_PANEL_FLAG = true;
    private static final int[] DEFAULT_ATTRS = new int[]{
            android.R.attr.gravity
    };
    public static final String SLIDING_STATE = "sliding_state";

    private int mMinFlingVelocity = DEFAULT_MIN_FLING_VELOCITY;

    private int mCoveredFadeColor = DEFAULT_FADE_COLOR;

    private int mPanelTopLimit = DEFAULT_PANEL_TOP_LIMIT;

    private static final int DEFAULT_PARALLAX_OFFSET = 0;

    private final Paint mCoveredFadePaint = new Paint();

    private final Drawable mShadowDrawable;

    private int mPanelHeight = -1;

    private int mShadowHeight = -1;

    private int mParallaxOffset = -1;

    private boolean mIsSlidingUp;

    private boolean mOverlayContent = DEFAULT_OVERLAY_FLAG;

    private boolean mClipPanel = DEFAULT_CLIP_PANEL_FLAG;

    private View mDragView;

    private int mDragViewResId = -1;

    private View mScrollableView;
    private int mScrollableViewResId;
    private ScrollableViewHelper mScrollableViewHelper = new ScrollableViewHelper();

    private View mSlideableView;

    private View mMainView;

    public enum PanelState {
        EXPANDED,
        COLLAPSED,
        ANCHORED,
        HIDDEN,
        DRAGGING
    }

    private PanelState mSlideState = DEFAULT_SLIDE_STATE;

    private PanelState mLastNotDraggingSlideState = DEFAULT_SLIDE_STATE;

    private float mSlideOffset;

    private int mSlideRange;

    private float mAnchorPoint = 1.f;

    private boolean mIsUnableToDrag;

    private boolean mIsTouchEnabled;

    private float mPrevMotionX;
    private float mPrevMotionY;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private boolean mIsScrollableViewHandlingTouch = false;

    private final List<PanelSlideListener> mPanelSlideListeners = new CopyOnWriteArrayList<>();
    private View.OnClickListener mFadeOnClickListener;

    private final ViewDragHelper mDragHelper;

    private boolean mFirstLayout = true;

    private final Rect mTmpRect = new Rect();

    public interface PanelSlideListener {
        public void onPanelSlide(View panel, float slideOffset);

        public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState);

        public void onPanelLayerColor(int color);
    }

    public static class SimplePanelSlideListener implements PanelSlideListener {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
        }

        @Override
        public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
        }

        @Override
        public void onPanelLayerColor(int color) {

        }
    }

    public SlidingUpPanelLayout(Context context) {
        this(context, null);
    }

    public SlidingUpPanelLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingUpPanelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (isInEditMode()) {
            mShadowDrawable = null;
            mDragHelper = null;
            return;
        }

        android.view.animation.Interpolator scrollerInterpolator = null;
        if (attrs != null) {
            TypedArray defAttrs = context.obtainStyledAttributes(attrs, DEFAULT_ATTRS);

            if (defAttrs != null) {
                int gravity = defAttrs.getInt(0, Gravity.NO_GRAVITY);
                setGravity(gravity);
                defAttrs.recycle();
            }


            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingUpPanelLayout);

            if (ta != null) {
                mPanelHeight = ta.getDimensionPixelSize(R.styleable.SlidingUpPanelLayout_zhPanelHeight, -1);
                mShadowHeight = ta.getDimensionPixelSize(R.styleable.SlidingUpPanelLayout_zhShadowHeight, -1);
                mParallaxOffset = ta.getDimensionPixelSize(R.styleable.SlidingUpPanelLayout_zhParallaxOffset, -1);

                mMinFlingVelocity = ta.getInt(R.styleable.SlidingUpPanelLayout_zhFlingVelocity, DEFAULT_MIN_FLING_VELOCITY);
                mCoveredFadeColor = ta.getColor(R.styleable.SlidingUpPanelLayout_zhFadeColor, DEFAULT_FADE_COLOR);

                mDragViewResId = ta.getResourceId(R.styleable.SlidingUpPanelLayout_zhDragView, -1);
                mScrollableViewResId = ta.getResourceId(R.styleable.SlidingUpPanelLayout_zhScrollableView, -1);

                mOverlayContent = ta.getBoolean(R.styleable.SlidingUpPanelLayout_zhOverlay, DEFAULT_OVERLAY_FLAG);
                mClipPanel = ta.getBoolean(R.styleable.SlidingUpPanelLayout_zhClipPanel, DEFAULT_CLIP_PANEL_FLAG);

                mAnchorPoint = ta.getFloat(R.styleable.SlidingUpPanelLayout_zhAnchorPoint, DEFAULT_ANCHOR_POINT);

                mPanelTopLimit = ta.getDimensionPixelOffset(R.styleable.SlidingUpPanelLayout_zhPanelTopLimit, DEFAULT_PANEL_TOP_LIMIT);

                mSlideState = PanelState.values()[ta.getInt(R.styleable.SlidingUpPanelLayout_zhInitialState, DEFAULT_SLIDE_STATE.ordinal())];

                int interpolatorResId = ta.getResourceId(R.styleable.SlidingUpPanelLayout_zhScrollInterpolator, -1);
                if (interpolatorResId != -1) {
                    scrollerInterpolator = AnimationUtils.loadInterpolator(context, interpolatorResId);
                }
                ta.recycle();
            }
        }

        final float density = context.getResources().getDisplayMetrics().density;
        if (mPanelHeight == -1) {
            mPanelHeight = (int) (DEFAULT_PANEL_HEIGHT * density + 0.5f);
        }
        if (mShadowHeight == -1) {
            mShadowHeight = (int) (DEFAULT_SHADOW_HEIGHT * density + 0.5f);
        }
        if (mParallaxOffset == -1) {
            mParallaxOffset = (int) (DEFAULT_PARALLAX_OFFSET * density);
        }
        if (mShadowHeight > 0) {
            if (mIsSlidingUp) {
                mShadowDrawable = getResources().getDrawable(R.drawable.above_shadow);
            } else {
                mShadowDrawable = getResources().getDrawable(R.drawable.below_shadow);
            }
        } else {
            mShadowDrawable = null;
        }

        setWillNotDraw(false);

        mDragHelper = ViewDragHelper.create(this, 0.5f, new OvershootInterpolator(0.8f), new DragHelperCallback());
        mDragHelper.setMinVelocity(mMinFlingVelocity * density);

        mIsTouchEnabled = true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mDragViewResId != -1) {
            setDragView(findViewById(mDragViewResId));
        }
        if (mScrollableViewResId != -1) {
            setScrollableView(findViewById(mScrollableViewResId));
        }
    }

    public void setGravity(int gravity) {
        if (gravity != Gravity.TOP && gravity != Gravity.BOTTOM) {
            throw new IllegalArgumentException("gravity must be set to either top or bottom");
        }
        mIsSlidingUp = gravity == Gravity.BOTTOM;
        if (!mFirstLayout) {
            requestLayout();
        }
    }

    public void setCoveredFadeColor(int color) {
        mCoveredFadeColor = color;
        requestLayout();
    }

    public int getCoveredFadeColor() {
        return mCoveredFadeColor;
    }

    public void setTouchEnabled(boolean enabled) {
        mIsTouchEnabled = enabled;
    }

    public boolean isTouchEnabled() {
        return mIsTouchEnabled && mSlideableView != null && mSlideState != PanelState.HIDDEN;
    }

    public void setPanelHeight(int val) {
        if (getPanelHeight() == val) {
            return;
        }

        mPanelHeight = val;
        if (!mFirstLayout) {
            requestLayout();
        }

        if (getPanelState() == PanelState.COLLAPSED) {
            smoothToBottom();
            invalidate();
            return;
        }
    }

    protected void smoothToBottom() {
        smoothSlideTo(0, 0);
    }

    public int getShadowHeight() {
        return mShadowHeight;
    }

    public void setShadowHeight(int val) {
        mShadowHeight = val;
        if (!mFirstLayout) {
            invalidate();
        }
    }

    public int getPanelHeight() {
        return mPanelHeight;
    }

    public int getCurrentParallaxOffset() {
        // Clamp slide offset at zero for parallax computation;
        int offset = (int) (mParallaxOffset * Math.max(mSlideOffset, 0));
        return mIsSlidingUp ? -offset : offset;
    }

    public void setParallaxOffset(int val) {
        mParallaxOffset = val;
        if (!mFirstLayout) {
            requestLayout();
        }
    }

    public int getMinFlingVelocity() {
        return mMinFlingVelocity;
    }

    public void setMinFlingVelocity(int val) {
        mMinFlingVelocity = val;
    }

    public void addPanelSlideListener(PanelSlideListener listener) {
        synchronized (mPanelSlideListeners) {
            mPanelSlideListeners.add(listener);
        }
    }

    public void removePanelSlideListener(PanelSlideListener listener) {
        synchronized (mPanelSlideListeners) {
            mPanelSlideListeners.remove(listener);
        }
    }

    public void setFadeOnClickListener(View.OnClickListener listener) {
        mFadeOnClickListener = listener;
    }

    public void setDragView(View dragView) {
        if (mDragView != null) {
            mDragView.setOnClickListener(null);
        }
        mDragView = dragView;
        if (mDragView != null) {
            mDragView.setClickable(true);
            mDragView.setFocusable(false);
            mDragView.setFocusableInTouchMode(false);
            mDragView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEnabled() || !isTouchEnabled()) return;
                    if (mSlideState != PanelState.EXPANDED && mSlideState != PanelState.ANCHORED) {
                        if (mAnchorPoint < 1.0f) {
                            setPanelState(PanelState.ANCHORED);
                        } else {
                            setPanelState(PanelState.EXPANDED);
                        }
                    } else {
                        setPanelState(PanelState.COLLAPSED);
                    }
                }
            });
            ;
        }
    }

    public void setDragView(int dragViewResId) {
        mDragViewResId = dragViewResId;
        setDragView(findViewById(dragViewResId));
    }

    public void setScrollableView(View scrollableView) {
        mScrollableView = scrollableView;
    }

    public void setScrollableViewHelper(ScrollableViewHelper helper) {
        mScrollableViewHelper = helper;
    }

    public void setAnchorPoint(float anchorPoint) {
        if (anchorPoint > 0 && anchorPoint <= 1) {
            mAnchorPoint = anchorPoint;
            mFirstLayout = true;
            requestLayout();
        }
    }

    public float getAnchorPoint() {
        return mAnchorPoint;
    }

    public void setOverlayed(boolean overlayed) {
        mOverlayContent = overlayed;
    }

    public boolean isOverlayed() {
        return mOverlayContent;
    }

    public void setClipPanel(boolean clip) {
        mClipPanel = clip;
    }

    public boolean isClipPanel() {
        return mClipPanel;
    }


    void dispatchOnPanelSlide(View panel) {
        synchronized (mPanelSlideListeners) {
            for (PanelSlideListener l : mPanelSlideListeners) {
                l.onPanelSlide(panel, mSlideOffset);
            }
        }
    }

    void dispathOnPanelLayerColor(int color) {
        for (PanelSlideListener l : mPanelSlideListeners) {
            l.onPanelLayerColor(color);
        }
    }


    void dispatchOnPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
        synchronized (mPanelSlideListeners) {
            for (PanelSlideListener l : mPanelSlideListeners) {
                l.onPanelStateChanged(panel, previousState, newState);
            }
        }
        sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
    }

    void updateObscuredViewVisibility() {
        if (getChildCount() == 0) {
            return;
        }
        final int leftBound = getPaddingLeft();
        final int rightBound = getWidth() - getPaddingRight();
        final int topBound = getPaddingTop();
        final int bottomBound = getHeight() - getPaddingBottom();
        final int left;
        final int right;
        final int top;
        final int bottom;
        if (mSlideableView != null && hasOpaqueBackground(mSlideableView)) {
            left = mSlideableView.getLeft();
            right = mSlideableView.getRight();
            top = mSlideableView.getTop();
            bottom = mSlideableView.getBottom();
        } else {
            left = right = top = bottom = 0;
        }
        View child = getChildAt(0);
        final int clampedChildLeft = Math.max(leftBound, child.getLeft());
        final int clampedChildTop = Math.max(topBound, child.getTop());
        final int clampedChildRight = Math.min(rightBound, child.getRight());
        final int clampedChildBottom = Math.min(bottomBound, child.getBottom());
        final int vis;
        if (clampedChildLeft >= left && clampedChildTop >= top &&
                clampedChildRight <= right && clampedChildBottom <= bottom) {
            vis = INVISIBLE;
        } else {
            vis = VISIBLE;
        }
        child.setVisibility(vis);
    }

    void setAllChildrenVisible() {
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == INVISIBLE) {
                child.setVisibility(VISIBLE);
            }
        }
    }

    private static boolean hasOpaqueBackground(View v) {
        final Drawable bg = v.getBackground();
        return bg != null && bg.getOpacity() == PixelFormat.OPAQUE;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY && widthMode != MeasureSpec.AT_MOST) {
            throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
        } else if (heightMode != MeasureSpec.EXACTLY && heightMode != MeasureSpec.AT_MOST) {
            throw new IllegalStateException("Height must have an exact value or MATCH_PARENT");
        }

        final int childCount = getChildCount();

        if (childCount != 2) {
            throw new IllegalStateException("Sliding up panel layout must have exactly 2 children!");
        }

        mMainView = getChildAt(0);
        mSlideableView = getChildAt(1);
        if (mDragView == null) {
            setDragView(mSlideableView);
        }

        // If the sliding panel is not visible, then put the whole view in the hidden state
        if (mSlideableView.getVisibility() != VISIBLE) {
            mSlideState = PanelState.HIDDEN;
        }

        int layoutHeight = heightSize - getPaddingTop() - getPaddingBottom();
        int layoutWidth = widthSize - getPaddingLeft() - getPaddingRight();

        // First pass. Measure based on child LayoutParams width/height.
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            // We always measure the sliding panel in order to know it's height (needed for show panel)
            if (child.getVisibility() == GONE && i == 0) {
                continue;
            }

            int height = layoutHeight;
            int width = layoutWidth;
            if (child == mMainView) {
                if (!mOverlayContent && mSlideState != PanelState.HIDDEN) {
                    height -= mPanelHeight;
                }

                width -= lp.leftMargin + lp.rightMargin;
            } else if (child == mSlideableView) {
                // The slideable view should be aware of its top margin.
                // See https://github.com/umano/AndroidSlidingUpPanel/issues/412.
                height -= lp.topMargin;
            }

            int childWidthSpec;
            if (lp.width == LayoutParams.WRAP_CONTENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
            } else if (lp.width == LayoutParams.MATCH_PARENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            } else {
                childWidthSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
            }

            int childHeightSpec;
            if (lp.height == LayoutParams.WRAP_CONTENT) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
            } else {
                // Modify the height based on the weight.
                if (lp.weight > 0 && lp.weight < 1) {
                    height = (int) (height * lp.weight);
                } else if (lp.height != LayoutParams.MATCH_PARENT) {
                    height = lp.height;
                }
                childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            }

            child.measure(childWidthSpec, childHeightSpec);

            if (child == mSlideableView) {
                mSlideRange = mSlideableView.getMeasuredHeight() - mPanelHeight;
            }
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();

        final int childCount = getChildCount();

        if (mFirstLayout) {
            switch (mSlideState) {
                case EXPANDED:
                    mSlideOffset = 1.0f;
                    break;
                case ANCHORED:
                    mSlideOffset = mAnchorPoint;
                    break;
                case HIDDEN:
                    int newTop = computePanelTopPosition(0.0f) + (mIsSlidingUp ? +mPanelHeight : -mPanelHeight);
                    mSlideOffset = computeSlideOffset(newTop);
                    break;
                default:
                    mSlideOffset = 0.f;
                    break;
            }
        }

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            // Always layout the sliding view on the first layout
            if (child.getVisibility() == GONE && (i == 0 || mFirstLayout)) {
                continue;
            }

            final int childHeight = child.getMeasuredHeight();
            int childTop = paddingTop;

            if (child == mSlideableView) {
                childTop = computePanelTopPosition(mSlideOffset);
            }

            if (!mIsSlidingUp) {
                if (child == mMainView && !mOverlayContent) {
                    childTop = computePanelTopPosition(mSlideOffset) + mSlideableView.getMeasuredHeight();
                }
            }
            final int childBottom = childTop + childHeight;
            final int childLeft = paddingLeft + lp.leftMargin;
            final int childRight = childLeft + child.getMeasuredWidth();

            child.layout(childLeft, childTop, childRight, childBottom);
        }

        if (mFirstLayout) {
            updateObscuredViewVisibility();
        }
        applyParallaxForCurrentSlideOffset();

        mFirstLayout = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Recalculate sliding panes and their details
        if (h != oldh) {
            mFirstLayout = true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mIsScrollableViewHandlingTouch || !isTouchEnabled()) {
            mDragHelper.abort();
            return false;
        }

        final int action = ev.getActionMasked();
        final float x = ev.getX();
        final float y = ev.getY();
        final float adx = Math.abs(x - mInitialMotionX);
        final float ady = Math.abs(y - mInitialMotionY);
        final int dragSlop = mDragHelper.getTouchSlop();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mIsUnableToDrag = false;
                mInitialMotionX = x;
                mInitialMotionY = y;
                if (!isViewUnder(mDragView, (int) x, (int) y)) {
                    mDragHelper.cancel();
                    mIsUnableToDrag = true;
                    return false;
                }

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (ady > dragSlop && adx > ady) {
                    mDragHelper.cancel();
                    mIsUnableToDrag = true;
                    return false;
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // If the dragView is still dragging when we get here, we need to call processTouchEvent
                // so that the view is settled
                // Added to make scrollable views work (tokudu)
                if (mDragHelper.isDragging()) {
                    mDragHelper.processTouchEvent(ev);
                    return true;
                }
                // Check if this was a click on the faded part of the screen, and fire off the listener if there is one.
                if (ady <= dragSlop
                        && adx <= dragSlop
                        && mSlideOffset > 0 && !isViewUnder(mSlideableView, (int) mInitialMotionX, (int) mInitialMotionY) && mFadeOnClickListener != null) {
                    playSoundEffect(android.view.SoundEffectConstants.CLICK);
                    mFadeOnClickListener.onClick(this);
                    return true;
                }
                break;

            default:
                break;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isEnabled() || !isTouchEnabled()) {
            return super.onTouchEvent(ev);
        }
        try {
            mDragHelper.processTouchEvent(ev);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();

        if (!isEnabled() || !isTouchEnabled() || (mIsUnableToDrag && action != MotionEvent.ACTION_DOWN)) {
            mDragHelper.abort();
            return super.dispatchTouchEvent(ev);
        }

        final float x = ev.getX();
        final float y = ev.getY();

        if (action == MotionEvent.ACTION_DOWN) {
            mIsScrollableViewHandlingTouch = false;
            mPrevMotionX = x;
            mPrevMotionY = y;
        } else if (action == MotionEvent.ACTION_MOVE) {
            float dx = x - mPrevMotionX;
            float dy = y - mPrevMotionY;
            mPrevMotionX = x;
            mPrevMotionY = y;

            if (Math.abs(dx) > Math.abs(dy)) {
                return super.dispatchTouchEvent(ev);
            }

            if (!isViewUnder(mScrollableView, (int) mInitialMotionX, (int) mInitialMotionY)) {
                return super.dispatchTouchEvent(ev);
            }

            if (dy * (mIsSlidingUp ? 1 : -1) > 0) {
                if (mScrollableViewHelper.getScrollableViewScrollPosition(mScrollableView, mIsSlidingUp) > 0) {
                    mIsScrollableViewHandlingTouch = true;
                    return super.dispatchTouchEvent(ev);
                }

                if (mIsScrollableViewHandlingTouch) {
                    // Send an 'UP' event to the child.
                    MotionEvent up = MotionEvent.obtain(ev);
                    up.setAction(MotionEvent.ACTION_CANCEL);
                    super.dispatchTouchEvent(up);
                    up.recycle();

                    ev.setAction(MotionEvent.ACTION_DOWN);
                }

                mIsScrollableViewHandlingTouch = false;
                return this.onTouchEvent(ev);
            } else if (dy * (mIsSlidingUp ? 1 : -1) < 0) {
                if (mSlideOffset < 1.0f) {
                    mIsScrollableViewHandlingTouch = false;
                    return this.onTouchEvent(ev);
                }

                if (!mIsScrollableViewHandlingTouch && mDragHelper.isDragging()) {
                    mDragHelper.cancel();
                    ev.setAction(MotionEvent.ACTION_DOWN);
                }

                mIsScrollableViewHandlingTouch = true;
                return super.dispatchTouchEvent(ev);
            }
        } else if (action == MotionEvent.ACTION_UP) {
            if (mIsScrollableViewHandlingTouch) {
                mDragHelper.setDragState(ViewDragHelper.STATE_IDLE);
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private boolean isViewUnder(View view, int x, int y) {
        if (view == null) {
            return false;
        }
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0] && screenX < viewLocation[0] + view.getWidth() &&
                screenY >= viewLocation[1] && screenY < viewLocation[1] + view.getHeight();
    }

    private int computePanelTopPosition(float slideOffset) {

        int slidingViewHeight = mSlideableView != null ? mSlideableView.getMeasuredHeight() : 0;
        int slidePixelOffset = (int) (slideOffset * mSlideRange);
        // Compute the top of the panel if its collapsed
        return mIsSlidingUp
                ? getMeasuredHeight() - getPaddingBottom() - mPanelHeight - slidePixelOffset
                : getPaddingTop() - slidingViewHeight + mPanelHeight + slidePixelOffset;
    }

    private float computeSlideOffset(int topPosition) {
        // Compute the panel top position if the panel is collapsed (offset 0)
        final int topBoundCollapsed = computePanelTopPosition(0);

        // Determine the new slide offset based on the collapsed top position and the new required
        // top position
        return (mIsSlidingUp
                ? (float) (topBoundCollapsed - topPosition) / mSlideRange
                : (float) (topPosition - topBoundCollapsed) / mSlideRange);
    }

    public PanelState getPanelState() {
        return mSlideState;
    }

    public void setPanelState(PanelState state) {

        // Abort any running animation, to allow state change
        if(mDragHelper.getViewDragState() == ViewDragHelper.STATE_SETTLING){
            Log.d(TAG, "View is settling. Aborting animation.");
            mDragHelper.abort();
        }

        if (state == null || state == PanelState.DRAGGING) {
            throw new IllegalArgumentException("Panel state cannot be null or DRAGGING.");
        }
        if (!isEnabled()
                || (!mFirstLayout && mSlideableView == null)
                || state == mSlideState
                || mSlideState == PanelState.DRAGGING) {
            return;
        }

        if (mFirstLayout) {
            setPanelStateInternal(state);
        } else {
            if (mSlideState == PanelState.HIDDEN) {
                mSlideableView.setVisibility(View.VISIBLE);
                requestLayout();
            }
            switch (state) {
                case ANCHORED:
                    smoothSlideTo(mAnchorPoint, 0);
                    break;
                case COLLAPSED:
                    smoothSlideTo(0, 0);
                    break;
                case EXPANDED:
                    smoothSlideTo(1.0f, 0);
                    break;
                case HIDDEN:
                    int newTop = computePanelTopPosition(0.0f) + (mIsSlidingUp ? +mPanelHeight : -mPanelHeight);
                    smoothSlideTo(computeSlideOffset(newTop), 0);
                    break;
                default:
                    break;
            }
        }
    }

    private void setPanelStateInternal(PanelState state) {
        if (mSlideState == state) {
            return;
        }
        PanelState oldState = mSlideState;
        mSlideState = state;
        dispatchOnPanelStateChanged(this, oldState, state);
    }

    private void applyParallaxForCurrentSlideOffset() {
        if (mParallaxOffset > 0) {
            int mainViewOffset = getCurrentParallaxOffset();
            mMainView.setTranslationY(mainViewOffset);
        }
    }

    private void onPanelDragged(int newTop) {
        if (mSlideState != PanelState.DRAGGING) {
            mLastNotDraggingSlideState = mSlideState;
        }
        setPanelStateInternal(PanelState.DRAGGING);
        mSlideOffset = computeSlideOffset(newTop);
        applyParallaxForCurrentSlideOffset();
        dispatchOnPanelSlide(mSlideableView);
        LayoutParams lp = (LayoutParams) mMainView.getLayoutParams();
        int defaultHeight = getHeight() - getPaddingBottom() - getPaddingTop() - mPanelHeight;

        if (mSlideOffset <= 0 && !mOverlayContent) {
            lp.height = mIsSlidingUp ? (newTop - getPaddingBottom()) : (getHeight() - getPaddingBottom() - mSlideableView.getMeasuredHeight() - newTop);
            if (lp.height == defaultHeight) {
                lp.height = LayoutParams.MATCH_PARENT;
            }
            mMainView.requestLayout();
        } else if (lp.height != LayoutParams.MATCH_PARENT && !mOverlayContent) {
            lp.height = LayoutParams.MATCH_PARENT;
            mMainView.requestLayout();
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
//        boolean result;
//        final int save = canvas.save(Canvas.CLIP_SAVE_FLAG);
//
//        if (mSlideableView != null && mSlideableView != child) {
//            canvas.getClipBounds(mTmpRect);
//            if (!mOverlayContent) {
//                if (mIsSlidingUp) {
//                    mTmpRect.bottom = Math.min(mTmpRect.bottom, mSlideableView.getTop());
//                } else {
//                    mTmpRect.top = Math.max(mTmpRect.top, mSlideableView.getBottom());
//                }
//            }
//            if (mClipPanel) {
//                canvas.clipRect(mTmpRect);
//            }
//
//            result = super.drawChild(canvas, child, drawingTime);
//
//            if (mCoveredFadeColor != 0 && mSlideOffset > 0) {
//                final int baseAlpha = (mCoveredFadeColor & 0xff000000) >>> 24;
//                final int imag = (int) (baseAlpha * mSlideOffset);
//                final int color = imag << 24 | (mCoveredFadeColor & 0xffffff);
//                mCoveredFadePaint.setColor(color);
//                canvas.drawRect(mTmpRect, mCoveredFadePaint);
//                dispathOnPanelLayerColor(color);
//            }
//        } else {
//            result = super.drawChild(canvas, child, drawingTime);
//        }
//
//        canvas.restoreToCount(save);
//
//        return result;
        return super.drawChild(canvas, child, drawingTime);
    }

    boolean smoothSlideTo(float slideOffset, int velocity) {
        if (!isEnabled() || mSlideableView == null) {
            // Nothing to do.
            return false;
        }

        int panelTop = computePanelTopPosition(slideOffset);

        if (mDragHelper.smoothSlideViewTo(mSlideableView, mSlideableView.getLeft(), panelTop)) {
            setAllChildrenVisible();
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper != null && mDragHelper.continueSettling(true)) {
            if (!isEnabled()) {
                mDragHelper.abort();
                return;
            }

            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void draw(Canvas c) {
        super.draw(c);

        // draw the shadow
//        if (mShadowDrawable != null && mSlideableView != null) {
//            final int right = mSlideableView.getRight();
//            final int top;
//            final int bottom;
//            if (mIsSlidingUp) {
//                top = mSlideableView.getTop() - mShadowHeight;
//                bottom = mSlideableView.getTop();
//            } else {
//                top = mSlideableView.getBottom();
//                bottom = mSlideableView.getBottom() + mShadowHeight;
//            }
//            final int left = mSlideableView.getLeft();
//            mShadowDrawable.setBounds(left, top, right, bottom);
//            mShadowDrawable.draw(c);
//        }
    }

    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();
            final int count = group.getChildCount();
            // Count backwards - let topmost views consume scroll distance first.
            for (int i = count - 1; i >= 0; i--) {
                final View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() &&
                        y + scrollY >= child.getTop() && y + scrollY < child.getBottom() &&
                        canScroll(child, true, dx, x + scrollX - child.getLeft(),
                                y + scrollY - child.getTop())) {
                    return true;
                }
            }
        }
        return checkV && ViewCompat.canScrollHorizontally(v, -dx);
    }


    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof MarginLayoutParams
                ? new LayoutParams((MarginLayoutParams) p)
                : new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putSerializable(SLIDING_STATE, mSlideState != PanelState.DRAGGING ? mSlideState : mLastNotDraggingSlideState);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mSlideState = (PanelState) bundle.getSerializable(SLIDING_STATE);
            mSlideState = mSlideState == null ? DEFAULT_SLIDE_STATE : mSlideState;
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return !mIsUnableToDrag && child == mSlideableView;

        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (mDragHelper != null && mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE) {
                mSlideOffset = computeSlideOffset(mSlideableView.getTop());
                applyParallaxForCurrentSlideOffset();

                if (mSlideOffset == 1) {
                    updateObscuredViewVisibility();
                    setPanelStateInternal(PanelState.EXPANDED);
                } else if (mSlideOffset == 0) {
                    setPanelStateInternal(PanelState.COLLAPSED);
                } else if (mSlideOffset < 0) {
                    setPanelStateInternal(PanelState.HIDDEN);
                    mSlideableView.setVisibility(View.INVISIBLE);
                } else {
                    updateObscuredViewVisibility();
                    setPanelStateInternal(PanelState.ANCHORED);
                }
            }
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            setAllChildrenVisible();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            onPanelDragged(top);
//            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int target = 0;

            // direction is always positive if we are sliding in the expanded direction
            float direction = mIsSlidingUp ? -yvel : yvel;

            if (direction > 0 && mSlideOffset <= mAnchorPoint) {
                // swipe up -> expand and stop at anchor point
                target = computePanelTopPosition(mAnchorPoint);
            } else if (direction > 0 && mSlideOffset > mAnchorPoint) {
                // swipe up past anchor -> expand
                target = computePanelTopPosition(1.0f);
            } else if (direction < 0 && mSlideOffset >= mAnchorPoint) {
                // swipe down -> collapse and stop at anchor point
                target = computePanelTopPosition(mAnchorPoint);
            } else if (direction < 0 && mSlideOffset < mAnchorPoint) {
                // swipe down past anchor -> collapse
                target = computePanelTopPosition(0.0f);
            } else if (mSlideOffset >= (1.f + mAnchorPoint) / 2) {
                // zero velocity, and far enough from anchor point => expand to the top
                target = computePanelTopPosition(1.0f);
            } else if (mSlideOffset >= mAnchorPoint / 3 * 2) {
                // zero velocity, and close enough to anchor point => go to anchor
                target = computePanelTopPosition(mAnchorPoint);
            } else {
                // settle at the bottom
                target = computePanelTopPosition(0.0f);
            }

            if (mDragHelper != null) {
                mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), target);
            }
            invalidate();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mSlideRange;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int collapsedTop = computePanelTopPosition(0.f);
            final int expandedTop = computePanelTopPosition(1.0f);
            if (mIsSlidingUp) {
                return Math.min(Math.max(top, expandedTop), collapsedTop);
            } else {
                return Math.min(Math.max(top, collapsedTop), expandedTop);
            }
        }
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        private static final int[] ATTRS = new int[]{
                android.R.attr.layout_weight
        };

        public float weight = 0;

        public LayoutParams() {
            super(MATCH_PARENT, MATCH_PARENT);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height);
            this.weight = weight;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            final TypedArray ta = c.obtainStyledAttributes(attrs, ATTRS);
            if (ta != null) {
                this.weight = ta.getFloat(0, 0);
                ta.recycle();
            }


        }
    }
}