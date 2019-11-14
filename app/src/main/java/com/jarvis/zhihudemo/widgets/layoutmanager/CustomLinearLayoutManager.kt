package com.jarvis.zhihudemo.widgets.layoutmanager

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.*
import android.view.View
import kotlin.math.floor


class CustomLinearLayoutManager : RecyclerView.LayoutManager() {

    init {
        isAutoMeasureEnabled = true
    }

    private var onceCompleteScrollLength = -1f

    private var mFirstVisiPos: Int = 0

    private var mLastVisiPos: Int = 0

    private var mHorizontalOffset: Long = 0

    private var normalViewGap = 0f

    private var childWidth = 0

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }



    override fun onLayoutChildren(recycler: Recycler, state: State) {
        if (state.itemCount == 0) {
            removeAndRecycleAllViews(recycler)
            return
        }

        onceCompleteScrollLength = -1f
        detachAndScrapAttachedViews(recycler)

        fill(recycler, state, 0)
    }

    private fun fill(recycler: Recycler, state: State, dx: Int): Int {
        val resultDelta = fillHorizontal(recycler, state, dx)
        recycleChildren(recycler)
        return resultDelta
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }


    override fun scrollHorizontallyBy(delta: Int, recycler: Recycler, state: State): Int {
        var dx = delta
        if (dx == 0 || childCount == 0) {
            return 0
        }

        mHorizontalOffset += dx.toLong()
        dx = fill(recycler, state, dx)
        offsetChildrenHorizontal(dx)
        return dx
    }

    private fun getMaxOffset(): Float {
        return (childWidth + normalViewGap) * itemCount - width
    }

    private fun getMinOffset(): Float {
        return 0f
    }

    private fun fillHorizontal(
        recycler: Recycler,
        state: State,
        delta: Int
    ): Int {

        var dx = delta
        if (dx < 0) {
            if (mHorizontalOffset < 0) {
                mHorizontalOffset = 0
                dx = 0
            }
        }

        if (dx > 0) {
            if (mHorizontalOffset >= getMaxOffset()) {
                mHorizontalOffset = getMaxOffset().toLong()
                dx = 0
            }
        }

        detachAndScrapAttachedViews(recycler)

        var startX = normalViewGap

        if (onceCompleteScrollLength == -1f) {
            val tempPosition = mFirstVisiPos
            val tempView = recycler.getViewForPosition(tempPosition)
            measureChildWithMargins(tempView, 0, 0)
            childWidth = getDecoratedMeasurementHorizontal(tempView)
            onceCompleteScrollLength = childWidth + normalViewGap
        }

        mFirstVisiPos = floor((mHorizontalOffset / onceCompleteScrollLength).toDouble()).toInt()
        mLastVisiPos = itemCount - 1

        val fraction = mHorizontalOffset % onceCompleteScrollLength / (onceCompleteScrollLength * 1.0f)
        val normalViewOffset = onceCompleteScrollLength * fraction
        startX -= normalViewOffset

        for (i in mFirstVisiPos..mLastVisiPos) {
            val child = recycler.getViewForPosition(i)
            addView(child)
            measureChildWithMargins(child, 0, 0)

            val l = startX.toInt()
            val t = paddingTop
            val r = l + getDecoratedMeasurementHorizontal(child)
            val b = t + getDecoratedMeasurementVertical(child)

            layoutDecoratedWithMargins(child, l, t, r, b)

            startX += childWidth + normalViewGap

            if (startX > width - paddingRight) {
                mLastVisiPos = i
                break
            }
        }
        return dx
    }

    private fun recycleChildren(recycler: Recycler) {
        val scrapList = recycler.scrapList
        for (i in scrapList.indices) {
            val holder = scrapList[i]
            removeAndRecycleView(holder.itemView, recycler)
        }
    }

    private fun getDecoratedMeasurementHorizontal(view: View): Int {
        val params = view.layoutParams as LayoutParams
        return (getDecoratedMeasuredWidth(view) + params.leftMargin + params.rightMargin)
    }

    private fun getDecoratedMeasurementVertical(view: View): Int {
        val params = view.layoutParams as LayoutParams
        return (getDecoratedMeasuredHeight(view) + params.topMargin + params.bottomMargin)
    }
}