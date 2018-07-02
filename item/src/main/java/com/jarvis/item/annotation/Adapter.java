package com.jarvis.item.annotation;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/3/30 下午2:47
 * @changeRecord [修改记录] <br/>
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Adapter {


    @interface Data {
        Class value();
    }

//    @interface View {
//        Class<? extends android.view.View>
//    }
}
