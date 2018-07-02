package com.jarvis.zhihudemo.avtivity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.view.nested.NestScrollWebView;
import com.jarvis.zhihudemo.view.nested.NestScrollingViewPager;
import com.jarvis.zhihudemo.view.widget.ZHViewPagerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/3/16 下午6:40
 * @changeRecord [修改记录] <br/>
 */

public class NestedScrollActivity extends BaseActivity {

    NestScrollingViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested);

        viewPager = findViewById(R.id.main_view);

        ZHViewPagerInfo pagerInfo = new ZHViewPagerInfo.Builder()
                .setOffsetScreenPageLimit(10)
                .setTopLimitOffset(0.114f)
                .setBottomLimitOffset(0.15f)
//                .setInterpolator(new OvershootInterpolator(0.8f))
                .setInterpolator(new DecelerateInterpolator(2))
                .setTopFator(0.45f)
                .setBottomFactor(0.8f)
                .setTime(300)
                .setNextOffset(0)
                .setPreOffset(0)
                .builder();
//        viewPager.setAdapter(new TestFragmentAdapter(getSupportFragmentManager()));
        viewPager.setAdapter(new GalleryViewPagerActivity.MyPagerAdapter(createViewList()));
        viewPager.bind(pagerInfo);

    }

    private List<View> createViewList() {
        List<View> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            int ranColor = 0xff000000 | random.nextInt(0x00ffffff);

            NestScrollWebView webView = new NestScrollWebView(this);
            webView.loadUrl("http://news.qq.com/");
            webView.setTag(i);
            list.add(webView);

//            View view = LayoutInflater.from(this).inflate(R.layout.pager_view, null);
//            view.findViewById(R.id.container_view).setBackgroundColor(ranColor);
//            TextView textView = (TextView) view.findViewById(R.id.text1);
//            textView.setText("ViewPager. \t\t" + i);
//            textView.setTextColor(Color.WHITE);
//            view.setTag(i);
//            list.add(view);
        }
        return list;
    }
}
