package com.jarvis.item.annotation.Presenter;

import com.jarvis.item.view.IPresenter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * @author yyf @ Zhihu Inc.
 * @since 07-09-2018
 */
@Retention(CLASS)
@Target(TYPE)
public @interface Presenter {
    Class<? extends IPresenter>[] value();
}
