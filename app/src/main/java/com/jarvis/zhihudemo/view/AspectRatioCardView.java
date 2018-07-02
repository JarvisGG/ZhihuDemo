package com.jarvis.zhihudemo.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/11/6 上午10:16
 * @changeRecord [修改记录] <br/>
 */

public class AspectRatioCardView extends CardView {

    private float ratio = 1.2f;

    public AspectRatioCardView(Context context) {
        super(context);
    }

    public AspectRatioCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (ratio > 0) {
            int ratioHeight = (int) (getMeasuredWidth() * ratio);
            setMeasuredDimension(getMeasuredWidth(), ratioHeight);
            ViewGroup.LayoutParams lp = getLayoutParams();
            lp.height = ratioHeight;
            setLayoutParams(lp);
        }
    }
}
