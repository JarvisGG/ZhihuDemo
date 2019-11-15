package com.jarvis.zhihudemo.widgets.layoutmanager

import android.support.v7.widget.RecyclerView
import android.util.Log

/**
 * @author yyf
 * @since 11-14-2019
 */
class SlipLayoutManager : RecyclerView.LayoutManager() {

    companion object {
        const val TAG = "SlipLayoutManager"
        const val TOTAL_COUNT = 4
    }

    private var slipCount = TOTAL_COUNT

    private val scaleStep = 0.05f

    private val translateStep = 20

    private var firstVisibleIndex = 0

    private var lastVisibleIndex = 0

    private var childWidth = 0

    private var childHeight = 0

    init {
        isAutoMeasureEnabled = true
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount <= 0 || state.isPreLayout) {
            removeAndRecycleAllViews(recycler)
            return
        }
        fill(recycler)
    }

    private fun fill(recycler: RecyclerView.Recycler) {
        val ifs = createViewIfs(recycler)
        detachAndScrapAttachedViews(recycler)
        layoutChildView(recycler, ifs)
    }

    private fun createViewIfs(recycler: RecyclerView.Recycler) : ArrayList<ViewInfo> {
        if (childWidth == 0) {
            val tempView = recycler.getViewForPosition(0)
            measureChildWithMargins(tempView, 0, 0)
            childWidth = getDecoratedMeasuredWidth(tempView)
            childHeight = getDecoratedMeasuredHeight(tempView)
        }

        val ifs = arrayListOf<ViewInfo>()

        firstVisibleIndex = itemCount - TOTAL_COUNT - 1
        if (firstVisibleIndex < 0) {
            firstVisibleIndex = 0
        }
        lastVisibleIndex = itemCount - 1

        slipCount = lastVisibleIndex - firstVisibleIndex

        var slipNum = 0
        for (index in firstVisibleIndex..lastVisibleIndex) {
            var realNum = if (slipNum == 0 && slipCount == TOTAL_COUNT) 1 else slipNum
            val info = ViewInfo()
            info.scale = 1f - (slipCount - realNum) * scaleStep

            if (slipCount < TOTAL_COUNT) {
                realNum++
            }

            val temp = if (TOTAL_COUNT - slipCount - 1 <= 0) 0 else TOTAL_COUNT - slipCount - 1
            info.translate = realNum * translateStep + (temp) * translateStep
            ifs.add(info)

            slipNum ++
        }

        return ifs
    }

    private fun layoutChildView(recycler: RecyclerView.Recycler, ifs : ArrayList<ViewInfo>) {

        var ifsIndex = 0
        for (index in firstVisibleIndex..lastVisibleIndex) {
            val child = recycler.getViewForPosition(index)
            addView(child)
            measureChildWithMargins(child, 0, 0)
            child.pivotX = childWidth / 2.toFloat()
            child.pivotY = 0f
            child.scaleX = ifs[ifsIndex].scale
            child.scaleY = ifs[ifsIndex].scale
            child.translationY = 0f
            child.rotation = 0f

            val containerTotalHeight = childHeight + translateStep * (TOTAL_COUNT - 1)
            val containerTotalWidth = childWidth
            val leftOffset = (width - containerTotalWidth) / 2
            val topOffset = (height - containerTotalHeight) / 2

            val l = paddingLeft + leftOffset
            val t = paddingTop + ifs[ifsIndex].translate + topOffset
            val r = l + childWidth
            val b = t + childHeight

            layoutDecoratedWithMargins(child, l, t, r, b)

            Log.e(TAG, "rotation : ${child.rotation}")

            ifsIndex++
        }

    }

    data class ViewInfo(var scale : Float = 1f, var translate : Int = 0)



}