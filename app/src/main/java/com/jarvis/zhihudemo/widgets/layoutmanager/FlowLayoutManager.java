package com.jarvis.zhihudemo.widgets.layoutmanager;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/11 下午3:07
 * @changeRecord [修改记录] <br/>
 */

public class FlowLayoutManager extends RecyclerView.LayoutManager {

    private int mVerticalOffset;
    private int mFirstVisiPos;
    private int mLastVisiPos;

    private SparseArray<Rect> mItemRects;
    private List<LineSpace> mLineSpaces = new ArrayList<>();

    public FlowLayoutManager() {
        setAutoMeasureEnabled(true);
        mItemRects = new SparseArray<>();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }
        detachAndScrapAttachedViews(recycler);
        mVerticalOffset = 0;
        mFirstVisiPos = 0;
        mLastVisiPos = getItemCount();
        mLineSpaces.add(new LineSpace(getHorizontalSpace(), 0, 0, 0));
        fill(recycler, state);
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        fill(recycler, state, 0);
    }

    private int fill(RecyclerView.Recycler recycler, RecyclerView.State state, int dy) {
        // 回收
        if (getChildCount() > 0) {
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View child = getChildAt(i);
                if (dy > 0) {
                    if (getDecoratedBottom(child) - dy < getPaddingTop()) {
                        removeAndRecycleView(child, recycler);
                        mFirstVisiPos ++;
                    }
                } else if (dy < 0) {
                    if (getDecoratedTop(child) - dy > getHeight() - getPaddingBottom()) {
                        removeAndRecycleView(child, recycler);
                        mLastVisiPos--;
                    }
                }
            }
        }

        if (dy >= 0) {
            int minPos = mFirstVisiPos;
            mLastVisiPos = getItemCount() - 1;
            if (getChildCount() > 0) {
                View lastView = getChildAt(getChildCount() - 1);
                minPos = getPosition(lastView) + 1;
            }
            for (int i = minPos; i <= mLastVisiPos; i++) {
                View child = recycler.getViewForPosition(i);
                addView(child);
                measureChildWithMargins(child, 0, 0);
                sortSpace();
                if (mLineSpaces.size() > 0) {
                    for (int j = 0; j < mLineSpaces.size(); j++) {
                        LineSpace space = mLineSpaces.get(j);
                        if (space.width >= getDecoratedMeasurementHorizontal(child)) {
                            layoutDecoratedWithMargins(child,
                                    space.left,
                                    space.top,
                                    space.left + getDecoratedMeasurementHorizontal(child),
                                    space.top + getDecoratedMeasurementVertical(child));
                            Rect rect = new Rect(
                                    space.left,
                                    space.top + mVerticalOffset,
                                    space.left + getDecoratedMeasurementHorizontal(child),
                                    space.top + mVerticalOffset + getDecoratedMeasurementVertical(child));
                            mItemRects.put(i, rect);
                            if (space.width - getDecoratedMeasurementHorizontal(child) != 0) {
                                LineSpace step1 = new LineSpace(
                                        space.width - getDecoratedMeasurementHorizontal(child),
                                        space.height,
                                        space.left + getDecoratedMeasurementHorizontal(child),
                                        space.top);
                                mLineSpaces.add(step1);
                            }
                            LineSpace step2 = new LineSpace(
                                    getDecoratedMeasurementHorizontal(child),
                                    getDecoratedMeasurementVertical(child),
                                    space.left,
                                    space.top + getDecoratedMeasurementVertical(child)
                            );
                            mLineSpaces.add(step2);
                            mLineSpaces.remove(space);
                            break;
                        }
                    }
                }
            }

//
//                if (leftOffset + getDecoratedMeasurementHorizontal(child) <= getHorizontalSpace()) {
//                    layoutDecoratedWithMargins(child,
//                            leftOffset,
//                            topOffset,
//                            leftOffset + getDecoratedMeasurementHorizontal(child),
//                            topOffset+getDecoratedMeasurementVertical(child));
//
//                    Rect rect = new Rect(leftOffset,
//                            topOffset + mVerticalOffset,
//                            leftOffset + getDecoratedMeasurementHorizontal(child),
//                            topOffset + mVerticalOffset + getDecoratedMeasurementVertical(child));
//                    mItemRects.put(i, rect);
//                    leftOffset += getDecoratedMeasurementHorizontal(child);
//                    lineMaxHeight = Math.max(lineMaxHeight, getDecoratedMeasurementVertical(child));
//                } else {
//                    leftOffset = getPaddingLeft();
//                    topOffset += lineMaxHeight;
//                    lineMaxHeight = 0;
//                    if (topOffset - dy > getHeight() - getPaddingBottom()) {
//                        removeAndRecycleView(child, recycler);
//                        mLastVisiPos = i - 1;
//                    } else {
//                        layoutDecoratedWithMargins(child,
//                                leftOffset,
//                                topOffset,
//                                leftOffset + getDecoratedMeasurementHorizontal(child),
//                                topOffset + getDecoratedMeasurementVertical(child));
//
//                        Rect rect = new Rect(
//                                leftOffset,
//                                topOffset + mVerticalOffset,
//                                leftOffset + getDecoratedMeasurementHorizontal(child),
//                                topOffset + getDecoratedMeasurementVertical(child) + mVerticalOffset);
//                        mItemRects.put(i, rect);
//
//                        leftOffset += getDecoratedMeasurementHorizontal(child);
//                        lineMaxHeight = Math.max(lineMaxHeight, getDecoratedMeasurementVertical(child));
//                    }
//                }
//            }
//            View lastChild = getChildAt(getChildCount() - 1);
//            if (getPosition(lastChild) == getItemCount() - 1) {
//                int gap = getHeight() - getPaddingBottom() - getDecoratedBottom(lastChild);
//                if (gap > 0) {
//                    dy -= gap;
//                }
//            }
        } else {
            int maxPos = getItemCount() - 1;
            mFirstVisiPos = 0;
            if (getChildCount() > 0) {
                View firstView = getChildAt(0);
                maxPos = getPosition(firstView) - 1;
            }
            for (int i = maxPos; i >= mFirstVisiPos; i--) {
                Rect rect = mItemRects.get(i);

                if (rect.bottom - mVerticalOffset - dy < getPaddingTop()) {
                    mFirstVisiPos = i + 1;
                    break;
                } else {
                    View child = recycler.getViewForPosition(i);
                    addView(child, 0);
                    measureChildWithMargins(child, 0, 0);

                    layoutDecoratedWithMargins(child, rect.left, rect.top - mVerticalOffset, rect.right, rect.bottom - mVerticalOffset);
                }
            }
        }
        return dy;
    }

    public void sortSpace() {
        for (int i = 0; i < mLineSpaces.size(); i ++) {
            for (int j = i + 1; j < mLineSpaces.size(); j++) {
                LineSpace a1 = mLineSpaces.get(i);
                LineSpace a2 = mLineSpaces.get(j);
                if (a1.top > a2.top) {
                    mLineSpaces.set(i, a2);
                    mLineSpaces.set(j, a1);
                } else if (a1.height + a1.top == a2.height + a2.top) {
                    if (a1.left > a2.left) {
                        mLineSpaces.set(i, a2);
                        mLineSpaces.set(j, a1);
                    }
                }
            }
        }
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (dy == 0 || getChildCount() == 0) {
            return 0;
        }

        int realOffset = dy;
        if (mVerticalOffset + realOffset < 0) {
            realOffset = -mVerticalOffset;
        } else if (realOffset > 0) {
            View lastChild = getChildAt(getChildCount() - 1);
            if (getPosition(lastChild) == getItemCount() - 1) {
                int gap = getHeight() - getPaddingBottom() - getDecoratedBottom(lastChild);
                if (gap > 0) {
                    realOffset = -gap;
                } else if (gap == 0) {
                    realOffset = 0;
                } else {
                    realOffset = Math.min(realOffset, -gap);
                }
            }
        }

        realOffset = fill(recycler, state, realOffset);

        mVerticalOffset += realOffset;

        offsetChildrenVertical(-realOffset);

        return realOffset;
    }

    public int getDecoratedMeasurementHorizontal(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin + params.rightMargin;
    }

    public int getDecoratedMeasurementVertical(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin + params.bottomMargin;
    }

    public int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private class LineSpace {
        int height, width;
        int top, left;
        public LineSpace(int width, int height, int left, int top) {
            this.width = width;
            this.height = height;
            this.left = left;
            this.top = top;
        }
    }
}
