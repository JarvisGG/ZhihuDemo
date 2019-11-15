package com.jarvis.zhihudemo.widgets.layoutmanager

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import kotlin.math.floor

/**
 * @author yyf
 * @since 11-13-2019
 */
class StackLayoutManager : RecyclerView.LayoutManager() {

    companion object {
        const val TAG = "StackLayoutManager"
    }

    private val stackWidth = 120f

    private val stackCount = 4

    private val scaleStep = 0.1f

    private var normalMargin = 20

    private var childWidth = 0

    private var childHeight = 0

    private var horizontalOffset = 0

    private var onceCompleteScrollLength = -1

    private var firstVisibleIndex = 0

    private var lastVisibleIndex = 0


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
        recycleChildren(recycler)
    }

    private fun fill(recycler: RecyclerView.Recycler) {
        val ifs = createViewIfs(recycler)
        detachAndScrapAttachedViews(recycler)
        layoutChildView(recycler, ifs)
    }
    private fun createViewIfs(recycler: RecyclerView.Recycler) : ArrayList<ViewInfo> {

        if (onceCompleteScrollLength == -1) {
            val tempView = recycler.getViewForPosition(0)
            measureChildWithMargins(tempView, 0, 0)
            childWidth = getDecoratedMeasurementHorizontal(tempView)
            childHeight = getDecoratedMeasurementVertical(tempView)
            onceCompleteScrollLength = childWidth + normalMargin
        }
        val ifs = arrayListOf<ViewInfo>()
        val step = stackWidth / stackCount

        val sleepCard = floor(((horizontalOffset) / onceCompleteScrollLength).toDouble()).toInt()

        firstVisibleIndex = sleepCard
        lastVisibleIndex = itemCount - 1

        val normalOffset = -horizontalOffset % onceCompleteScrollLength
        val stackOffset = normalOffset * step / onceCompleteScrollLength

        var stackNum = 0
        for (index in firstVisibleIndex..lastVisibleIndex) {
            val info = ViewInfo()
            if (stackNum <= stackCount) {
                info.left = (step * stackNum + stackOffset).toInt()
                info.scale = 1f - scaleStep * (stackCount - stackNum) - (-normalOffset.toFloat() / onceCompleteScrollLength) * scaleStep
                Log.e(TAG + stackNum, "scale : ${info.scale}")
            } else {
                info.left = stackWidth.toInt() + onceCompleteScrollLength * (stackNum - stackCount) + normalOffset
                info.scale = 1f
            }
            stackNum ++
            ifs.add(info)

            if (info.left > width - paddingRight) {
                lastVisibleIndex = index
                break
            }
        }

        Log.e(TAG, "sleepCard $sleepCard ----------- ifs.size : ${ifs.size} ----- firstVisibleIndex : $firstVisibleIndex ---------- lastVisibleIndex : $lastVisibleIndex")
        Log.e(TAG, "normalOffset : $normalOffset ---------- stackOffset : $stackOffset")

        return ifs
    }

    private fun layoutChildView(recycler: RecyclerView.Recycler, ifs : ArrayList<ViewInfo>) {
        for (index in firstVisibleIndex..lastVisibleIndex) {
            val ifsIndex = index - firstVisibleIndex
            val child = recycler.getViewForPosition(index)
            addView(child)
            measureChildWithMargins(child, 0, 0)
            child.pivotX = 0f
            child.pivotY = childHeight / 2.toFloat()
            child.scaleX = ifs[ifsIndex].scale
            child.scaleY = ifs[ifsIndex].scale

            val l = ifs[ifsIndex].left
            val t = paddingTop
            val r = l + getDecoratedMeasurementHorizontal(child)
            val b = t + getDecoratedMeasurementVertical(child)

            layoutDecoratedWithMargins(child, l, t, r, b)
        }
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun scrollHorizontallyBy(delta: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        var dx = delta
        if (dx == 0 || childCount == 0) {
            return 0
        }
        horizontalOffset += dx
        if (dx < 0 && horizontalOffset < 0) {
            horizontalOffset = 0
            dx = 0
        }
        if (dx > 0 && horizontalOffset >= getMaxOffset()) {
            horizontalOffset = getMaxOffset().toInt()
            dx = 0
        }
        Log.e(TAG, "horizontalOffset : $horizontalOffset ---------- dx : $dx")
        onLayoutChildren(recycler, state)
        return dx
    }

    private fun recycleChildren(recycler: RecyclerView.Recycler) {
        val scrapList = recycler.scrapList
        Log.e(TAG, "scrapList.size : ${scrapList.size}")

        for (i in scrapList.indices) {
            val holder = scrapList[i]
            removeAndRecycleView(holder.itemView, recycler)
        }
    }

    private fun getMaxOffset(): Float {
        return (childWidth + normalMargin) * (itemCount - stackCount) + stackWidth - width
    }

    private fun getMinOffset(): Float {
        return 0f
    }


    private fun getDecoratedMeasurementHorizontal(view: View): Int {
        val params = view.layoutParams as RecyclerView.LayoutParams
        return (getDecoratedMeasuredWidth(view) + params.leftMargin + params.rightMargin)
    }

    private fun getDecoratedMeasurementVertical(view: View): Int {
        val params = view.layoutParams as RecyclerView.LayoutParams
        return (getDecoratedMeasuredHeight(view) + params.topMargin + params.bottomMargin)
    }


    data class ViewInfo(var left : Int = 0, var scale : Float = 1f)

}