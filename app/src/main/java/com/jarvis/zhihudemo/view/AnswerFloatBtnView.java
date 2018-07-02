package com.jarvis.zhihudemo.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.TouchDelegate;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.jarvis.zhihudemo.widgets.ExplosionUtils;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title Futureve
 * @description 该类主要功能描述
 * @create 2018/2/7 下午4:06
 * @changeRecord [修改记录] <br/>
 */

public class AnswerFloatBtnView extends CardView {

    private AnimatorSet mClickAnimation;

    public AnswerFloatBtnView(Context pContext) {
        super(pContext);
        init();
    }

    public AnswerFloatBtnView(Context pContext, AttributeSet pAttributeSet) {
        super(pContext, pAttributeSet);
        init();
    }

    public AnswerFloatBtnView(Context pContext, AttributeSet pAttributeSet, int pDefaultStyle) {
        super(pContext, pAttributeSet, pDefaultStyle);
        init();
    }

    private void init() {
        this.setClickable(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        expandViewTouchDelegate(this,
                ExplosionUtils.dp2Px(4),
                ExplosionUtils.dp2Px(8),
                0,
                0);

    }

    private static void expandViewTouchDelegate(final View view, final int top, final int bottom, final int left, final int right) {
//        ((View) view.getParent()).post(() -> {
//            Rect bounds = new Rect();
//            view.setEnabled(true);
//            view.getHitRect(bounds);
//
//            bounds.top -= top;
//            bounds.bottom += bottom;
//            bounds.left -= left;
//            bounds.right += right;
//
//            TouchDelegate touchDelegate = new TouchDelegate(bounds, view);
//
//            if (View.class.isInstance(view.getParent())) {
//                ((View) view.getParent()).setTouchDelegate(touchDelegate);
//            }
//        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        restoreViewTouchDelegate(this);
    }

    public static void restoreViewTouchDelegate(final View view) {
        ((View) view.getParent()).post(() -> {
            Rect bounds = new Rect();
            bounds.setEmpty();
            TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

            if (View.class.isInstance(view.getParent())) {
                ((View) view.getParent()).setTouchDelegate(touchDelegate);
            }
        });
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }

    @Override
    public boolean performClick() {
        excuteClickAnim();
        return super.performClick();
    }

    private void excuteClickAnim() {
        if (mClickAnimation != null) {
            mClickAnimation.cancel();
            mClickAnimation = null;
        }
        mClickAnimation = new AnimatorSet();
        ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(this, "scaleX", 1.04f, 1f);
        ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(this, "scaleY", 1.04f, 1f);

        animatorScaleX.setInterpolator(new DecelerateInterpolator(2f));
        animatorScaleY.setInterpolator(new DecelerateInterpolator(2f));
        animatorScaleX.setDuration(200);
        animatorScaleY.setDuration(200);

        mClickAnimation.playTogether(animatorScaleX, animatorScaleY);
        mClickAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mClickAnimation = null;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mClickAnimation = null;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mClickAnimation.start();
    }
}
