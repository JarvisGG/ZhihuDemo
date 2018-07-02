package com.jarvis.zhihudemo.widgets.layoutmanager;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;

import com.jarvis.zhihudemo.widgets.ExplosionUtils;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/11 上午10:12
 * @changeRecord [修改记录] <br/>
 */

public class TestLayoutManager extends RecyclerView.LayoutManager {

    int totalHeight = 0;
    int verticalScrollOffset = 0;
    private Context mContext;

    private SparseArray<Rect> allItemRects = new SparseArray<>();
    private SparseBooleanArray itemStates = new SparseBooleanArray();

    public TestLayoutManager(Context context) {
        this.mContext = context;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }
        detachAndScrapAttachedViews(recycler);
        calculateChildrenSite(recycler);
        recycleAndFillView(recycler, state);
    }

    private void calculateChildrenSite(RecyclerView.Recycler recycler) {
        totalHeight = 0;
        for (int i = 0; i < getItemCount(); i++) {
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view, ExplosionUtils.getScreenSizeX(mContext) / 2, 0);
            int width = getDecoratedMeasuredWidth(view);
            int height = getDecoratedMeasuredHeight(view);

            Rect mTmpRect = allItemRects.get(i);
            if (mTmpRect == null) {
                mTmpRect = new Rect();
            }
            //调用这个方法能够调整ItemView的大小，以除去ItemDecorator。
            // 调用这句我们指定了该View的显示区域，并将View显示上去，此时所有区域都用于显示View，
            //包括ItemDecorator设置的距离。
            if (i % 2 == 0) {
                mTmpRect.set(0, totalHeight, ExplosionUtils.getScreenSizeX(mContext) / 2, totalHeight + height);
            } else {
                mTmpRect.set(ExplosionUtils.getScreenSizeX(mContext) / 2, totalHeight, ExplosionUtils.getScreenSizeX(mContext), totalHeight + height);
                totalHeight += height;
            }
            allItemRects.put(i, mTmpRect);
            itemStates.put(i, false);
        }
    }

    private void recycleAndFillView(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }
        Rect displayRect = new Rect(0, verticalScrollOffset, getHorizontalSpace(), verticalScrollOffset + getVerticalSpace());
        Rect childRect = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            childRect.left = getDecoratedLeft(child);
            childRect.top = getDecoratedTop(child);
            childRect.right = getDecoratedRight(child);
            childRect.bottom = getDecoratedBottom(child);
            if (!Rect.intersects(displayRect, childRect)) {
                removeAndRecycleView(child, recycler);
                itemStates.put(i, false);
            }
        }

        for (int i = 0; i < getItemCount(); i++) {
            if (Rect.intersects(displayRect, allItemRects.get(i))) {
                View itemView = recycler.getViewForPosition(i);
                measureChildWithMargins(itemView, ExplosionUtils.getScreenSizeX(mContext) / 2, 0);
                addView(itemView);
                Rect rect = allItemRects.get(i);
                layoutDecoratedWithMargins(itemView,
                        rect.left,
                        rect.top - verticalScrollOffset,
                        rect.right,
                        rect.bottom - verticalScrollOffset);
                itemStates.put(i, true);
            }
        }
        Log.e("itemViewCount -------->", ""+getChildCount());
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        int travel = dy;
        if (verticalScrollOffset + dy < 0) {
            travel = -verticalScrollOffset;
        } else if (verticalScrollOffset + dy > totalHeight - getVerticalSpace()) {
            travel = totalHeight - getVerticalSpace() - verticalScrollOffset;
        }

        verticalScrollOffset += travel;
        recycleAndFillView(recycler, state);
        offsetChildrenVertical(-travel);

        return travel;
    }

    private int getVerticalSpace() {
        //计算RecyclerView的可用高度，除去上下Padding值
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }
}
