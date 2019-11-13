package com.jarvis.zhihudemo.widgets.layoutmanager

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View

/**
 * @author yyf
 * @since 11-13-2019
 */
class PanelCardLayoutManager : RecyclerView.LayoutManager() {


    companion object {
        const val TAG = "PanelCardLayoutManager"
    }


    private var verticalOffset: Long = 0

    private var normalMarginTop = 0f

    private var firstVisibleIndex: Int = 0

    private var lastVisibleIndex: Int = 0

    private var childHeight: Int = -1




    private val scaleStep = 0.1f

    private val stackLengthStep = 20

    private val stackCount = 4



    init {
        isAutoMeasureEnabled = true
    }

    override fun generateDefaultLayoutParams() = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (state.itemCount == 0) {
            removeAndRecycleAllViews(recycler)
            return
        }

        detachAndScrapAttachedViews(recycler)

        fill(recycler)
    }

    private fun fill(recycler: RecyclerView.Recycler) {
        fillVertical(recycler, 0)
        recycleChildren(recycler)
    }

    private fun fillVertical(recycler: RecyclerView.Recycler, delta : Int) : Int {
        detachAndScrapAttachedViews(recycler)

        val dy = delta

        if (childHeight == -1) {
            val tempPosition = firstVisibleIndex
            val tempChild = recycler.getViewForPosition(tempPosition)
            measureChildWithMargins(tempChild, 0, 0)
            childHeight = getDecoratedMeasuredHeight(tempChild)
        }

        firstVisibleIndex = 0
        lastVisibleIndex = stackCount

        var num = 0

        var startY = normalMarginTop

        for (index in firstVisibleIndex..lastVisibleIndex) {
            val child = recycler.getViewForPosition(index)
            addView(child, 0)
            measureChildWithMargins(child, 0, 0)

            val l = paddingLeft
            val t = startY.toInt()
            val r = l + getDecoratedMeasurementHorizontal(child)
            val b = t + getDecoratedMeasurementVertical(child)

            val scale = 1.0f - scaleStep * num
            child.scaleX = scale
            child.scaleY = scale
            val offset = (b - t) * (1 - scale) / 2
            child.translationY = t - stackLengthStep * num - offset

            Log.e(TAG, "translationY : ${child.translationY}")


            layoutDecoratedWithMargins(child, l, t, r, b)

            num++

        }

        return dy
    }

    override fun canScrollVertically(): Boolean {
        return false
    }


    private fun recycleChildren(recycler: RecyclerView.Recycler) {
        val scrapList = recycler.scrapList
        for (i in scrapList.indices) {
            val holder = scrapList[i]
            removeAndRecycleView(holder.itemView, recycler)
        }
    }

    private fun getDecoratedMeasurementHorizontal(view: View): Int {
        val params = view.layoutParams as RecyclerView.LayoutParams
        return (getDecoratedMeasuredWidth(view) + params.leftMargin + params.rightMargin)
    }

    private fun getDecoratedMeasurementVertical(view: View): Int {
        val params = view.layoutParams as RecyclerView.LayoutParams
        return (getDecoratedMeasuredHeight(view) + params.topMargin + params.bottomMargin)
    }

}