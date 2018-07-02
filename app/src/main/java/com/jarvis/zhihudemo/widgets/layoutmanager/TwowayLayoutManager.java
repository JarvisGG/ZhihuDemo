package com.jarvis.zhihudemo.widgets.layoutmanager;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/11 下午5:20
 * @changeRecord [修改记录] <br/>
 */

public class TwowayLayoutManager extends RecyclerView.LayoutManager {


    private SparseArray<Rect> allItemRects = new SparseArray<>();
    private int mHorizontallyOffset = 0;
    private int mFirstVisiPos;
    private int mLastVisiPos;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }

        detachAndScrapAttachedViews(recycler);
        mFirstVisiPos = 0;
        mLastVisiPos = getItemCount();
        recycleAndFillView(recycler, state, 0);
    }

    private void recycleAndFillView(RecyclerView.Recycler recycler, RecyclerView.State state, int dx) {

        if (getChildCount() > 0) {
            for (int i = getChildCount() - 1; i >= 0; i --) {
                View child = getChildAt(i);
                if (dx > 0) {
                    if (getDecoratedRight(child) - dx < getPaddingLeft()) {
                        removeAndRecycleView(child, recycler);
                        mFirstVisiPos++;
                    }
                } else if (dx < 0) {
                    if (getDecoratedLeft(child) - dx > getWidth() - getPaddingRight()) {
                        removeAndRecycleView(child, recycler);
                        mLastVisiPos --;
                    }
                }
            }
        }

        int leftOffset = getPaddingLeft();
        int topOffset = getPaddingTop();
        int lineMaxHeight = 0;
    }


}
