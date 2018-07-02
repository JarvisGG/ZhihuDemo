package com.jarvis.zhihudemo.avtivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.jarvis.library.widget.ArrayObjectAdapter;
import com.jarvis.library.widget.Presenter;
import com.jarvis.library.widget.SinglePresenterSelector;
import com.jarvis.library.widget.ZhihuRecyclerView;
import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.view.annotations.ContentView;
import com.jarvis.zhihudemo.view.annotations.ViewInject;
import com.jarvis.zhihudemo.view.annotations.ViewInjectUtils;
import com.jarvis.zhihudemo.widgets.AdUtils;

import static android.view.animation.LayoutAnimationController.ORDER_NORMAL;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/4/17 上午10:59
 * @changeRecord [修改记录] <br/>
 */

@ContentView(R.layout.activity_main)
public class CommentRecyclerActivity extends BaseActivity {

    @ViewInject(R.id.main_rv)
    private ZhihuRecyclerView recyclerView;

    private LayoutAnimationController layoutAnimationController;

    private AnimationSet animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SinglePresenterSelector presenterSelector = new SinglePresenterSelector(new InnerPresenter(this));
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(presenterSelector);
        recyclerView.setObjectAdapter(adapter);
        animation = new AnimationSet(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, AdUtils.getScreenSizeY(this) / 2, 0);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        animation.addAnimation(translateAnimation);
        animation.addAnimation(alphaAnimation);
        animation.setDuration(300);
        layoutAnimationController = new LayoutAnimationController(animation);
        layoutAnimationController.setOrder(ORDER_NORMAL);
        layoutAnimationController.setDelay(0.15f);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        for (int i = 0; i < 30; i++) {
            adapter.add(new InnerData("Jarvis -----> " + i));
        }
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
            View view = mInflater.inflate(R.layout.item_main, parent, false);
            return new InnerPresenter.InnerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item, int position) {
            final InnerData data = (InnerData) item;
            final InnerViewHolder holder = (InnerViewHolder) viewHolder;
            holder.tv.setText(data.title);
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
            public InnerViewHolder(View view) {
                super(view);
                tv = view.findViewById(R.id.main_tv);
            }
        }
    }

    class InnerData {
        String title;

        public InnerData(String data) {
            this.title = data;
        }
    }
}

