package com.jarvis.zhihudemo.avtivity;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.view.annotations.ContentView;
import com.jarvis.zhihudemo.view.annotations.ViewInject;
import com.jarvis.zhihudemo.view.annotations.ViewInjectUtils;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;
import com.jarvis.zhihudemo.widgets.InnerDataVH;
import com.jarvis.zhihudemo.widgets.InnerInnerDataVH;
import com.jarvis.zhihudemo.widgets.adapter.ObjectAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yyf @ Zhihu Inc.
 * @since 05-30-2018
 */
@ContentView(R.layout.activity_topic)
public class TopicActivity extends BaseActivity {

    @ViewInject(R.id.top_rv)
    RecyclerView recyclerView;

    ObjectAdapter mAdapter;

    List list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);

        list = new ArrayList();
        for (int i = 0; i < 20; i++ ) {
            list.add(new InnerData("Jarvis ^-^ -> " + i));
        }

//        list.add(4, new ArrayList() {{
//            add(new InnerInnerData("Jarvis inner ^-^ -> " + 1));
//            add(new InnerInnerData("Jarvis inner ^-^ -> " + 2));
//            add(new InnerInnerData("Jarvis inner ^-^ -> " + 3));
//            add(new InnerInnerData("Jarvis inner ^-^ -> " + 4));
//            add(new InnerInnerData("Jarvis inner ^-^ -> " + 5));
//            add(new InnerInnerData("Jarvis inner ^-^ -> " + 6));
//            add(new InnerInnerData("Jarvis inner ^-^ -> " + 7));
//            add(new InnerInnerData("Jarvis inner ^-^ -> " + 8));
//            add(new InnerInnerData("Jarvis inner ^-^ -> " + 9));
//        }});

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = ObjectAdapter.create(this)
                .addLayer(InnerData.class, InnerDataVH.class, R.layout.item_main)
                .bindList(list)
                .bindOperator((holder, data, position) -> {
                    if (holder instanceof InnerDataVH) {
                        if (((InnerData)data).title.contains("kk")) {
                            ((InnerDataVH) holder).tv.setText(((InnerData) data).title);
                            ((InnerDataVH) holder).itemView.getLayoutParams().height = 300;
                            ((InnerDataVH) holder).itemView.setTag("111");
                        } else {
                            ((InnerDataVH) holder).tv.setText(((InnerData) data).title);
                            ((InnerDataVH) holder).itemView.getLayoutParams().height = 150;
                            ((InnerDataVH) holder).itemView.setTag("222");
                        }
                    }
                    if (holder instanceof InnerInnerDataVH) {
                        ((InnerInnerDataVH) holder).tv.setText(((InnerInnerData)data).title);
                    }
                })
                .registerObserver(new ObjectAdapter.AdapterListener())
                .build();
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        });

        findViewById(R.id.add_inner).setOnClickListener(v -> {
            mAdapter.add(new InnerData("Jarvis inner ^-^ -> kk"), 0);

            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
            View halfVisiableView = layoutManager.findViewByPosition(lastVisiblePosition);
            int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
            View firstVisiableView = layoutManager.findViewByPosition(firstVisiblePosition);
            Rect visiableRect = new Rect();
            halfVisiableView.getWindowVisibleDisplayFrame(visiableRect);

            int distance = -firstVisiableView.getMeasuredHeight();

            recyclerView.smoothScrollBy(0, distance);


        });

    }




    public class InnerData {
        String title;

        public InnerData(String t) {
            this.title = t;
        }
    }

    public class InnerInnerData {
        String title;

        public InnerInnerData(String t) {
            this.title = t;
        }
    }

}
