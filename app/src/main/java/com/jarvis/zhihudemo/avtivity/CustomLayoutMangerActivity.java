package com.jarvis.zhihudemo.avtivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jarvis.library.widget.ArrayObjectAdapter;
import com.jarvis.library.widget.Presenter;
import com.jarvis.library.widget.SinglePresenterSelector;
import com.jarvis.library.widget.ZhihuRecyclerView;
import com.jarvis.zhihudemo.MainActivity;
import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.view.annotations.ContentView;
import com.jarvis.zhihudemo.view.annotations.ViewInject;
import com.jarvis.zhihudemo.view.annotations.ViewInjectUtils;
import com.jarvis.zhihudemo.widgets.layoutmanager.FlowLayoutManager;
import com.jarvis.zhihudemo.widgets.layoutmanager.OverLayCardLayoutManager;
import com.jarvis.zhihudemo.widgets.layoutmanager.RollBack;
import com.jarvis.zhihudemo.widgets.layoutmanager.TestLayoutManager;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/11 上午9:57
 * @changeRecord [修改记录] <br/>
 */

@ContentView(R.layout.activity_main)
public class CustomLayoutMangerActivity extends BaseActivity {

    @ViewInject(R.id.main_rv)
    private ZhihuRecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);

        recyclerView.setLayoutManager(new OverLayCardLayoutManager());
        SinglePresenterSelector presenterSelector = new SinglePresenterSelector(new CustomLayoutMangerActivity.InnerPresenter(this));
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(presenterSelector);
        recyclerView.setObjectAdapter(adapter);

        for (int i = 0; i < 20; i ++) {
            adapter.add(new CustomLayoutMangerActivity.InnerData("n："+i));
        }

        ItemTouchHelper.Callback callback = new RollBack(recyclerView) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.remove(viewHolder.getAdapterPosition());
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    class InnerPresenter extends Presenter {

        private Context mContext;
        private LayoutInflater mInflater;

        public InnerPresenter(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View view = mInflater.inflate(R.layout.item_flow, parent, false);
            return new CustomLayoutMangerActivity.InnerPresenter.InnerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item, int position) {
            final CustomLayoutMangerActivity.InnerData data = (CustomLayoutMangerActivity.InnerData) item;
            final CustomLayoutMangerActivity.InnerPresenter.InnerViewHolder holder = (CustomLayoutMangerActivity.InnerPresenter.InnerViewHolder) viewHolder;
//            ViewGroup.LayoutParams params = holder.view.getLayoutParams();
//            if (position == 4) {
//                params.height *= 2;
//            }
//            if (position == 2) {
//                params.height *= 4;
//            }
//            if (position %3 == 0) {
//                params.height *= (position / 3 + 1) * 0.4;
//            }
//            holder.view.setLayoutParams(params);
            holder.tv.setText(data.title);
            holder.iv.setImageResource(R.drawable.demo2);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        @Override
        public void onUnBindViewHolder(ViewHolder viewHolder) {

        }

        class InnerViewHolder extends ViewHolder {

            public TextView tv;
            public ImageView iv;
            public InnerViewHolder(View view) {
                super(view);
                tv = view.findViewById(R.id.tvPrecent);
                iv = view.findViewById(R.id.iv);
            }
        }
    }

    class InnerData {
        String title;

        public InnerData(String t) {
            this.title = t;
        }
    }
}
