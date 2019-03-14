package com.jarvis.zhihudemo.view1;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jarvis.zhihudemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yyf @ Zhihu Inc.
 * @since 03-13-2019
 */
public class NumberCountView extends LinearLayout {

    private List<NumberView> numberViews = new ArrayList<>();

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
        String[] currents = current.split(",");
        String[] targets = target.split(",");
        int length = Math.max(currents.length, currents.length);
        for (int i = 0; i < length; i++) {
            int subLength = Math.max(currents[i].length(), targets[i].length());
            for (int j = 0; j < subLength; j++) {
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

            }
        }
    }

    private void addNumberView(String current, String target, boolean hidenStart, boolean hidenEnd) {
        NumberView numberView = new NumberView(getContext());
        numberView.setNumber(Integer.valueOf(current), Integer.valueOf(target));
        numberView.isHidenStartZero(hidenStart);
        numberView.isHidenEndZero(hidenEnd);
        numberView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(numberView, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        numberViews.add(numberView);
    }

    private void addPointView() {
        TextView view = new TextView(getContext());
        view.setText(",");
        view.setTextColor(getContext().getColor(R.color.BL03));
        view.setTextSize(30);
//        addView(view, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
            });
            animator.setStartDelay(j * delay);
            animator.start();
            j++;
        }

//        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
//        animator.setDuration(1000);
//        animator.addUpdateListener(animation -> {
//                    float persent = (float) animation.getAnimatedValue();
//                    for (NumberView numberView : numberViews) {
//                        numberView.notifyPersent(persent);
//                    }
//                });
//        animator.start();
//        for (NumberView numberView : numberViews) {
//            numberView.notifyPersent(1);
//        }

    }

    public void reset() {
        for (int i = numberViews.size() - 1; i >= 0; i--) {
            NumberView numberView = numberViews.get(i);
            numberView.notifyPersent(0);
        }
    }

    public void setAlphaRange(float origin, float middle) {
        for (int i = numberViews.size() - 1; i >= 0; i--) {
            NumberView numberView = numberViews.get(i);
            numberView.setAlphaRange(origin, middle);
        }
    }
}
