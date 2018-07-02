package com.jarvis.zhihudemo.widgets.layoutmanager;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.jarvis.zhihudemo.widgets.ExplosionUtils;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/15 下午12:19
 * @changeRecord [修改记录] <br/>
 */

public abstract class RollBack extends ItemTouchHelper.SimpleCallback {

    private RecyclerView mRv;

    public RollBack(RecyclerView rv) {
        this(0, ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, rv);
    }

    public RollBack(int dragDirs, int swipeDirs, RecyclerView rv) {
        super(dragDirs, swipeDirs);
        mRv = rv;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public abstract void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        double swipValue = Math.sqrt(dX * dX + dY * dY);
        double fraction = swipValue / (mRv.getWidth() / 2);
        if (fraction > 1) fraction = 1;
        int childCount = mRv.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = mRv.getChildAt(i);
            int level = childCount - i - 1;
            if (level > 0) {
                child.setScaleX((float)(1 - 0.05f * level + fraction * 0.05f));
                if (level < 3) {
                    child.setScaleY((float)(1 - 0.05f * level + fraction * 0.05f));
                    child.setTranslationY((float) (ExplosionUtils.dp2Px(15) * level - fraction * ExplosionUtils.dp2Px(15)));
                }
            } else {
                float xFraction = dX / (mRv.getWidth() / 2);
                if (xFraction > 1) {
                    xFraction = 1;
                } else if (xFraction < -1) {
                    xFraction = -1;
                }
                child.setRotation(xFraction * 15);
            }
        }
    }
}
