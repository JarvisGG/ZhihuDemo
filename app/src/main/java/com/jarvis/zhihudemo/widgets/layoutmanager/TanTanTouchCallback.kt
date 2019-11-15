package com.jarvis.zhihudemo.widgets.layoutmanager

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.jarvis.zhihudemo.widgets.layoutmanager.TanTanLayoutManager.Companion.TOTAL_SLIP_COUNT
import com.jarvis.zhihudemo.widgets.layoutmanager.TanTanLayoutManager.Companion.TOTAL_TRANSLATE
import kotlin.math.sqrt

/**
 * @author yyf
 * @since 11-14-2019
 */
class TanTanTouchCallback(
        dragDirs : Int,
        swipeDirs : Int,
        val recyclerView : RecyclerView,
        var data : ArrayList<Int>
 ) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    companion object {
        const val TAG = "SlipTouchCallback"

        const val MAX_ROTATION = 15

    }

    private val adapter = recyclerView.adapter

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val remove = data.removeAt(viewHolder.layoutPosition)
        data.add(0, remove)
        adapter.notifyDataSetChanged()
        viewHolder.itemView.rotation = 0f
    }

    override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val slipValue = sqrt(dX * dX + dY * dY)
        var fraction = slipValue / getThreshold()
        if (fraction > 1) {
            fraction = 1f
        }

        val childCount = recyclerView.childCount
        for (index in 0 until childCount) {
            val childView = recyclerView.getChildAt(index)
            val level = childCount - index - 1
            if (level > 0) {
                childView.scaleX = (1f - 0.05f * level + fraction * 0.05f)
                if (level < TOTAL_SLIP_COUNT - 1) {
                    childView.scaleY = (1f - 0.05f * level + fraction * 0.05f)
                    childView.translationY = TOTAL_TRANSLATE * level - fraction * TOTAL_TRANSLATE
                }
            } else {
                var fractionX = dX / getThreshold()
                if (fractionX > 1f) fractionX = 1f
                else if (fractionX < -1f) fractionX = -1f
                childView.rotation = fractionX * MAX_ROTATION
            }
        }
    }

    /**
     * 设置移除阈值
     */
    private fun getThreshold() : Float {
        return recyclerView.width * 0.8f
    }

}