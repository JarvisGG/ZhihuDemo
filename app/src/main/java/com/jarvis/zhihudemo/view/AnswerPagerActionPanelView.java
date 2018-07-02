package com.jarvis.zhihudemo.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.view.widget.AnswerScrollComponent;
import com.jarvis.zhihudemo.widgets.AdUtils;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;

import javax.security.auth.login.LoginException;


/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/25 上午11:09
 * @changeRecord [修改记录] <br/>
 */

public class AnswerPagerActionPanelView extends FrameLayout implements AnswerScrollComponent.IViewOperators {

    private View mActionPanel;
    private View mFloatView;

    private Context mContext;
    private LayoutInflater mInflater;

    private Handler mHander;

    public AnswerPagerActionPanelView(@NonNull Context context) {
        super(context);
        init(context);

    }

    public AnswerPagerActionPanelView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public AnswerPagerActionPanelView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        initView(context);
        setupAnim();
    }

    private void setupAnim() {
        mHander.postDelayed(this::excuteUpAnim, 2000);
    }

    private void excuteUpAnim() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator translationX = ObjectAnimator.ofFloat(mFloatView, View.TRANSLATION_Y, 0, -ExplosionUtils.dp2Px(42));
        translationX.setDuration(600);
        translationX.setInterpolator(new OvershootInterpolator(1.4f));

        ObjectAnimator alpha = ObjectAnimator.ofFloat(mFloatView, View.ALPHA, 0, 1);
        alpha.setDuration(500);

        set.playTogether(translationX, alpha);
        set.start();
    }

    private void initView(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mActionPanel = mInflater.inflate(R.layout.layout_answer_action_panel, this, false);
        this.mFloatView = mInflater.inflate(R.layout.widget_answer_float_btn, this, false);

        LayoutParams mPanelParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        this.addView(mActionPanel, mPanelParams);

        LayoutParams mFloatParams = new LayoutParams(
                ExplosionUtils.dp2Px(80),
                ExplosionUtils.dp2Px(32)
        );
        mFloatParams.gravity = Gravity.RIGHT | Gravity.TOP;
        mFloatParams.topMargin = ExplosionUtils.dp2Px(56);
        this.mFloatView.setAlpha(0);
        this.addView(mFloatView, mFloatParams);

        this.mHander = new Handler();
    }

    @Override
    public void intercept(float total, float current) {
        float res;
        if (total > 0) {
            res = 1 - current / total;
            if (res > 1) {
                res = 1f;
            } else if (res < 0) {
                res = 0f;
            }
        } else {
            res = 1;
        }
        mFloatView.setAlpha(res);
    }
}
