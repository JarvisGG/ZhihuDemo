package com.jarvis.zhihudemo.widgets;

import android.view.View;
import android.widget.TextView;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.avtivity.TopicActivity;
import com.jarvis.zhihudemo.widgets.adapter.ObjectAdapter;

/**
 * @author yyf @ Zhihu Inc.
 * @since 06-06-2018
 */
public class InnerInnerDataVH extends ObjectAdapter.EditInnerViewHolder<TopicActivity.InnerInnerData> {

    public TextView tv;

    public InnerInnerDataVH(View itemView) {
        super(itemView);
        tv = itemView.findViewById(R.id.inner_tv);
    }

    @Override
    public void excuteBindData(TopicActivity.InnerInnerData data) {

    }

    @Override
    public void bindOperator() {

    }
}