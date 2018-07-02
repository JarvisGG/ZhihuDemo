package com.jarvis.zhihudemo.avtivity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/28 下午4:06
 * @changeRecord [修改记录] <br/>
 */

public class AnswerSkeletionActivity extends BaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_skeletion);
    }
}
