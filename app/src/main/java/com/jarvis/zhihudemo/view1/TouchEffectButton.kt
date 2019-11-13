package com.jarvis.zhihudemo.view1

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.animation.DecelerateInterpolator
import com.jarvis.zhihudemo.R
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author yyf
 * @since 11-11-2019
 */
class TouchEffectButton(context: Context, attributeSet: AttributeSet) : AppCompatImageButton(context, attributeSet) {

    companion object {
        const val SCALE_OFFSET = 0.94f
    }

    private var scaleAnimator : ValueAnimator? = null
    private var recoverAnimator : ValueAnimator? = null

    private var downY: Float = 0f
    private var downX: Float = 0f

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val res = super.onTouchEvent(event)

        val x = event.x
        val y = event.y

        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = x
                downY = y
                startScaling()
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = downX - event.x
                val deltaY = downY - event.y
                val delta = sqrt(deltaX.toDouble().pow(2.0) + deltaY.toDouble().pow(2.0))
                if (delta > ViewConfiguration.get(context).scaledTouchSlop * 2) {
                    stopScaling()
                }
            }
            MotionEvent.ACTION_UP -> {
                stopScaling()
            }
        }
        return res
    }

    private fun startScaling() {
        recoverAnimator?.cancel()
        recoverAnimator = null

        if (scaleAnimator != null) {
            return
        }

        scaleAnimator = ValueAnimator.ofFloat(1.0f, SCALE_OFFSET).apply {
            duration = 500
            injectUpdateListener()
            injectListener { scaleAnimator = null }
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    private fun stopScaling() {
        scaleAnimator?.cancel()
        scaleAnimator = null

        if (recoverAnimator != null) {
            return
        }

        recoverAnimator = ValueAnimator.ofFloat(getTag(R.id.touch_effect_scale_tag) as Float, 1.0f).apply {
            duration = 400
            injectUpdateListener()
            injectListener { recoverAnimator = null }
            interpolator = DecelerateInterpolator(2f)
            start()
        }
    }

    private fun ValueAnimator.injectUpdateListener() {
        this.addUpdateListener {
            val temp = it.animatedValue as Float
            scaleX = temp
            scaleY = temp
            setTag(R.id.touch_effect_scale_tag, temp)
        }
    }
    private fun ValueAnimator.injectListener(action : () -> Unit) {
        this.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                action()
            }
        })
    }
}