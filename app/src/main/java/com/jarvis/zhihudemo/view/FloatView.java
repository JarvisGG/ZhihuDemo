package com.jarvis.zhihudemo.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.DrawableContainer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.jarvis.zhihudemo.App;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;

import static android.content.Context.WINDOW_SERVICE;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/11/13 下午12:01
 * @changeRecord [修改记录] <br/>
 */

public class FloatView extends CardView {

    private Context mContext;
    private View mContainerView;

    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;

    private WindowManager wm = (WindowManager)getContext().getSystemService(WINDOW_SERVICE);
    private WindowManager.LayoutParams wmParams = ((App)getContext().getApplicationContext()).getMywmParams();

    public FloatView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public FloatView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FloatView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        initView();
    }

    private void initView() {
        this.setRadius(ExplosionUtils.dp2Px(10));
        this.setCardBackgroundColor(Color.YELLOW);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getRawX();
        y = event.getRawY() - 25;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                updateViewPosition();
                mTouchStartX = mTouchStartY = 0;
                break;
            default:
                break;
        }
        return true;
    }

    private void updateViewPosition() {
        wmParams.x = (int)(x - mTouchStartX);
        wmParams.y = (int)(y - mTouchStartY);
        wm.updateViewLayout(this, wmParams);
    }


    public void bindView(View containerView) {
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT, ExplosionUtils.dp2Px(74)
        );
        this.addView(containerView, params);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

}
