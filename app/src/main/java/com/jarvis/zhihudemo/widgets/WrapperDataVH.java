package com.jarvis.zhihudemo.widgets;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.widgets.adapter.ObjectAdapter;

import java.util.List;

/**
 * @author yyf @ Zhihu Inc.
 * @since 06-06-2018
 */
public class WrapperDataVH<T> extends ObjectAdapter.EditInnerViewHolder<T> {

    RecyclerView InnerRecycler;

    public WrapperDataVH(View itemView) {
        super(itemView);
        InnerRecycler = itemView.findViewById(R.id.root);
    }

    @Override
    public void excuteBindData(T data) {

    }

    @Override
    public void bindOperator() {

    }


    public void setInnerAdapter(ObjectAdapter o) {
        InnerRecycler.setAdapter(o);
    }

    public void setInnerManager(LinearLayoutManager l) {
        InnerRecycler.setLayoutManager(l);
    }


}
