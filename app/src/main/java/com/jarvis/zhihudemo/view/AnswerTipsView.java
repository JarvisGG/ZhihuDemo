package com.jarvis.zhihudemo.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/3/15 下午3:03
 * @changeRecord [修改记录] <br/>
 */

public class AnswerTipsView extends FrameLayout {

    private ImageView mBackView;
    private ImageView mArrow;
    private ImageView mHands;
    private FrameLayout mContainer;
    private FrameLayout mContainerBack;

    private ValueAnimator mAnimator;

    public AnswerTipsView(@NonNull Context context) {
        super(context);
        init();
    }

    public AnswerTipsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnswerTipsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initView();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initRxBus();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void initView() {

        mBackView = new ImageView(getContext());

        mContainerBack = new FrameLayout(getContext());
        mContainerBack.setClipChildren(true);
        mContainerBack.setClipToPadding(true);
        mContainerBack.addView(mBackView, new LayoutParams(
                ExplosionUtils.dp2Px(200),
                ExplosionUtils.dp2Px(160)
        ));

        mContainer = new FrameLayout(getContext());
        mContainer.setClipChildren(true);
        mContainer.setClipToPadding(true);

        LayoutParams containerBackParams = new LayoutParams(
                ExplosionUtils.dp2Px(200),
                ExplosionUtils.dp2Px(0)
        );
        containerBackParams.topMargin = ExplosionUtils.dp2Px(80);

        mContainer.addView(mContainerBack, containerBackParams);

        LayoutParams containerParams = new LayoutParams(
                ExplosionUtils.dp2Px(200),
                ExplosionUtils.dp2Px(160)
        );

        this.addView(mContainer, containerParams);

        mArrow = new ImageView(getContext());
        LayoutParams arrowParams = new LayoutParams(
                ExplosionUtils.dp2Px(24),
                ExplosionUtils.dp2Px(64)
        );
        arrowParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        arrowParams.bottomMargin = ExplosionUtils.dp2Px(-8);
        this.addView(mArrow, arrowParams);

        mHands = new ImageView(getContext());
        LayoutParams handParams = new LayoutParams(
                ExplosionUtils.dp2Px(112),
                ExplosionUtils.dp2Px(83)
        );
        handParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        handParams.rightMargin = ExplosionUtils.dp2Px(-32);
        handParams.bottomMargin = ExplosionUtils.dp2Px(-43);
        this.addView(mHands, handParams);

        mHands.setAlpha(0f);
        mArrow.setAlpha(0f);

        changeRes(true);
    }

    private void changeRes(boolean isLight) {
        if (isLight) {
            mBackView.setImageResource(R.drawable.zh_answer_guide);
            mArrow.setImageResource(R.drawable.zh_answer_uparrow);
            mHands.setImageResource(R.drawable.zh_answer_righthand);
        } else {
            mBackView.setImageResource(R.drawable.zh_answer_guide_dark);
            mArrow.setImageResource(R.drawable.zh_answer_uparrow_dark);
            mHands.setImageResource(R.drawable.zh_answer_righthand_dark);
        }
    }

    public void excuteAnim(int start, int end) {
        mAnimator = ValueAnimator.ofFloat(start, end);
        mAnimator.addUpdateListener(animation -> changeAnim((float)animation.getAnimatedValue()));
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.setDuration(500);
        mAnimator.start();
    }

    public void changeAnim(float value) {
        float offsetBack = ExplosionUtils.dp2Px(80) * value;
        float offsetHand = ExplosionUtils.dp2Px(66) * value;
        float offsetArrow = ExplosionUtils.dp2Px(40) * value;

        mHands.setTranslationY(-offsetHand);
        mArrow.setTranslationY(-offsetArrow);

        mHands.setAlpha(value);
        mArrow.setAlpha(value);

        LayoutParams containerParams = (LayoutParams) mContainerBack.getLayoutParams();
        containerParams.height = (int) (offsetBack * 2);
        containerParams.topMargin = (int) (ExplosionUtils.dp2Px(80) * (1 - value));
        mContainerBack.setLayoutParams(containerParams);
    }

    private void initRxBus() {
//        RxBus.getInstance().toObservable(ThemeChangedEvent.class)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<ThemeChangedEvent>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                    }
//
//                    @Override
//                    public void onComplete() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onNext(ThemeChangedEvent event) {
//                        changeRes(ThemeManager.isLight());
//                    }
//                });
    }


}
