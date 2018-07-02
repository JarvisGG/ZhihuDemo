package com.jarvis.zhihudemo.avtivity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.view.annotations.ContentView;
import com.jarvis.zhihudemo.view.annotations.ViewInject;
import com.jarvis.zhihudemo.view.annotations.ViewInjectUtils;
import com.jarvis.zhihudemo.view1.TipsRecyclerView;
import com.jarvis.zhihudemo.widgets.SimpleItemDecoration;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/4/2 下午4:59
 * @changeRecord [修改记录] <br/>
 */

@ContentView(R.layout.activity_tip_recycler)
public class TipsHeaderRecyclerActivity extends BaseActivity implements TipsRecyclerView.ITitleClickLisitener {

    @ViewInject(R.id.tip_recycler)
    TipsRecyclerView tipsRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);

        List<InnerData> list = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            InnerData data = new InnerData("Jarvis ----> " + i, i);
            list.add(data);
        }

        InnerAdapter adapter = new InnerAdapter(this, list);
        tipsRecyclerView.register(this);
        tipsRecyclerView.setAdapter(adapter);
        tipsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tipsRecyclerView.addItemDecoration(new SimpleItemDecoration(this, new SimpleItemDecoration.ObtainTextCallback() {
            @Override
            public String getText(int position) {
                return list.get(position).index / 10 + "";
            }
        }));
    }

    @Override
    public void onClickTitleListener(int position) {
        Toast.makeText(this, "" + position, LENGTH_SHORT).show();
    }

    class InnerAdapter extends RecyclerView.Adapter<InnerViewHolder> {

        private Context mContext;
        private LayoutInflater mInflater;

        private List<InnerData> mList;

        public InnerAdapter(Context context, List<InnerData> list) {
            this.mContext = context;
            this.mList = list;
            this.mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public InnerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_main, null, false);
            return new InnerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(InnerViewHolder holder, int position) {
            holder.mTv.setText(mList.get(position).name);
            holder.itemView.setTag(mList.get(position).index / 10);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    class InnerViewHolder extends RecyclerView.ViewHolder {

        private TextView mTv;

        public InnerViewHolder(View itemView) {
            super(itemView);
            mTv = itemView.findViewById(R.id.main_tv);
        }
    }

    class InnerData {
        public int index;
        public String name;
        public InnerData(String name, int index) {
            this.name = name;
            this.index = index;
        }
    }
}
