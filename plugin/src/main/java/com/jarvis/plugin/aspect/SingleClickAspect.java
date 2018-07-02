package com.jarvis.plugin.aspect;

import android.util.Log;
import android.view.View;

import org.android10.gintonic.R;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Calendar;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/4 上午11:50
 * @changeRecord [修改记录] <br/>
 */

@Aspect
public class SingleClickAspect {
    static int TIME_TAG = R.id.click_time;
    public static final int MIN_CLICK_DELAY_TIME = 600;



}
