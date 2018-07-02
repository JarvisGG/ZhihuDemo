package com.jarvis.zhihudemo.avtivity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Button;

import com.github.jinatonic.confetti.CommonConfetti;
import com.github.jinatonic.confetti.ConfettiManager;
import com.github.jinatonic.confetti.ConfettiSource;
import com.github.jinatonic.confetti.ConfettoGenerator;
import com.github.jinatonic.confetti.Utils;
import com.github.jinatonic.confetti.confetto.BitmapConfetto;
import com.github.jinatonic.confetti.confetto.Confetto;
import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.view.annotations.ContentView;
import com.jarvis.zhihudemo.view.annotations.ViewInject;
import com.jarvis.zhihudemo.view.annotations.ViewInjectUtils;
import com.jarvis.zhihudemo.widgets.AdUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/5 上午10:50
 * @changeRecord [修改记录] <br/>
 */

@ContentView(R.layout.activity_confetti)
public class ConfettiActivity extends BaseActivity {
    private ConfettiManager confettiManager;
    private CommonConfetti commonConfetti;
    private List<Bitmap> bitmaps;

    @ViewInject(R.id.container)
    ViewGroup container;

    @ViewInject(R.id.start)
    Button startbtn;

    @ViewInject(R.id.change)
    Button changeBtn;

    protected int goldDark, goldMed, gold, goldLight;
    protected int[] colors;


    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);

        final Resources res = getResources();
        goldDark = res.getColor(R.color.gold_dark);
        goldMed = res.getColor(R.color.gold_med);
        gold = res.getColor(R.color.gold);
        goldLight = res.getColor(R.color.gold_light);
        colors = new int[] { goldDark, goldMed, gold, goldLight };

        initBitmap(30);

        initManager1();

        startbtn.setOnClickListener((view) -> {
            initManager();
        });

        changeBtn.setOnClickListener(view -> {
            if (confettiManager != null) {
                confettiManager.setTargetVelocityY(0);
            }
        });
    }

    private void initBitmap(int num) {
        bitmaps = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            bitmaps.add(getBitmapFromVectorDrawable(this, R.drawable.ic_con));
        }
    }

    private void initManager1() {
        CommonConfetti.rainingConfetti(container, new int[] { Color.BLACK })
                .infinite();

        final int size = getResources().getDimensionPixelSize(R.dimen.default_confetti_size);
        final ConfettiSource confettiSource = new ConfettiSource(-size, -size);
        commonConfetti =
                CommonConfetti.rainingConfetti(container, colors);
//        final int velocitySlow = res.getDimensionPixelOffset(R.dimen.default_velocity_slow);
//        final int velocityNormal = res.getDimensionPixelOffset(R.dimen.default_velocity_normal);
//        final int velocityFast = res.getDimensionPixelOffset(R.dimen.default_velocity_fast);
//
//        // Further configure it
//        commonConfetti.getConfettiManager()
//                .setVelocityX(velocityFast, velocityNormal)
//                .setAccelerationX(-velocityNormal, velocitySlow)
//                .setTargetVelocityX(0, velocitySlow / 2)
//                    .setVelocityY(velocityNormal, velocitySlow);
    }

    private void initManager() {
        final Resources res = getResources();
        final int velocitySlow = res.getDimensionPixelOffset(R.dimen.default_velocity_slow);
        final int velocityNormal = res.getDimensionPixelOffset(R.dimen.default_velocity_normal);
        final int velocityFast = res.getDimensionPixelOffset(R.dimen.default_velocity_fast);
        final int size = getResources().getDimensionPixelSize(R.dimen.default_confetti_size);

        final Context context = container.getContext();
        final ConfettoGenerator generator = getDefaultGenerator();

        final ConfettiSource confettiSource = new ConfettiSource(AdUtils.getScreenSizeX(this) / 2, -AdUtils.getScreenSizeY(this) / 4);



        confettiManager = new ConfettiManager(context, generator, confettiSource, container)
                .setVelocityX(velocityFast, velocityNormal)
                .setVelocityY(velocityFast * 4, velocityFast * 5 / 2)
                .setTargetVelocityX(0, velocityNormal * 2)
                .setInitialRotation(180, 180)
                .setRotationalAcceleration(360, 180)
                .setTargetRotationalVelocity(360)
                .setNumInitialCount(0)
                .setEmissionDuration(2500)
                .setEmissionRate(60)
                .animate();

    }

    private ConfettoGenerator getDefaultGenerator() {
        final int numBitmaps = bitmaps.size();
        return random -> new BitmapConfetto(bitmaps.get(random.nextInt(numBitmaps)));
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Bitmap bitmap = null;
        Bitmap result = null;
        VectorDrawableCompat drawable = VectorDrawableCompat.create(context.getResources(), drawableId, context.getTheme());
        Random random = new Random();
        int ranColor = 0xff000000 | random.nextInt(0x00ffffff);
        int[] color = new int[]{0x000084FF, 0x00FAD052, 0x002AEEE3, 0x00FF5E62};
        int index = (int) (Math.random() * color.length);
        ranColor = 0xff000000 | color[index];
        if (drawable != null) {
            drawable.setTint(ranColor);
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float s = random.nextFloat() * (1.1f - 0.8f) + 0.8f;
            matrix.postScale(s, s);
            result = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        }
        return result;
    }
}
