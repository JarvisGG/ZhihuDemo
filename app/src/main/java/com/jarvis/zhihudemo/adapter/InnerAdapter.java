package com.jarvis.zhihudemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/3/30 下午3:12
 * @changeRecord [修改记录] <br/>
 */

public class InnerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int mLayoutId;

    private View mLayoutRoot;

    private Class mInnerViewHolder;

    private IAdapterCallback iAdapterCallback;

    private Context mContext;

    private interface IAdapterCallback {

        RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position);

        public void onUnBindViewHolder(RecyclerView.ViewHolder holder);

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder);

        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder);

    }

    private InnerAdapter(Builder builder) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return iAdapterCallback.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        iAdapterCallback.onBindViewHolder(holder, position);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        iAdapterCallback.onViewAttachedToWindow(holder);

    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        iAdapterCallback.onViewDetachedFromWindow(holder);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        iAdapterCallback.onUnBindViewHolder(holder);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class Builder {

        public InnerAdapter builder() {
            return new InnerAdapter(this);
        }
    }
}
