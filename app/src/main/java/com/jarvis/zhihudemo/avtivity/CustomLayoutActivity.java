package com.jarvis.zhihudemo.avtivity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/3/21 下午5:30
 * @changeRecord [修改记录] <br/>
 */

public class CustomLayoutActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
    }
}
