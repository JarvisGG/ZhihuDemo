package com.jarvis.zhihudemo.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;
import com.jarvis.zhihudemo.widgets.blur.BlurView;
import com.jarvis.zhihudemo.widgets.blur.SupportRenderScriptBlur;

import jp.wasabeef.blurry.Blurry;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.internal.FastBlur;
import jp.wasabeef.glide.transformations.internal.RSBlur;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/11/13 下午12:06
 * @changeRecord [修改记录] <br/>
 */

public class FloatContainerView extends CardView {

    private Context mContent;
    private LayoutInflater mInflater;
    private ImageView mLogoView;
    private TextView mContentView;

    private BlurView mBlurView;

    public FloatContainerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public FloatContainerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FloatContainerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContent = context;
        this.mInflater = LayoutInflater.from(mContent);

        this.setRadius(ExplosionUtils.dp2Px(10));
        this.setBackgroundColor(Color.TRANSPARENT);

        initView();
    }

    private void initView() {
        mInflater.inflate(R.layout.item_float_container, this);
        mLogoView = findViewById(R.id.logo);
        mContentView = findViewById(R.id.content);
        mBlurView = findViewById(R.id.float_root);
        setupBlurView();
    }

    public void bindData(FloatData data) {

        mContentView.setText(data.content);
    }

    public static class FloatData {

        private String url;
        private String content;

        public FloatData(String url, String content) {
            this.url = url;
            this.content = content;
        }
    }

    private void setupBlurView() {
        final float radius = 25f;
        final float minBlurRadius = 10f;
        final float step = 4f;

        //set background, if your root layout doesn't have one
        final Drawable windowBackground = ((Activity)mContent).getWindow().getDecorView().getBackground();

        final BlurView.ControllerSettings bottomViewSettings = mBlurView.setupWith((ViewGroup) ((Activity)mContent).getWindow().getDecorView().findViewById(android.R.id.content))
                .windowBackground(windowBackground)
                .blurAlgorithm(new SupportRenderScriptBlur(mContent))
                .blurRadius(minBlurRadius);

        int initialProgress = (int) (radius * step);
//        radiusSeekBar.setProgress(initialProgress);
//
//        radiusSeekBar.setOnSeekBarChangeListener(new SeekBarListenerAdapter() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                float blurRadius = progress / step;
//                blurRadius = Math.max(blurRadius, minBlurRadius);
//                topViewSettings.blurRadius(blurRadius);
//                bottomViewSettings.blurRadius(blurRadius);
//            }
//        });
    }
}
