package com.jarvis.zhihudemo.widgets.layoutmanager

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import java.util.ArrayList
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * @author yyf
 * @since 11-13-2019
 */
class TrapezoidLayoutManager : RecyclerView.LayoutManager() {


    companion object {
        const val TAG = "TrapezoidLayoutManager"
    }



    private var normalMarginTop = 0f


    private var childWidth = -1

    private var childHeight = -1

    private var verticalOffset = Integer.MAX_VALUE

    private var mScale = 0.95f

    private var mTranslateY = 0.9f


    init {
        isAutoMeasureEnabled = true
    }

    override fun generateDefaultLayoutParams() : RecyclerView.LayoutParams {
        val params = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT)
        return params
    }


    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (state.itemCount == 0 || state.isPreLayout) {
            return
        }

        fill(recycler)
    }

    private fun fill(recycler: RecyclerView.Recycler) {
        fillVertical(recycler)
    }

    private fun fillVertical(recycler: RecyclerView.Recycler) {
        detachAndScrapAttachedViews(recycler)

        if (childHeight == -1) {
            childWidth = (getHorizontalSpace() * 0.9f).toInt()
            childHeight = (childWidth * 1.46f).toInt()
        }

        verticalOffset = min(max(childHeight, verticalOffset), itemCount * childHeight)

        var bottomItemPosition = floor((verticalOffset / childHeight).toDouble()).toInt()
        val bottomItemVisibleHeight = verticalOffset % childHeight

        val verticalSpace = getVerticalSpace()
        val viewInfoArrayList = ArrayList<TrapezoidViewBean>()
        var remainSpace = verticalSpace - childHeight
        val offsetPercentRelativeToItemView = bottomItemVisibleHeight * mTranslateY / childHeight
        var i = bottomItemPosition - 1
        var j = 1
        Log.e(TAG + "1", "verticalOffset : $verticalOffset ---------- bottomItemPosition : $bottomItemPosition --------- bottomItemVisibleHeight : $bottomItemVisibleHeight")
        Log.e(TAG + "1", "verticalSpace : $verticalSpace ---------- remainSpace : $remainSpace --------- offsetPercentRelativeToItemView : $offsetPercentRelativeToItemView")

        while (i >= 0) {
            val maxOffset = (verticalSpace - childHeight) / 2 * 0.9
            val top = (remainSpace - offsetPercentRelativeToItemView * maxOffset).toInt()
            val scaleXY = (mScale.toDouble().pow((j - 1).toDouble()) * (1 - offsetPercentRelativeToItemView * (1 - mScale))).toFloat()
            val info = TrapezoidViewBean(top, scaleXY)
            Log.e(TAG + "2", "position : $i ----- top : $top ---------- scaleXY : $scaleXY --------- maxOffset : $maxOffset")
            viewInfoArrayList.add(0, info)
            remainSpace = (remainSpace - maxOffset).toInt()
            if (remainSpace <= 0) {
                info.top = (remainSpace + maxOffset).toInt()
                break
            }
            i--
            j++
        }
        if (bottomItemPosition < itemCount) {
            val start = (verticalSpace - bottomItemVisibleHeight)
            val itemViewInfo = TrapezoidViewBean(start,
                    1.0f)
            viewInfoArrayList.add(itemViewInfo)
            Log.e(TAG + "2", "position : special ----- top : $start ---------- scaleXY : 1.0f")
        } else {
            bottomItemPosition -= 1
        }

        val layoutCount = viewInfoArrayList.size
        val startPos = bottomItemPosition - (layoutCount - 1)
        for (index in 0 until layoutCount) {
            val view = recycler.getViewForPosition(startPos + index)
            val layoutInfo = viewInfoArrayList[index]
            addView(view)
            measureChildWithExactlySize(view)
            val left = (getHorizontalSpace() - childWidth) / 2
            layoutDecoratedWithMargins(view, left, layoutInfo.top, left + childWidth, layoutInfo.top + childHeight)
            view.pivotX = (view.width / 2).toFloat()
            view.pivotY = 0f
            view.scaleX = layoutInfo.scaleXY
            view.scaleY = layoutInfo.scaleXY
        }
    }

    private fun getMaxOffset(): Float {
        return (childHeight + normalMarginTop) * itemCount - height
    }

    private fun getMinOffset(): Float {
        return 0f
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val pendingScrollOffset = verticalOffset + dy
        verticalOffset = min(max(childHeight, verticalOffset + dy), itemCount * childHeight)
        onLayoutChildren(recycler, state)
        val res = (verticalOffset - pendingScrollOffset + dy).toInt()
        Log.e(TAG, "dy : $dy <----------------> res : $res")
        return res
    }


    private fun recycleChildren(recycler: RecyclerView.Recycler) {
        val scrapList = recycler.scrapList
        for (i in scrapList.indices) {
            val holder = scrapList[i]
            removeAndRecycleView(holder.itemView, recycler)
        }
    }

    /**
     * 获取RecyclerView的显示高度
     */
    fun getVerticalSpace(): Int {
        return height - paddingTop - paddingBottom
    }

    /**
     * 获取RecyclerView的显示宽度
     */
    fun getHorizontalSpace(): Int {
        return width - paddingLeft - paddingRight
    }

    private fun measureChildWithExactlySize(child: View) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(childWidth, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(childHeight, View.MeasureSpec.EXACTLY)
        child.measure(widthSpec, heightSpec)
    }

    data class TrapezoidViewBean(var top : Int, var scaleXY : Float)

}