package com.jarvis.zhihudemo.view;

import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/19 下午3:46
 * @changeRecord [修改记录] <br/>
 */

public final class VerticalViewPagerCompat {
    private VerticalViewPagerCompat() {}

    public static void setDataSetObserver(PagerAdapter adapter, DataSetObserver observer) {
        adapter.registerDataSetObserver(observer);
    }
}
