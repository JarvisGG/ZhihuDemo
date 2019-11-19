package com.jarvis.zhihudemo.widgets.layoutmanager

import android.support.annotation.Px
import android.support.v7.widget.RecyclerView
import com.jarvis.zhihudemo.widgets.utils.dp2px

/**
 * @author yyf
 * @since 11-14-2019
 */
class SlipLayoutManager(
        var translateStep: Int = DEFAULT_TRANSLATE_STEP,
        var scaleStep: Float = DEFAULT_SCALE_STEP,
        var slipNum: Int = DEFAULT_TOTAL_SLIP_COUNT
) : RecyclerView.LayoutManager() {

    companion object {
        const val DEFAULT_TOTAL_SLIP_COUNT = 4
        const val DEFAULT_TRANSLATE_STEP = 20
        const val DEFAULT_SCALE_STEP = 0.05f
    }

    private var slipCount: Int = slipNum

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

        firstVisibleIndex = itemCount - slipNum - 1
        if (firstVisibleIndex < 0) {
            firstVisibleIndex = 0
        }
        lastVisibleIndex = itemCount - 1

        slipCount = lastVisibleIndex - firstVisibleIndex

        var count = 0
        for (index in firstVisibleIndex..lastVisibleIndex) {
            var realNum = if (count == 0 && slipCount == slipNum) 1 else count
            val info = ViewInfo()
            info.scale = 1f - (slipCount - realNum) * scaleStep

            if (slipCount < slipNum) {
                realNum++
            }

            val temp = if (slipNum - slipCount - 1 <= 0) 0 else slipNum - slipCount - 1
            info.translate = realNum * translateStep + (temp) * translateStep
            ifs.add(info)

            count ++
        }

        return ifs
    }

    private fun layoutChildView(recycler: RecyclerView.Recycler, ifs : ArrayList<ViewInfo>) {

        if (itemCount < 1) {
            return
        }

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

            val containerTotalHeight = childHeight + translateStep * (slipNum - 1)
            val containerTotalWidth = childWidth
            val leftOffset = (width - containerTotalWidth) / 2
            val topOffset = (height - containerTotalHeight) / 2

            val l = paddingLeft + leftOffset
            val t = paddingTop + ifs[ifsIndex].translate + topOffset
            val r = l + childWidth
            val b = t + childHeight

            layoutDecoratedWithMargins(child, l, t, r, b)

            ifsIndex++
        }

    }

    data class ViewInfo(var scale : Float = 1f, var translate : Int = 0)


    class SlipBuilder() {
        constructor(init: SlipBuilder.() -> Unit): this() {
            init()
        }

        @Px
        private var translateStep: Int = DEFAULT_TRANSLATE_STEP
        fun translateStep(init: () -> Int) {
            translateStep = init().dp2px
        }

        private var scaleStep: Float = DEFAULT_SCALE_STEP
        fun scaleStep(init: () -> Float) {
            scaleStep = init()
        }

        private var slipNum: Int = DEFAULT_TOTAL_SLIP_COUNT
        fun slipNum(init: () -> Int) {
            slipNum = init()
        }

        fun build() : SlipLayoutManager {
            return SlipLayoutManager(translateStep, scaleStep, slipNum)
        }
    }
}

fun slipLayoutManagerBuilder(init: SlipLayoutManager.SlipBuilder.() -> Unit) : SlipLayoutManager {
    return SlipLayoutManager.SlipBuilder(init).build()
}

fun bindLayoutManager(recyclerView: RecyclerView, init: SlipLayoutManager.SlipBuilder.() -> Unit): SlipLayoutManager {
    return slipLayoutManagerBuilder(init).apply {
        recyclerView.layoutManager = this
    }
}