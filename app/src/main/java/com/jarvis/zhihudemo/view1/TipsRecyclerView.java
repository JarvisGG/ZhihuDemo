package com.jarvis.zhihudemo.view1;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jarvis.zhihudemo.widgets.SimpleItemDecoration;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/4/2 下午5:00
 * @changeRecord [修改记录] <br/>
 */

public class TipsRecyclerView extends RecyclerView {

    private ITitleClickLisitener clickLisitener;

    public TipsRecyclerView(Context context) {
        super(context);
    }

    public TipsRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TipsRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        View childView = findChildViewUnder(e.getX(), e.getY());
        if (childView != null) {
            if (e.getY() >= 0 && e.getY() <= SimpleItemDecoration.itemDecorationHeight) {
                int position = (int) childView.getTag();
                clickLisitener.onClickTitleListener(position);
//                this.smoothScrollToPosition(position * 10);
            }
        } else {
            View nextView = findChildViewUnder(e.getX(), e.getY() + SimpleItemDecoration.itemDecorationHeight);
            if (nextView != null) {
                int position = (int) nextView.getTag();
                clickLisitener.onClickTitleListener(position);
//                this.smoothScrollToPosition(position * 10);
            }
        }

        return super.onInterceptTouchEvent(e);
    }

    public void register(ITitleClickLisitener iTitleClickLisitener) {
        clickLisitener = iTitleClickLisitener;
    }

    public interface ITitleClickLisitener {
        void onClickTitleListener(int position);
    }
}
