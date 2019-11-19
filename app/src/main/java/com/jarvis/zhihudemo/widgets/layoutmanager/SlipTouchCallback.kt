package com.jarvis.zhihudemo.widgets.layoutmanager

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import com.jarvis.zhihudemo.avtivity.CustomLayoutManager2Activity
import com.jarvis.zhihudemo.widgets.layoutmanager.SlipTouchCallback.Companion.DEFAULT_MAX_ROTATION
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author yyf
 * @since 11-14-2019
 */
class SlipTouchCallbackBuilder(
        private var recyclerView: RecyclerView,
        private var layoutManager: SlipLayoutManager,
        init: SlipTouchCallbackBuilder.() -> Unit
) {

    /**
     * 允许拖拽方向
     */
    private var dragDirs : Int = 0
    fun dragDirs(init: () -> Int) {
        this.dragDirs = init()
    }

    /**
     * 允许侧滑方向
     */
    private var swipeDirs : Int = 0
    fun swipeDirs(init: () -> Int) {
        this.swipeDirs = init()
    }

    /**
     * 侧滑最大角度
     */
    private var rotationLimit : Int = DEFAULT_MAX_ROTATION
    fun rotationLimit(init: () -> Int) {
        this.rotationLimit = init()
    }

    /**
     * 左右滑动更新值
     */
    private var notifyFraction: ((fraction : Float) -> Unit)? = null
    fun notifyFraction(init: () -> ((fraction : Float) -> Unit)) {
        this.notifyFraction = init()
    }

    /**
     * 删除第一个卡片
     */
    private var notifyItemRemove: ((position : Int) -> Unit)? = null
    fun notifyItemRemove(init: () -> ((position : Int) -> Unit)) {
        this.notifyItemRemove = init()
    }


    fun build() : SlipTouchCallback {
        return SlipTouchCallback(layoutManager, dragDirs, swipeDirs, rotationLimit, notifyFraction, notifyItemRemove, recyclerView)
    }

    init {
        init()
    }
}

fun slipTouchCallbackBuilder(
        recyclerView: RecyclerView,
        layoutManager: SlipLayoutManager,
        init : SlipTouchCallbackBuilder.() -> Unit
) = SlipTouchCallbackBuilder(recyclerView, layoutManager, init).build()

fun bindTouchHelper(
        recyclerView: RecyclerView,
        layoutManager: SlipLayoutManager,
        init : SlipTouchCallbackBuilder.() -> Unit
) {
    CustomItemTouchHelper(slipTouchCallbackBuilder(recyclerView, layoutManager, init)).attachToRecyclerView(recyclerView)
}

class SlipTouchCallback(
        private val layoutManager: SlipLayoutManager,
        private val dragDirs: Int,
        private val swipeDirs: Int,
        private val rotationLimit: Int = DEFAULT_MAX_ROTATION,
        private val notifyFraction: ((fraction : Float) -> Unit)? = null,
        private val notifyItemRemove: ((position : Int) -> Unit)? = null,
        private val recyclerView : RecyclerView
) : CustomItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    companion object {
        const val TAG = "SlipTouchCallback"
        const val DEFAULT_MAX_ROTATION = 15
    }


    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        viewHolder.itemView.rotation = 0f
        notifyItemRemove?.invoke(viewHolder.layoutPosition)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.6f
    }

    override fun getAnimationThreshold(): Float {
        return 1.3f
    }

    override fun getAnimationDuration(recyclerView: RecyclerView?, animationType: Int, animateDx: Float, animateDy: Float): Long {
        val duration = super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy)
        return (duration * 1.2f).toLong()
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
        val lastIndex = if (recyclerView.childCount < layoutManager.slipNum + 1) 0 else 1
        for (index in firstIndex downTo lastIndex) {
            val childView = recyclerView.getChildAt(index)
            if (index == firstIndex) {
                var fractionX = dX / getThreshold()
                if (fractionX > 1) {
                    fractionX = 1f
                }
                childView.rotation = fractionX * rotationLimit
                continue
            }

            childView.translationY = layoutManager.translateStep * fraction
            childView.scaleX = (1f - (firstIndex - index) * layoutManager.scaleStep) + layoutManager.scaleStep * fraction
            childView.scaleY = (1f - (firstIndex - index) * layoutManager.scaleStep) + layoutManager.scaleStep * fraction
        }
        notifyFraction?.invoke(fraction)
    }

    /**
     * 设置移除阈值
     */
    private fun getThreshold() : Float {
        return recyclerView.width * 0.8f
    }

}