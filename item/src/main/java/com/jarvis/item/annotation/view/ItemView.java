package com.jarvis.item.annotation.view;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/3/30 下午3:01
 * @changeRecord [修改记录] <br/>
 */

public interface ItemView<T> {

    @NonNull
    View onCreateItemView(
            @NonNull LayoutInflater inflater,
            @NonNull ViewGroup parent);

    void onAttachToWindow();

    void onBindData(@NonNull T data);

}
