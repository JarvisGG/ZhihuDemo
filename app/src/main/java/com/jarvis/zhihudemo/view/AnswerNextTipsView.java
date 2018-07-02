package com.jarvis.zhihudemo.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title Futureve
 * @description 该类主要功能描述
 * @create 2018/3/5 下午12:41
 * @changeRecord [修改记录] <br/>
 */

public class AnswerNextTipsView extends FrameLayout {

    private LayoutInflater mInflater;

    private View mContainerView;

    public AnswerNextTipsView(Context pContext) {
        super(pContext);
        initView();
    }

    public AnswerNextTipsView(Context pContext, AttributeSet pAttributeSet) {
        super(pContext, pAttributeSet);
        initView();
    }

    public AnswerNextTipsView(Context pContext, AttributeSet pAttributeSet, int pDefaultStyle) {
        super(pContext, pAttributeSet, pDefaultStyle);
        initView();
    }

    private void initView() {

        this.setClipChildren(false);
        this.setClipToPadding(false);
        mInflater = LayoutInflater.from(getContext());
        mContainerView = mInflater.inflate(R.layout.widget_answer_tips, null, false);
        this.addView(mContainerView);

        this.setBackground(new ShapeDrawable(new RectShape() {
            @Override
            public void draw(Canvas canvas, Paint paint) {
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(ContextCompat.getColor(getContext(), R.color.BL03));
                Path path = buildContainerPath(rect());
                canvas.drawPath(path, paint);
            }

            @Override
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public void getOutline(Outline outline) {
                Path path = buildContainerPath(rect());
                if (path.isConvex()) {
                    outline.setConvexPath(path);
                } else {
                    super.getOutline(outline);
                }
            }
        }));

    }

    private ArrowPath buildArrowPath(RectF rectF) {
        ArrowPath path = new ArrowPath();
        path.lineMoveTo(rectF.left, rectF.top)
                .linePathTo(rectF.left, rectF.bottom)
                .linePathTo(rectF.right, (rectF.top + rectF.bottom) / 2);
        path.close();
        return path;
    }

    private ArrowPath buildContainerPath(RectF rectF) {
        float radius = ExplosionUtils.dp2Px(4);
        float diameter = radius * 2.0F;
        ArrowPath path = new ArrowPath();
        path.lineMoveTo(rectF.left, (rectF.top + rectF.bottom) / 2.0F)
                .lineMoveTo(rectF.left, rectF.top + radius)
                .lineArcTo(rectF.left, rectF.top, rectF.left + diameter, rectF.top + diameter, 180.0f, 90.0f)
                .linePathTo(rectF.right - radius - (rectF.bottom - rectF.top - diameter) / 4, rectF.top)
                .lineArcTo(rectF.right - diameter - (rectF.bottom - rectF.top - diameter) / 4, rectF.top, rectF.right - (rectF.bottom - rectF.top - diameter) / 4, rectF.top + diameter, 270, 90)

//                .linePathTo(rectF.right - (rectF.bottom - rectF.top - diameter) / 4, rectF.bottom - radius)
                .linePathTo(rectF.right - (rectF.bottom - rectF.top - diameter) / 4, (rectF.bottom - rectF.top - diameter) / 4 + rectF.top + radius)
                .linePathTo(rectF.right, (rectF.top + rectF.bottom) / 2)
                .linePathTo(rectF.right - (rectF.bottom - rectF.top - diameter) / 4, rectF.bottom - radius - (rectF.bottom - rectF.top - diameter) / 4)


                .lineArcTo(rectF.right - diameter - (rectF.bottom - rectF.top - diameter) / 4, rectF.bottom - diameter, rectF.right - (rectF.bottom - rectF.top - diameter) / 4, rectF.bottom, 0, 90)
                .linePathTo(rectF.left + radius, rectF.bottom)
                .lineArcTo(rectF.left, rectF.bottom - diameter, rectF.left + diameter, rectF.bottom, 90, 90);

        path.close();

        this.setPadding(
                ExplosionUtils.dp2Px(8),
                ExplosionUtils.dp2Px(10),
                (int) (ExplosionUtils.dp2Px(8) + (rectF.bottom - rectF.top - diameter) / 4),
                ExplosionUtils.dp2Px(10));

        return path;
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    private class ArrowPath extends Path {
        @Override
        public void moveTo(float x, float y) {
            super.moveTo(x, y);
        }

        public ArrowPath lineMoveTo(float x, float y) {
            this.moveTo(x, y);
            return this;
        }

        public ArrowPath linePathTo(float x, float y) {
            this.lineTo(x, y);
            return this;
        }

        public ArrowPath lineArcTo(RectF oval, float startAngle, float sweepAngle) {
            this.arcTo(oval, startAngle, sweepAngle);
            return this;
        }

        public ArrowPath lineArcTo(float left, float top, float right, float bottom,  float startAngle, float sweepAngle) {
            RectF oval = new RectF(left, top, right, bottom);
            this.arcTo(oval, startAngle, sweepAngle);
            return this;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(getMeasuredWidth(),
                getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

}
