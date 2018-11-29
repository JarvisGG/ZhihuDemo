package com.jarvis.zhihudemo.avtivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.view.annotations.ContentView;
import com.jarvis.zhihudemo.view.annotations.ViewInject;
import com.jarvis.zhihudemo.view.annotations.ViewInjectUtils;
import com.jarvis.zhihudemo.view1.TopicLabelLayout;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yyf @ Zhihu Inc.
 * @since 11-27-2018
 */
@ContentView(R.layout.activity_topic_label)
public class TopicLabelActivity extends AppCompatActivity {

    @ViewInject(R.id.topic_label)
    TopicLabelLayout topicLabelLayout;

    @ViewInject(R.id.add)
    Button add;

    @ViewInject(R.id.remove)
    Button remove;

    public class InnerData {
        String label = "Jarvis";

        InnerData() {

        }

        InnerData(String name) {
            label = name;
        }
    }

    public class InnerAdapter extends TopicLabelLayout.Adapter<TopicLabelLayout.ViewHolder, InnerData> {

        LayoutInflater layoutInflater;

        public InnerAdapter() {
            initParam();
        }

        public InnerAdapter(List<InnerData> list) {
            mData = list;
            initParam();
        }

        private void initParam() {
            layoutInflater = LayoutInflater.from(TopicLabelActivity.this);
        }

        @Override
        public TopicLabelLayout.ViewHolder onCreateViewHolder(ViewGroup parent) {
            View view = layoutInflater.inflate(R.layout.item_topic_label, parent, false);
            return new TopicLabelLayout.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TopicLabelLayout.ViewHolder holder, int position) {
            TextView name = holder.itemView.findViewById(R.id.title);
            name.setText(mData.get(position).label);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);

        List<InnerData> data = new ArrayList() {{
            add(new InnerData("交互设计"));
            add(new InnerData("交互设计交互设计"));
            add(new InnerData("交"));
            add(new InnerData("交互设计叫"));
            add(new InnerData("交互设"));
            add(new InnerData("交互设计叫交互设计叫"));
            add(new InnerData("交互"));
            add(new InnerData("交互设计叫"));
            add(new InnerData("交互设计叫交互设计叫"));
            add(new InnerData("交互设计"));
            add(new InnerData("交互设计交互设计"));
            add(new InnerData("交"));
            add(new InnerData("交互设计叫"));
            add(new InnerData("交互设计叫交互设计叫交互设计叫交互设计叫交互设计叫交互设计叫"));
            add(new InnerData("交互"));
            add(new InnerData("交互设计叫"));
            add(new InnerData("交"));
            add(new InnerData("交互设计叫交互设计叫"));
            add(new InnerData("交互设计叫交互设计叫"));
            add(new InnerData("交互设计叫交互设计叫"));
        }};

        InnerAdapter adapter = new InnerAdapter(data);

        topicLabelLayout.setAdapterInternal(adapter);

        add.setOnClickListener(v -> {
            adapter.addData(new InnerData("Jarvis"), 6);
            adapter.notifyItemRangeInserted(6, 1);
        });

        remove.setOnClickListener(v -> {
            adapter.removeData(6);
            adapter.notifyItemRangeRemoved(6, 1);
        });
    }
}
