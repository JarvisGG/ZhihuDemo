package com.jarvis.zhihudemo.view1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.widgets.DisplayUtils;

/**
 * @author yyf @ Zhihu Inc.
 * @since 03-13-2019
 */
public class NumberView extends View {

    private Paint paint;

    private int start = 0;
    private int end = 0;
    private int current = 0;
    private int next = 0;

    private float persent = 0.0f;
    private float textCenterX = -1;
    private float textCenterY = -1;

    private int totalScrollRange = 0;
    private int step = 0;

    private float stepPersent = 0.0f;

    private boolean isHidenStartZero = false;
    private boolean isHidenEndZero = false;

    private Rect textBounds = new Rect();




    private float origin = 1;
    private float middle = 1;




    public NumberView(Context context) {
        super(context);
        init();
    }

    public NumberView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(DisplayUtils.spToPixel(getContext(), 30));
        paint.setColor(getContext().getColor(R.color.BL03));
    }

    public void isHidenStartZero(boolean hiden) {
        isHidenStartZero = hiden;
    }

    public void isHidenEndZero(boolean hiden) {
        isHidenEndZero = hiden;
    }

    public void setNumber(int start, int end) {
        this.start = start;
        this.end = end;
        step = end < start ? 10 + end - start : end - start;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = measureSize(widthMeasureSpec, -1);
        int sizeHeight = measureSize(heightMeasureSpec, 1);
        setMeasuredDimension(sizeWidth, sizeHeight);

        textCenterX = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) >>> 1;
        textCenterY = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) >>> 1;

        totalScrollRange = step * getMeasuredHeight();

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        textCenterY = textCenterY + distance;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        calculateNumber();
        drawCurrent(canvas);
        drawNext(canvas);
    }

    public void notifyPersent(float persent) {
        this.persent = persent;
        invalidate();
    }

    private void calculateNumber() {
        float currentScrollY = totalScrollRange * persent;
        current = (int) (currentScrollY / getMeasuredHeight()) + start;
        current %= 10;
        next = (current + 1) % 10;
        stepPersent = (currentScrollY % getMeasuredHeight()) / getMeasuredHeight();
        if (current == end && isHidenEndZero) {
            isHidenStartZero = true;
        }
    }

    private void drawCurrent(Canvas canvas) {
        if (isHidenStartZero && current == 0) {
            return;
        }

        float alphaRage = origin - middle;
        if (alphaRage != 0) {
            paint.setAlpha((int) (Math.abs(alphaRage - 2 * stepPersent) * 255));
        }

        canvas.drawText(String.valueOf(current), textCenterX, textCenterY - (stepPersent * getMeasuredHeight()), paint);
    }

    private void drawNext(Canvas canvas) {
        if (isHidenEndZero && next == 0) {
            return;
        }

        float alphaRage = origin - middle;
        if (alphaRage != 0) {
            paint.setAlpha((int) (Math.abs(alphaRage - 2 * stepPersent) * 255));
        }

        canvas.drawText(String.valueOf(next), textCenterX, textCenterY + ((1 - stepPersent) * getMeasuredHeight()), paint);
    }

    private int measureSize(int measureSpec, int model) {
        int mode = MeasureSpec.getMode(measureSpec);
        int val = MeasureSpec.getSize(measureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = val;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                paint.getTextBounds(String.valueOf("0"), 0, 1, textBounds);
                result = (model == 1 ? textBounds.height() : textBounds.width());
                break;
            default:
                break;
        }
        result = mode == MeasureSpec.AT_MOST ? Math.min(result, val) : result;
        return model == 1 ? result + getPaddingTop() + getPaddingBottom() + DisplayUtils.dpToPixel(getContext(), 4) :
                result + getPaddingLeft() + getPaddingRight() + DisplayUtils.dpToPixel(getContext(), 2);
    }

    public void setAlphaRange(float origin, float middle) {
        this.origin = origin;
        this.middle = middle;
    }
}
