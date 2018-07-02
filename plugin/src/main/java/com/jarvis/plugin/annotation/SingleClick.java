package com.jarvis.plugin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/4 上午11:19
 * @changeRecord [修改记录] <br/>
 */

@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.CONSTRUCTOR, ElementType.METHOD })
public @interface SingleClick {

}
