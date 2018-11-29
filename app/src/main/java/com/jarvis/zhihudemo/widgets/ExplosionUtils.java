package com.jarvis.zhihudemo.widgets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import static android.content.Context.WINDOW_SERVICE;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/11/13 下午12:29
 * @changeRecord [修改记录] <br/>
 */

public class ExplosionUtils {
    private static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;
    private static final Canvas sCanvas = new Canvas();

    public static int dp2Px(int dp) {
        return Math.round(dp * DENSITY);
    }

    public static int dpToPixel(final Context pContext, final float pDp) {
        if (pContext == null) {
            return 0;
        }

        final float density = pContext.getResources().getDisplayMetrics().density;

        return (int) ((pDp * density) + 0.5f);
    }

    public static Bitmap createBitmapFromView(View view) {
        if (view instanceof ImageView) {
            Drawable drawable = ((ImageView) view).getDrawable();
            if (drawable != null && drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }
        }
        view.clearFocus();
        Bitmap bitmap = createBitmapSafely(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888, 1);
        if (bitmap != null) {
            synchronized (sCanvas) {
                Canvas canvas = sCanvas;
                canvas.setBitmap(bitmap);
                view.draw(canvas);
                canvas.setBitmap(null);
            }
        }
        return bitmap;
    }

    public static Bitmap createBitmapSafely(int width, int height, Bitmap.Config config, int retryCount) {
        try {
            return Bitmap.createBitmap(width, height, config);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (retryCount > 0) {
                System.gc();
                return createBitmapSafely(width, height, config, retryCount - 1);
            }
            return null;
        }
    }

    public static int getScreenSizeY(Context context) {
        WindowManager wm = (WindowManager)context.getSystemService(WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static int getScreenSizeX(Context context) {
        WindowManager wm = (WindowManager)context.getSystemService(WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static Activity scanForActivity(Context context) {
        return context == null?null:(context instanceof Activity?(Activity)context:(context instanceof ContextWrapper ?scanForActivity(((ContextWrapper)context).getBaseContext()):null));
    }

    public static AppCompatActivity getAppCompActivity(Context context) {
        return context == null?null:(context instanceof AppCompatActivity?(AppCompatActivity)context:(context instanceof ContextThemeWrapper ?getAppCompActivity(((ContextThemeWrapper)context).getBaseContext()):null));
    }

    @SuppressLint("RestrictedApi")
    public static void showActionBar(Context context) {
        ActionBar ab = getAppCompActivity(context).getSupportActionBar();
        if(ab != null) {
            ab.setShowHideAnimationEnabled(false);
            ab.show();
        }

        scanForActivity(context).getWindow().clearFlags(1024);
    }

    @SuppressLint("RestrictedApi")
    public static void hideActionBar(Context context) {
        ActionBar ab = getAppCompActivity(context).getSupportActionBar();
        if(ab != null) {
            ab.setShowHideAnimationEnabled(false);
            ab.hide();
        }

        scanForActivity(context).getWindow().setFlags(1024, 1024);
    }

    public static void disableClipOnParents(View v, boolean isShow) {
        if(v.getParent() != null) {
            if(v instanceof ViewGroup) {
                ((ViewGroup)v).setClipChildren(isShow);
            }

            if(v.getParent() instanceof View) {
                disableClipOnParents((View)v.getParent(), isShow);
            }

        }
    }
}
