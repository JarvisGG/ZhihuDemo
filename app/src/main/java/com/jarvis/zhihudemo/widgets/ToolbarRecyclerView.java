package com.jarvis.zhihudemo.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yyf @ Zhihu Inc.
 * @since 07-23-2018
 */
public class ToolbarRecyclerView extends RecyclerView {

    private List<View> mChildViews;
    private InnerAdapter mAdapter;

    public ToolbarRecyclerView(Context context) {
        super(context);
        initOperator();
    }

    public ToolbarRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initOperator();
    }

    public ToolbarRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initOperator();
    }

    private void initOperator() {
        mAdapter = new InnerAdapter();
        setAdapter(mAdapter);
        setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void bindChildView(View... views) {
        mChildViews = new ArrayList<>();
        mChildViews.addAll(Arrays.asList(views));
        mAdapter.notifyDataSetChanged();
        mAdapter.notifyItemRangeChanged(0, mChildViews.size());
    }

    class InnerAdapter extends Adapter<InnerHolder> {

        @Override
        public InnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FrameLayout itemContainer = new FrameLayout(parent.getContext());
            FrameLayout.LayoutParams itemParams = new FrameLayout.LayoutParams(
                    ExplosionUtils.dp2Px(56), ExplosionUtils.dp2Px(56)
            );
            itemParams.gravity = Gravity.CENTER;
            itemContainer.setLayoutParams(itemParams);
            return new InnerHolder(itemContainer);
        }

        @Override
        public void onBindViewHolder(InnerHolder holder, int position) {

            ((FrameLayout)holder.itemView).addView(mChildViews.get(position));
        }

        @Override
        public int getItemCount() {
            return mChildViews.size();
        }
    }

    class InnerHolder extends ViewHolder {

        public InnerHolder(View itemView) {
            super(itemView);
        }
    }
}
