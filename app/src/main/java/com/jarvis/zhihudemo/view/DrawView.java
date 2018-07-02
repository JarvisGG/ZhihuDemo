//package com.jarvis.zhihudemo.view;
//
//import android.content.Context;
//import android.content.res.ColorStateList;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.BitmapShader;
//import android.graphics.BlurMaskFilter;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.EmbossMaskFilter;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.graphics.Shader;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.support.annotation.ColorInt;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//import android.util.TypedValue;
//import android.view.View;
//import android.widget.ImageView;
//
//import com.jarvis.zhihudemo.R;
//import com.jarvis.zhihudemo.view.shadow.RenderingMode;
//import com.jarvis.zhihudemo.view.shadow.ShadowShape;
//import com.jarvis.zhihudemo.view.shadow.ShadowView;
//
///**
// * @author Jarvis @ Zhihu Inc.
// * @version 1.0
// * @title ZhihuDemo
// * @description 该类主要功能描述
// * @create 2017/12/13 下午11:03
// * @changeRecord [修改记录] <br/>
// */
//
//public class DrawView extends View implements
//        ShadowView {
//
//    private RectF mRect;
//    private Paint mShadowPaint = new Paint();
//
//    public DrawView(Context context) {
//        super(context, null);
//    }
//
//    public DrawView(Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        mRect = new RectF(0, 0, getWidth(), getHeight());
//        setLayerType(LAYER_TYPE_SOFTWARE , null);
//    }
//
//    public void setShadowColor(float shadowRadius, float y, float width, float height, float rectRadius, @ColorInt int shadowColor) {
//        mShadowPaint.setShadowLayer(shadowRadius, 0, y, shadowColor);
//        mRect = new RectF(50f, 50f,
//                getWidth() - 50f, getHeight() - 50f);
//
//        invalidate();
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
////        canvas.drawRoundRect(mRect, 16,16, mShadowPaint);
////        setShadowColor(50f, 7f, 160f, 228, 16, Color.BLUE);
//    }
//
//    public int dp2px(int dpVal) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//                dpVal, getResources().getDisplayMetrics());
//    }
//
//    @Override
//    public void setRenderingMode(RenderingMode mode) {
//
//    }
//
//    @Override
//    public RenderingMode getRenderingMode() {
//        return null;
//    }
//
//    @Override
//    public ShadowShape getShadowShape() {
//        return null;
//    }
//
//    @Override
//    public boolean hasShadow() {
//        return false;
//    }
//
//    @Override
//    public void drawShadow(Canvas canvas) {
//
//    }
//
//    @Override
//    public void setElevationShadowColor(ColorStateList shadowColor) {
//
//    }
//
//    @Override
//    public void setElevationShadowColor(int color) {
//
//    }
//
//    @Override
//    public ColorStateList getElevationShadowColor() {
//        return null;
//    }
//}
