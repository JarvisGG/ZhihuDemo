package com.jarvis.zhihudemo.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title OverScrollLayout
 * @description 该类主要功能描述
 * @create 2018/1/19 下午3:06
 * @changeRecord [修改记录] <br/>
 */

public class VerticalViewPager extends ViewPager {

    public VerticalViewPager(Context context) {
        this(context, null);
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPageTransformer(false, new DefaultTransformer());
    }

    private MotionEvent swapTouchEvent(MotionEvent event) {
        float width = getWidth();
        float height = getHeight();

        float swappedX = (event.getY() / height) * width;
        float swappedY = (event.getX() / width) * height;

        event.setLocation(swappedX, swappedY);

        return event;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }


    public class DefaultTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View view, float position) {
            float alpha = 0;
            if (0 <= position && position <= 1) {
                alpha = 1 - position;
            } else if (-1 < position && position < 0) {
                alpha = position + 1;
            }
            view.setAlpha(alpha);
            view.setTranslationX(view.getWidth() * -position);
            float yPosition = position * view.getHeight();
            view.setTranslationY(yPosition);
        }
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return super.canScrollVertically(direction);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return super.canScrollHorizontally(direction);
    }
}