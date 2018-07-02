package com.jarvis.zhihudemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.widgets.AdUtils;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;

import java.util.List;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/12/28 上午11:40
 * @changeRecord [修改记录] <br/>
 */

public class MaterialRecyclerView extends RecyclerView {

    private InnerLayoutManager mLayoutManager;
    private InnerAdapter mAdapter;
    private LinearSnapHelper mSnapHelper;

    private int mCurrentItemPos = 0;
    private int mCurrentItemOffset;

    private float mStartMoveX;
    private float mStartMoveY;

    public MaterialRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public MaterialRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MaterialRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        initOperator();
        mLayoutManager = new InnerLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.setLayoutManager(mLayoutManager);
        mAdapter = new InnerAdapter(getContext(), mLayoutManager);
        this.setAdapter(mAdapter);
    }

    public void bindData(List<InnerData> list) {
        mAdapter.bindData(list);
    }

    public void initOperator() {
        mSnapHelper = new LinearSnapHelper();

//        this.addOnScrollListener(new OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                mCurrentItemOffset += dx;
//                computeCurrentItemPos();
//                onScrolledChangedCallback();
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    mSnapHelper.mNoNeedToScroll = mCurrentItemOffset == 0 || mCurrentItemOffset == getDestItemOffset(getAdapter().getItemCount() - 1);
//                } else {
//                    mSnapHelper.mNoNeedToScroll = false;
//                }
//            }
//        });
        mSnapHelper.attachToRecyclerView(this);
        initWidth();
    }

    private void initWidth() {
//        this.post(new Runnable() {
//            @Override
//            public void run() {
//                mCardGalleryWidth = MaterialRecyclerView.this.getWidth();
//                mCardWidth = mCardGalleryWidth - Utils.dp2px(mContext, 2 * (mPagePadding + mShowLeftCardWidth));
//                mOnePageWidth = mCardWidth;
//                HomeContainerView.this.smoothScrollToPosition(mCurrentItemPos);
//                onScrolledChangedCallback();
//            }
//        });
    }

    private void computeCurrentItemPos() {
//        if (mOnePageWidth <= 0)
//            return;
//        boolean pageChanged = false;
//        // 滑动超过一页说明已翻页
//        if (Math.abs(mCurrentItemOffset - mCurrentItemPos * mOnePageWidth) >= mOnePageWidth) {
//            pageChanged = true;
//        }
//        if (pageChanged) {
//            mCurrentItemPos = mCurrentItemOffset / mOnePageWidth;
//        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        View child = (this.findChildViewUnder(event.getX(), event.getY()));
        switch (event.getAction()) {
            case ACTION_DOWN:
                mStartMoveY = event.getY();
                mStartMoveX = event.getX();
                break;
            case ACTION_MOVE:
                float y = event.getY();
                float x = event.getX();
                int deltaY = (int) (mStartMoveY - y);
                int deltaX = (int) (mStartMoveX - x);
                if (Math.abs(deltaX) < Math.abs(deltaY)) {
                    child.onTouchEvent(event);
                    return false;
                }
            case ACTION_UP:
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return super.onTouchEvent(e);
    }

    public static class InnerLayoutManager extends LinearLayoutManager {
        public InnerLayoutManager(Context context) {
            super(context);
        }

        public InnerLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public InnerLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }
    }

    public static class InnerAdapter extends Adapter<InnerViewHolder> {

        private Context mContext;
        private LayoutInflater mInflater;
        private InnerLayoutManager mLayoutManager;

        private List<InnerData> mDatas;

        public InnerAdapter(Context context, InnerLayoutManager layoutManager) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
            this.mLayoutManager = layoutManager;
        }

        @Override
        public InnerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_material, parent, false);
            return new InnerViewHolder(view);
        }

        @Override
        public void onViewAttachedToWindow(InnerViewHolder holder) {
            super.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(InnerViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onBindViewHolder(InnerViewHolder holder, int position) {
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        public void bindData(List<InnerData> list) {
            this.mDatas = list;
            this.notifyDataSetChanged();
        }
    }

    public static class InnerViewHolder extends ViewHolder {
        public InnerViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class InnerData {
        public int cover;
    }
}
