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
public class InnerDataVH extends ObjectAdapter.EditInnerViewHolder<TopicActivity.InnerData> {

    public TextView tv;

    public InnerDataVH(View itemView) {
        super(itemView);
        tv = itemView.findViewById(R.id.main_tv);
    }

    @Override
    public void excuteBindData(TopicActivity.InnerData data) {

    }

    @Override
    public void bindOperator() {

    }
}