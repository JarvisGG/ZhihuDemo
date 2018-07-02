package com.jarvis.zhihudemo.avtivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jarvis.library.widget.ArrayObjectAdapter;
import com.jarvis.library.widget.ObjectAdapter;
import com.jarvis.library.widget.Presenter;
import com.jarvis.library.widget.SinglePresenterSelector;
import com.jarvis.zhihudemo.MainActivity;
import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.view.GalleryRecyclerView;
import com.jarvis.zhihudemo.view.OverScrollLayout;
import com.jarvis.zhihudemo.widgets.AdUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/11/2 下午2:05
 * @changeRecord [修改记录] <br/>
 */

public class GalleryActivity extends BaseActivity {

    private final String[] imageArray = {"assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image1.jpg", "assets://image2.jpg", "assets://image3.jpg", "assets://image4.jpg", "assets://image5.jpg"};
    private final List<String> mData = new ArrayList<>();

    private GalleryRecyclerView mRecyclerView;
    private SinglePresenterSelector mPresenterSelector;
    private ArrayObjectAdapter mAdapter;
    private GalleryRecyclerView.CustomLinearLayoutManager linearLayoutManager;

    private OverScrollLayout overScrollLayout;

    private int dragx = 0, dragy = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callery);

        init();

    }

    private void init() {

        overScrollLayout = findViewById(R.id.callery_root);

        this.mPresenterSelector = new SinglePresenterSelector(new InnerPresenter(this));
        this.mAdapter = new ArrayObjectAdapter(mPresenterSelector);
        this.mRecyclerView = new GalleryRecyclerView(this);

        this.linearLayoutManager = new GalleryRecyclerView.CustomLinearLayoutManager(this);
        this.mRecyclerView.setLayoutManager(linearLayoutManager);
        for (int i = 0; i < imageArray.length; i++) {
            this.mAdapter.add(imageArray[i]);
        }
        this.mRecyclerView.setObjectAdapter(mAdapter);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                AdUtils.getScreenSizeY(this) * 2
        );

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    linearLayoutManager.setScrollEnabled(false);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        overScrollLayout.addView(mRecyclerView, params);
        overScrollLayout.setOnDragOffsetCallback(new OverScrollLayout.OnDragOffsetCallback() {
            @Override
            public void onDragOffset(int dx, int dy) {
                dragx = dx;
                dragy = dy;
                Log.e("dragoffset --------> ", "( dragx = "+ dragx + ", dragy = " + dragy + " )");
            }

            @Override
            public void onDragOver() {
                if (dragy >= 200) {
                    linearLayoutManager.setScrollEnabled(true);
                    mRecyclerView.smoothScrollBy(0,
                            AdUtils.getScreenSizeY(GalleryActivity.this),
                            new OvershootInterpolator(0.8f));
                }
                if (dragy <= -200) {
                    linearLayoutManager.setScrollEnabled(true);
                    mRecyclerView.smoothScrollBy(0,
                            -AdUtils.getScreenSizeY(GalleryActivity.this),
                            new OvershootInterpolator(0.8f));
                }
                dragy = 0;
                dragx = 0;
            }
        });

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
            View view = mInflater.inflate(R.layout.item_grallery, parent, false);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    AdUtils.getScreenSizeY(GalleryActivity.this)
            );
            view.setLayoutParams(params);
            return new InnerPresenter.InnerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item, int position) {
            String url = (String) item;
            InnerViewHolder holder = (InnerViewHolder) viewHolder;
            Random random = new Random();
            int ranColor = 0xff000000 | random.nextInt(0x00ffffff);
            holder.view.setBackgroundColor(ranColor);
            ((TextView)holder.view.findViewById(R.id.main_tv)).setText("Jarvis -----------------> " + position);
//            GlideApp.with(mContext).load(url).into(holder.mImg);
        }

        @Override
        public void onUnBindViewHolder(ViewHolder viewHolder) {

        }

        class InnerViewHolder extends ViewHolder {
            public ImageView mImg;
            public InnerViewHolder(View view) {
                super(view);
                mImg = view.findViewById(R.id.image);
            }
        }
    }

}
