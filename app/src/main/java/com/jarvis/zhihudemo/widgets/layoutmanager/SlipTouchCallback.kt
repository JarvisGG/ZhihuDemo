package com.jarvis.zhihudemo.widgets.layoutmanager

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.jarvis.zhihudemo.avtivity.CustomLayoutManager2Activity
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author yyf
 * @since 11-14-2019
 */
class SlipTouchCallback(
        dragDirs : Int,
        swipeDirs : Int,
        val recyclerView : RecyclerView
 ) : CustomItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    companion object {
        const val TAG = "SlipTouchCallback"

        const val MAX_ROTATION = 15

    }

    private val adapter by lazy {
        recyclerView.adapter as CustomLayoutManager2Activity.Adapter
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        viewHolder.itemView.rotation = 0f
        val data = adapter.remove(viewHolder.layoutPosition)
        adapter.notifyDataSetChanged()
    }


    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
        super.clearView(recyclerView, viewHolder)
        Log.e(TAG, "method : clearView")
    }

    override fun getAnimationDuration(recyclerView: RecyclerView?, animationType: Int, animateDx: Float, animateDy: Float): Long {
        val duration = super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy)
        return duration * 3
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
        val slipValue = sqrt(dX.pow(2) + dY.pow(2))
        var fraction = slipValue / getThreshold()
        if (fraction > 1) {
            fraction = 1f
        }
        val firstIndex = recyclerView.childCount - 1
        val lastIndex = if (recyclerView.childCount < 5) 0 else 1
        for (index in firstIndex downTo lastIndex) {
            val childView = recyclerView.getChildAt(index)
            if (index == firstIndex) {
                var fractionX = dX / getThreshold()
                if (fractionX > 1) {
                    fractionX = 1f
                }
                childView.rotation = fractionX * MAX_ROTATION
                continue
            }

            childView.translationY = 20 * fraction
            childView.scaleX = (1f - (firstIndex - index) * 0.05f) + 0.05f * fraction
            childView.scaleY = (1f - (firstIndex - index) * 0.05f) + 0.05f * fraction
        }
    }

    /**
     * 设置移除阈值
     */
    private fun getThreshold() : Float {
        return recyclerView.width * 0.8f
    }

}