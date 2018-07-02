package com.jarvis.zhihudemo.avtivity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.widgets.AdUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/12/13 上午10:56
 * @changeRecord [修改记录] <br/>
 */

public class GlideCrossActivity extends BaseActivity {

    private ImageView mIv;
    public static final String img = "http://imgq.duitang.com/uploads/item/201504/30/20150430201805_CLRKe.thumb.700_0.jpeg";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide);
        mIv = findViewById(R.id.iiiv);
        int width = AdUtils.getScreenSizeX(this);
        int height = AdUtils.getScreenSizeY(this);

        Observable.create(e -> e.onNext(img))
               .map(o -> {
                   Bitmap res = Glide.with(GlideCrossActivity.this)
                           .load(R.drawable.demo3)
                           .asBitmap()
                           .into(AdUtils.getScreenSizeX(GlideCrossActivity.this),
                                   AdUtils.getScreenSizeY(GlideCrossActivity.this))
                           .get();
                   return res;
               })
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
                .map(bitmap -> {
                    Matrix matrix = new Matrix();
                    matrix.postScale(1, 1);
                    Bitmap target = Bitmap.createBitmap(
                            bitmap,
                            0,
                            0,
                            bitmap.getWidth(),
                            bitmap.getWidth()*height/width,
                            matrix,
                            true);

                    return target;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                   @Override
                   public void onSubscribe(Disposable d) {

                   }

                   @Override
                   public void onNext(Bitmap bitmap) {
                       mIv.setImageBitmap(bitmap);
                   }

                   @Override
                   public void onError(Throwable e) {
                       
                   }

                   @Override
                   public void onComplete() {

                   }
               });

    }

    /**
     * Glide 圆角 Transform
     */

    public static class GlideRoundTransform extends BitmapTransformation {

        private static float radius = 0f;

        /**
         * 构造函数 默认圆角半径 4dp
         *
         * @param context Context
         */
        public GlideRoundTransform(Context context) {
            this(context, 4);
        }

        /**
         * 构造函数
         *
         * @param context Context
         * @param dp 圆角半径
         */
        public GlideRoundTransform(Context context, int dp) {
            super(context);
            radius = Resources.getSystem().getDisplayMetrics().density * dp;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform);
        }

        private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            if (source == null) {
                return null;
            }

            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            paint.setShadowLayer(30, 5, 2, Color.GREEN);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);

            return result;
        }

        @Override
        public String getId() {
            return getClass().getName() + Math.round(radius);
        }
    }

}
