package com.jarvis.zhihudemo.widgets.layoutmanager

import android.support.v7.widget.RecyclerView

/**
 * @author yyf
 * @since 11-14-2019
 */
class TanTanLayoutManager : RecyclerView.LayoutManager() {

    companion object {
        const val TAG = "SlipLayoutManager"
        const val TOTAL_SLIP_COUNT = 5
        const val TOTAL_TRANSLATE = 100

    }

    private val scaleStep = 0.05f

    private var firstVisibleIndex = 0

    private var lastVisibleIndex = 0

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
        val ifs = createViewIfs()
        detachAndScrapAttachedViews(recycler)
        layoutChildView(recycler, ifs)
    }


    private fun createViewIfs() : ArrayList<ViewInfo> {
        val ifs = arrayListOf<ViewInfo>()

        firstVisibleIndex = if (itemCount < TOTAL_SLIP_COUNT) 0 else itemCount - TOTAL_SLIP_COUNT
        lastVisibleIndex = itemCount - 1

        for (index in firstVisibleIndex..lastVisibleIndex) {
            val info = ViewInfo()
            val level = lastVisibleIndex - index
            info.level = level
            if (level > 0) {
                info.scaleX = (1f - scaleStep * level)
                if (level < TOTAL_SLIP_COUNT - 1) {
                    info.scaleY = (1f - scaleStep * level)
                    info.translate = (TOTAL_TRANSLATE * (level)).toFloat()
                } else {
                    info.scaleY = (1f - scaleStep * (level - 1))
                    info.translate = (TOTAL_TRANSLATE * (level - 1)).toFloat()
                }
            }
            ifs.add(info)
        }

        return ifs
    }

    private fun layoutChildView(recycler: RecyclerView.Recycler, ifs : ArrayList<ViewInfo>) {

        if (itemCount < 1) {
            return
        }
        for (index in 0 until ifs.size) {
            val info = ifs[index]
            val child = recycler.getViewForPosition(itemCount - info.level - 1)
            addView(child)
            measureChildWithMargins(child, 0, 0)
            val widthSpace = width - getDecoratedMeasuredWidth(child)
            val heightSpace = height - getDecoratedMeasuredHeight(child)
            layoutDecoratedWithMargins(child,
                    widthSpace / 2,
                    heightSpace / 4,
                    widthSpace / 2 + getDecoratedMeasuredWidth(child),
                    heightSpace / 4 + getDecoratedMeasuredHeight(child))

            if (info.level > 0) {
                child.scaleX = info.scaleX
                child.scaleY = info.scaleY
                child.translationY = info.translate
            }
            child.rotation = 0f
        }

    }

    data class ViewInfo(var level : Int = 0, var scaleX : Float = 1f, var scaleY : Float = 1f, var translate : Float = 0f)



}