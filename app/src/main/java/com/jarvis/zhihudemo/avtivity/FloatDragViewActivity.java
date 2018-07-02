package com.jarvis.zhihudemo.avtivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.renderscript.RSRuntimeException;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jarvis.library.widget.ArrayObjectAdapter;
import com.jarvis.library.widget.Presenter;
import com.jarvis.library.widget.SinglePresenterSelector;
import com.jarvis.library.widget.ZhihuRecyclerView;
import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.view.FloatContainerView;
import com.jarvis.zhihudemo.view.FloatDragView;
import com.jarvis.zhihudemo.view.FloatView;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;

import jp.wasabeef.blurry.Blurry;
import jp.wasabeef.picasso.transformations.internal.FastBlur;
import jp.wasabeef.picasso.transformations.internal.RSBlur;

import static android.view.View.GONE;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/11/16 上午10:51
 * @changeRecord [修改记录] <br/>
 */

public class FloatDragViewActivity extends BaseActivity {

    private ZhihuRecyclerView recyclerView;
    private FloatContainerView containerView;
    private FloatDragView floatView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_drag_view);
        initView();
    }

    private void initView() {
        recyclerView = findViewById(R.id.float_view_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SinglePresenterSelector presenterSelector = new SinglePresenterSelector(new InnerPresenter(this));
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(presenterSelector);
        recyclerView.setObjectAdapter(adapter);

        for (int i = 0 ; i < 20; i++) {
            adapter.add(new InnerData(i+""));
        }

        floatView = findViewById(R.id.float_view);
        floatView.setVisibility(GONE);
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
            View view = mInflater.inflate(R.layout.item_float_view, parent, false);
            return new InnerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item, int position) {
            final InnerData data = (InnerData) item;
            final InnerViewHolder holder = (InnerViewHolder) viewHolder;
            holder.tv.setText(data.title);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Blurry.with(FloatDragViewActivity.this)
                            .radius(10)
                            .sampling(8)
                            .async()
                            .capture(holder.iv)
                            .into(holder.iv);
                    showFloatView();
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
                tv = view.findViewById(R.id.main_tv);
                iv = view.findViewById(R.id.main_iv);
            }
        }
    }

    private void showFloatView() {
        floatView.setVisibility(View.VISIBLE);
        containerView = new FloatContainerView(this);
        containerView.bindData(new FloatContainerView.FloatData("https://pic4.zhimg.com/v2-aac9fef8993ce584ac0fc6128eeb73c3.jpg", "阿迪开始放假卡还是对方立刻换了卡技术德虎发卡量时间德虎封口机拉萨德虎分"));
        floatView.bindView(containerView);
//        Blurry.with(FloatDragViewActivity.this)
//                .radius(10)
//                .sampling(8)
//                .async()
//                .capture(containerView.mBack)
//                .into(containerView.mBack);

//        Blurry.with(FloatDragViewActivity.this)
//                .radius(25)
//                .sampling(2)
//                .async()
//                .animate(500)
//                .onto(floatView);

        Bitmap bitmap = Bitmap.createBitmap(ExplosionUtils.getScreenSizeX(this),
                ExplosionUtils.dp2Px(78), Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.parseColor("#EBEBEB"));

        Bitmap preBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.demo);


    }

    class InnerData {
        String title;

        public InnerData(String text) {
            this.title = text;
        }
    }
}
