package com.jarvis.zhihudemo.widgets.layoutmanager;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.jarvis.zhihudemo.widgets.AdUtils;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/12 下午2:05
 * @changeRecord [修改记录] <br/>
 */

public class OverLayCardLayoutManager extends RecyclerView.LayoutManager {

    public static final int sShowCardNum = 4;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }
        detachAndScrapAttachedViews(recycler);
        fill(recycler, state);
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int minPos = 0;
        int itemCount = getItemCount();
        if (itemCount < 4) {
            minPos = 0;
        } else {
            minPos = itemCount - 4;
        }

        for (int i = minPos; i < itemCount; i++) {
            View child = recycler.getViewForPosition(i);
            addView(child);
            measureChildWithMargins(child, 0, 0);
            int widthSpace = getWidth() - getDecoratedMeasuredWidth(child);
            int heightSpace = getHeight() - getDecoratedMeasuredHeight(child);
            layoutDecoratedWithMargins(child,
                    widthSpace / 2,
                    heightSpace / 2,
                    getWidth() - widthSpace / 2,
                    getHeight() - heightSpace / 2);

            int level = getItemCount() - i - 1;
            if (level > 0) {
                child.setScaleX(1 - 0.05f * level);
                if (level < 3) {
                    child.setTranslationY(ExplosionUtils.dp2Px(15) * level);
                    child.setScaleY(1 - 0.05f * level);
                } else {
                    child.setTranslationY(ExplosionUtils.dp2Px(15) * (level - 1));
                    child.setScaleY(1 - 0.05f * (level - 1));
                }
            }
        }
    }
}
