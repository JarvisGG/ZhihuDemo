package com.jarvis.zhihudemo.view1;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.widgets.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yyf @ Zhihu Inc.
 * @since 03-13-2019
 */
public class NumberCountView extends LinearLayout {

    private List<NumberView> numberViews = new ArrayList<>();
    private List<PointView> pointViews = new ArrayList<>();
    private List<View> tatalViews = new ArrayList<>();

    private int duration;

    private int delay;

    public NumberCountView(Context context) {
        super(context);
        init();
    }

    public NumberCountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NumberCountView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
    }

    public void setNumber(String current, String target) {
        processor(current, target);
    }

    public void setNumber(int current, int target) {
        processor(String.valueOf(current), String.valueOf(target));
    }

    public void setInfo(int duration, int delay) {
        this.duration = duration;
        this.delay = delay;
    }

    private void processor(String current, String target) {
        removeAllViews();
        boolean hidenStart = true;
        boolean hidenEnd = true;

        int op = current.length() - target.length();
        for(int i = 0; i < Math.abs(op); i ++) {
            if (op > 0) {
                target = "0" + target;
            } else if (op < 0) {
                current = "0" + current;
            }
        }
        String currentTemp = "";
        String targetTemp = "";
        int j = 0;
        for (int i = current.length() - 1; i >= 0; i--) {
            currentTemp = currentTemp + current.charAt(i);
            targetTemp = targetTemp + target.charAt(i);
            j++;
            if (j == 3) {
                j = 0;
                currentTemp = currentTemp + ",";
                targetTemp = targetTemp + ",";
            }
        }
        current = new StringBuilder(currentTemp).reverse().toString();
        target = new StringBuilder(targetTemp).reverse().toString();

        String[] currents = current.split(",");
        String[] targets = target.split(",");
        int length = Math.max(currents.length, currents.length);
        for (int i = 0; i < length; i++) {
            int subLength = Math.max(currents[i].length(), targets[i].length());
            for (j = 0; j < subLength; j++) {
                String c = String.valueOf(currents[i].charAt(j));
                String t = String.valueOf(targets[i].charAt(j));
                if (!"0".equals(c)) {
                    hidenStart = false;
                }
                if (!"0".equals(t)) {
                    hidenEnd = false;
                }
                addNumberView(c, t, hidenStart, hidenEnd);
            }
            if (i < length - 1) {
                addPointView();
            }
        }

        checkPointVisiable();

    }

    private void addNumberView(String current, String target, boolean hidenStart, boolean hidenEnd) {
        NumberView numberView = new NumberView(getContext());
        numberView.setNumber(Integer.valueOf(current), Integer.valueOf(target));
        numberView.isHidenStartZero(hidenStart);
        numberView.isHidenEndZero(hidenEnd);
        numberView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(numberView, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        numberViews.add(numberView);
        tatalViews.add(numberView);
    }

    private void addPointView() {
        PointView view = new PointView(getContext());
        view.setVisibility(INVISIBLE);
        addView(view, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        pointViews.add(view);
        tatalViews.add(view);
    }

    private void checkPointVisiable() {
        checkPointVisiable(false);
    }

    private void checkPointVisiable(boolean isReset) {
        boolean isStartShowPoint = false;
        for (int i = 0; i < tatalViews.size(); i++) {
            if (tatalViews.get(i) instanceof NumberView) {
                if (isReset) {
                    if (((NumberView) tatalViews.get(i)).getStart() > 0) {
                        isStartShowPoint = true;
                    }
                } else {
                    if (((NumberView) tatalViews.get(i)).getCurrent() > 0) {
                        isStartShowPoint = true;
                    }
                }
            }
            if (tatalViews.get(i) instanceof PointView) {
                if (isStartShowPoint) {
                    tatalViews.get(i).setVisibility(VISIBLE);
                } else {
                    tatalViews.get(i).setVisibility(INVISIBLE);
                }
            }
        }
    }

    public void start() {
        int j = 0;
        for (int i = numberViews.size() - 1; i >= 0; i--) {
            NumberView numberView = numberViews.get(i);
            ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
            animator.setDuration(duration);
            animator.addUpdateListener(animation -> {
                float persent = (float) animation.getAnimatedValue();
                numberView.notifyPersent(persent);
                checkPointVisiable();
            });
            animator.setStartDelay(j * delay);
            animator.start();
            j++;
        }
    }

    public void reset() {
        for (int i = numberViews.size() - 1; i >= 0; i--) {
            NumberView numberView = numberViews.get(i);
            numberView.notifyPersent(0);
        }
        checkPointVisiable(true);
    }

    public void setAlphaRange(float origin, float middle) {
        for (int i = numberViews.size() - 1; i >= 0; i--) {
            NumberView numberView = numberViews.get(i);
            numberView.setAlphaRange(origin, middle);
        }
    }

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

        /**
         * 文字滚动中 alpha 变化
         */
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
            this.current = start;
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

        public int getCurrent() {
            return current;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }

    class PointView extends View {
        private Paint paint;
        private Rect textBounds = new Rect();
        private float textCenterX = -1;
        private float textCenterY = -1;

        public PointView(Context context) {
            super(context);
            init();
        }

        public PointView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public PointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init() {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(DisplayUtils.spToPixel(getContext(), 30));
            paint.setColor(getContext().getColor(R.color.BL03));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int sizeWidth = measureSize(widthMeasureSpec, -1);
            int sizeHeight = measureSize(heightMeasureSpec, 1);
            setMeasuredDimension(sizeWidth, sizeHeight);
            textCenterX = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) >>> 1;
            textCenterY = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) >>> 1;
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
            textCenterY = textCenterY + distance;
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            canvas.drawText(",", textCenterX, textCenterY, paint);
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
                    result + getPaddingLeft() + getPaddingRight();
        }

    }
}
