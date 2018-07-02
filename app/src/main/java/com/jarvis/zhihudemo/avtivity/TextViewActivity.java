package com.jarvis.zhihudemo.avtivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.view.annotations.ContentView;
import com.jarvis.zhihudemo.view.annotations.ViewInject;
import com.jarvis.zhihudemo.view.annotations.ViewInjectUtils;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/12/27 下午3:07
 * @changeRecord [修改记录] <br/>
 */

@ContentView(R.layout.activity_textview)
public class TextViewActivity extends BaseActivity {

    @ViewInject(R.id.back_up_title)
    TextView tv;
    @ViewInject(R.id.btn)
    Button brn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);

        brn.setOnClickListener(view -> {
            tv.setMaxLines(2);
        });
    }
}
