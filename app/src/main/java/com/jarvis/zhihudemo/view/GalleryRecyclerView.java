package com.jarvis.zhihudemo.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.jarvis.library.widget.ZhihuRecyclerView;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/11/2 下午1:47
 * @changeRecord [修改记录] <br/>
 */

public class GalleryRecyclerView extends ZhihuRecyclerView {
    public GalleryRecyclerView(Context context) {
        super(context);
    }

    public GalleryRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean requestRectangleOnScreen(Rect rectangle) {
        return super.requestRectangleOnScreen(rectangle);
    }

    public static class CustomLinearLayoutManager extends LinearLayoutManager {
        private boolean isScrollEnabled = false;

        public CustomLinearLayoutManager(Context context) {
            super(context);
        }

        public void setScrollEnabled(boolean isScrollEnabled) {
            this.isScrollEnabled = isScrollEnabled;
        }

        @Override
        public boolean canScrollVertically() {
            return isScrollEnabled;
        }

//        @Override
//        public int scrollVerticallyBy(int dy, Recycler recycler, State state) {
//            int dys = super.scrollVerticallyBy(dy, recycler, state);
//            return (int) (dys * 0.02);
//        }
    }
}
