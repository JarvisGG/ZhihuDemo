package com.jarvis.zhihudemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jarvis.zhihudemo.R;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/12/28 下午3:30
 * @changeRecord [修改记录] <br/>
 */

public class InnerCardView extends CardView {

    private ImageView mCover;
    private View mContainer;

    private float mContainerFinalScaleX = 1.0f;
    private float mContainerFinalScaleY = 1.0f;

    private Context mContext;
    private MaterialRecyclerView.InnerLayoutManager mLayoutManager;
    private ViewDragHelper mDragHelper;
    private GestureDetectorCompat moveDetector;

    private int mContainerLayoutId;

    private int mTouchSlop = 5;

    public InnerCardView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public InnerCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public InnerCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context) {
        this.mContext = context;
        this.setClipChildren(false);
        this.setClipToPadding(false);
        if (mContainerLayoutId != -1) {
            initContainer(mContainerLayoutId);
        }
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mDragHelper = ViewDragHelper
                .create(this, 10f, new DragHelperCallback());
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_TOP);
        moveDetector = new GestureDetectorCompat(context, new MoveDetector());
        moveDetector.setIsLongpressEnabled(false);
        mCover = new ImageView(getContext());
    }

    private void init(Context context, AttributeSet set) {
        TypedArray array = getContext().obtainStyledAttributes(set, R.styleable.InnerCardView);
        this.mContainerLayoutId = array.getResourceId(R.styleable.InnerCardView_container_layout_id, -1);
        array.recycle();
        init(context);
    }

    private void initCover() {
        CardView.LayoutParams params = new LayoutParams(
                (int) (670 / 1.3),
                (int) (804 / 1.3)
        );
        params.gravity = Gravity.CENTER;
        this.addView(mCover, params);
    }

    private void initContainer(int layoutId) {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        View view = inflater.inflate(layoutId, null, false);
        operatorScale(view);
        this.addView(view);
        initCover();
    }

    private void initContainer(View view) {
        CardView.LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        operatorScale(view);
        params.gravity = Gravity.CENTER;
        params.topMargin = mCover.getHeight() / 2;
        this.addView(view, params);
        initCover();
    }

    class MoveDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
            return Math.abs(dy) + Math.abs(dx) > mTouchSlop;
        }
    }

    private void operatorScale(View view) {
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.post(() -> {
            int width = view.getWidth();
            int height = view.getHeight();
            int coverWidth = mCover.getWidth();
            int coverHeight = mCover.getHeight();
            mContainerFinalScaleX = ((float) coverWidth) / ((float) width);
            mContainerFinalScaleY = ((float) coverHeight) / ((float) height);
        });
    }

    public void bindContainer(View viewGroup) {
        this.mContainer = viewGroup;
        initContainer(viewGroup);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean yScroll = moveDetector.onTouchEvent(ev);
        boolean shouldIntercept = false;
        try {
            shouldIntercept = mDragHelper.shouldInterceptTouchEvent(ev);
        } catch (Exception e) {

        }

        int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            mDragHelper.processTouchEvent(ev);
        }

        return shouldIntercept && yScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            mDragHelper.processTouchEvent(event);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    private void operatorContainer() {
        int top = mCover.getTop() - mContainer.getTop() - mCover.getHeight() / 2;
        int totalRangeY = mCover.getHeight() / 3;
        float sizePersent = ((float)top) / ((float)totalRangeY);
        mContainer.setAlpha(sizePersent);
        mContainer.setScaleY(sizePersent*1.1f);
        mContainer.setScaleY(sizePersent*1.1f);
    }

    public void setLayoutManager(MaterialRecyclerView.InnerLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mCover;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int result = 0;
            if (releasedChild == mCover) {
                if (mLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    int finalY = mContainer.getTop() + releasedChild.getHeight() * 5 / 6;
                    int originX = (int) mCover.getLeft();
                    int originY =  mContainer.getTop() + releasedChild.getHeight() / 2;
                    int offset = finalY - releasedChild.getTop();
                    if (offset < 0) {
                        result = finalY;
                    } else if (offset < releasedChild.getHeight() / 4) {
                        result = finalY;
                    } else if (offset < releasedChild.getHeight() / 2) {
                        result = originY;
                    } else {
                        result = originY;
                    }
                    if (mDragHelper.smoothSlideViewTo(mCover, originX, result)) {
//                        ViewCompat.postInvalidateOnAnimation(InnerCardView.this);
                    }
                } else {
                    int finalX = mContainer.getLeft() + releasedChild.getWidth() * 5 / 6;
                    int originX =  mContainer.getLeft() + releasedChild.getWidth() / 2;
                    int originY = mCover.getTop();
                    int offset = finalX - releasedChild.getLeft();
                    if (offset < 0) {
                        result = finalX;
                    } else if (offset < releasedChild.getWidth() / 4) {
                        result = finalX;
                    } else if (offset < releasedChild.getWidth() / 2) {
                        result = originX;
                    } else {
                        result = originX;
                    }
                    if (mDragHelper.smoothSlideViewTo(mCover, result, originY)) {
//                        ViewCompat.postInvalidateOnAnimation(InnerCardView.this);
                    }
                }
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == mCover) {
                operatorContainer();
            }
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }

        @Override
        public boolean onEdgeLock(int edgeFlags) {
            return super.onEdgeLock(edgeFlags);
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            if (mLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                return 100;
            } else {
                return mLayoutManager.getWidth();
            }
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            if (mLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                return 0;
            } else {
                return mLayoutManager.getHeight();
            }
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            final int leftBround = child.getPaddingLeft();
            final int rightBround = mLayoutManager.getWidth() - child.getPaddingRight() - child.getWidth();
            int result = Math.min(Math.max(leftBround, rightBround), left);
            int parentLeftBrand = child.getLeft();
            int dleft = parentLeftBrand - result;
            int totalDx = child.getWidth();
            int level = totalDx / 6;
            return getNewPosOffset(dleft, level);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBround = child.getPaddingTop();
            final int bottomBround = mLayoutManager.getHeight() - child.getPaddingBottom() - child.getHeight();
            int result = Math.min(Math.max(topBround, bottomBround), top);
            int parentTopBrand = child.getTop();
            int dTop = parentTopBrand - result;
            int totalDy = child.getHeight();
            int level = totalDy / 6;
            return getNewPosOffset(dTop, level);
        }

    }

    private int getNewPosOffset(int offset, int level) {
        int result = offset;
        if (offset < level) {
            result = offset / 2;
        } else if (offset < 2 * level) {
            result = offset / 4;
        } else if (offset < 3 * level) {
            result = offset / 8;
        } else if (offset < 4 * level) {
            result = offset / 16;
        } else if (offset < 5 * level) {
            result = offset / 32;
        } else if (offset < 6 * level) {
            result = offset / 64;
        }
        return offset;
    }

    public void bindCover(int res) {
        mCover.setImageResource(res);
    }
}

