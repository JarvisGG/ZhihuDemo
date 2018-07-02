package com.jarvis.zhihudemo.view1;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.WebView;

/**
 * @author yyf @ Zhihu Inc.
 * @since 06-27-2018
 */
public class CustiomWebView extends WebView {

    private float mLatestTouchDownY;
    private float mTouchSlop;

    public CustiomWebView(Context context) {
        super(context);
    }

    public CustiomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustiomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustiomWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CustiomWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean res = super.dispatchTouchEvent(event);
        Log.e("webView --> ", "dispatchTouchEvent -> " + res);
        return res;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean res = super.onInterceptTouchEvent(ev);
        Log.e("webView --> ", "onInterceptTouchEvent -> " + res);
        return res;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean res = super.onTouchEvent(event);
        Log.e("webView --> ", "onTouchEvent -> " + res);
        return res;
    }
}
