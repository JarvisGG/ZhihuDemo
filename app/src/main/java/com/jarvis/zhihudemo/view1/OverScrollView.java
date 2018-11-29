package com.jarvis.zhihudemo.view1;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

/**
 * @author yyf @ Zhihu Inc.
 * @since 07-12-2018
 */
public class OverScrollView extends ScrollView {

    private int mTopOverScrollDistance = 150;
    private int mBottomOverScrollDistance = 90;

    private boolean mTopOverScrollEnable;
    private boolean mBottomOverScrollEnable;

    public OverScrollView(Context context) {
        super(context);
    }

    public OverScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OverScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OverScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected boolean overScrollBy(
            int deltaX, int deltaY,
            int scrollX, int scrollY,
            int scrollRangeX, int scrollRangeY,
            int maxOverScrollX, int maxOverScrollY,
            boolean isTouchEvent) {
        Log.e("overScrollBy -----> ", " deltaY : " + deltaY + " scrollY : " + scrollY + " scrollRangeY : " +
                scrollRangeY);
        return super.overScrollBy(
                deltaX, deltaY, scrollX, scrollY,
                scrollRangeX, scrollRangeY, maxOverScrollX, 150,
                isTouchEvent);
    }
}

