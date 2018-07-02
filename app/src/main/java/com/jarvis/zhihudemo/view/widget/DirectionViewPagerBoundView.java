package com.jarvis.zhihudemo.view.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/30 上午12:42
 * @changeRecord [修改记录] <br/>
 */

public class DirectionViewPagerBoundView extends FrameLayout {

    private Context mContext;
    private LayoutInflater mInflater;

    private ImageView mArrowView;

    private boolean isUp = false;

    public DirectionViewPagerBoundView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public DirectionViewPagerBoundView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DirectionViewPagerBoundView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DirectionViewPagerBoundView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.setBackgroundColor(Color.parseColor("#191970"));

        this.mArrowView = new ImageView(context);
        this.mArrowView.setImageResource(R.drawable.ic_arrow_right);
        this.mArrowView.setRotation(90);

        LayoutParams arrowParams = new LayoutParams(
                ExplosionUtils.dp2Px(24),
                ExplosionUtils.dp2Px(24)
        );
        arrowParams.gravity = Gravity.CENTER;
        this.addView(mArrowView, arrowParams);
    }

    public void changeArrowDirection(boolean up) {
        if (!isUp && up) {
            float rotation = mArrowView.getRotation();
            ValueAnimator animator = ValueAnimator.ofFloat(0, 90);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mArrowView.setRotation(rotation + (Float) valueAnimator.getAnimatedValue());
                }
            });
            animator.setInterpolator(new DecelerateInterpolator(2));
            animator.setDuration(300);
            animator.start();
            isUp = true;
        }

        if (isUp && !up) {
            float rotation = mArrowView.getRotation();
            ValueAnimator animator = ValueAnimator.ofFloat(0, -90);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mArrowView.setRotation(rotation + (Float) valueAnimator.getAnimatedValue());
                }
            });
            animator.setInterpolator(new DecelerateInterpolator(2));
            animator.setDuration(300);
            animator.start();
            isUp = true;
        }

    }

    public void changeArrow(float up) {
        float rotation = mArrowView.getRotation();
        Log.e("changeArrow -----> ", up + "");
        mArrowView.setRotation(up + 90);
    }



}
