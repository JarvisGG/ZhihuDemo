package com.jarvis.zhihudemo.avtivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.jarvis.library.widget.ArrayObjectAdapter;
import com.jarvis.library.widget.Presenter;
import com.jarvis.library.widget.SinglePresenterSelector;
import com.jarvis.library.widget.ZhihuRecyclerView;
import com.jarvis.zhihudemo.App;
import com.jarvis.zhihudemo.MainActivity;
import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.view.AnswerPagerActionPanelView;
import com.jarvis.zhihudemo.view.FloatContainerView;
import com.jarvis.zhihudemo.view.FloatView;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/11/13 上午11:49
 * @changeRecord [修改记录] <br/>
 */

public class FloatViewActivity extends BaseActivity {

    private ZhihuRecyclerView recyclerView;
    private FloatContainerView containerView;
    private FloatView floatView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams params;
    private AnswerPagerActionPanelView panelView;

    private static final String TAG = "RxPermissionTest";
    private RxPermissions rxPermission;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_view);
        initView();
        requestPermissions();
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
    }

    private void requestPermissions() {
        rxPermission = new RxPermissions(FloatViewActivity.this);

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
            if (position == 10) {
                View child = viewHolder.view;
                ViewGroup.LayoutParams params = child.getLayoutParams();
                params.height = ExplosionUtils.dp2Px(180);
            }
            holder.tv.setText(data.title);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showFloatView();

                    rxPermission
                            .requestEach(Manifest.permission.SYSTEM_ALERT_WINDOW)
                            .subscribe(new Consumer<Permission>() {
                                @Override
                                public void accept(Permission permission) throws Exception {
                                    if (permission.granted) {
                                        showFloatView();
                                        Log.d(TAG, permission.name + " is granted.");
                                    } else if (permission.shouldShowRequestPermissionRationale) {
                                        Log.d(TAG, permission.name + " is denied.");
                                    } else {
                                        Log.d(TAG, permission.name + " is denied.");
                                    }
                                }
                            });
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

    private void showFloatView() {
        containerView = new FloatContainerView(this);
        floatView = new FloatView(this);
        floatView.bindView(containerView);

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        params = ((App)getApplicationContext()).getMywmParams();
        params.format = PixelFormat.TRANSLUCENT;
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.alpha = 0.9f;
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;

        params.gravity = Gravity.LEFT|Gravity.TOP;
        params.width = ExplosionUtils.getScreenSizeX(this) - 2 * ExplosionUtils.dp2Px(10);
        params.height =  ExplosionUtils.dp2Px(74);

//        params.x = (ExplosionUtils.getScreenSizeX(this) - params.width) / 2 - ExplosionUtils.dp2Px(10);
//        params.y = (ExplosionUtils.getScreenSizeY(this) - params.height);
        params.x = 0;
        params.y = 0;

        mWindowManager.addView(floatView, params);
        containerView.bindData(new FloatContainerView.FloatData("https://pic4.zhimg.com/v2-aac9fef8993ce584ac0fc6128eeb73c3.jpg", "阿迪开始放假卡还是对方立刻换了卡技术德虎发卡量时间德虎封口机拉萨德虎分"));

    }

    class InnerData {
        String title;

        public InnerData(String text) {
            this.title = text;
        }
    }
}
