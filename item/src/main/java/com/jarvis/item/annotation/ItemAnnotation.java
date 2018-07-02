package com.jarvis.item.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/3/26 下午3:47
 * @changeRecord [修改记录] <br/>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ItemAnnotation {
    String name() default "undefined";
    String text() default "";
}
