package com.jarvis.zhihudemo;

import android.app.Application;
import android.view.WindowManager;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/11/15 下午6:29
 * @changeRecord [修改记录] <br/>
 */

public class App extends Application {
    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

    public WindowManager.LayoutParams getMywmParams(){
        return wmParams;
    }

    public static App INSTANCE;

    {
        INSTANCE = this;
    }
}
