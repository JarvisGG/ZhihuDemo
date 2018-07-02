package com.jarvis.zhihudemo.view.widget;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/25 下午7:08
 * @changeRecord [修改记录] <br/>
 */

public class ZHDragViewInfo {

    /**
     * 拖拽百分比
     */
    public float mMiddlePrecent;

    ZHDragViewInfo(Builder builder) {
        this.mMiddlePrecent = builder.middlePrecent;
    }

    public static class Builder {
        float middlePrecent;

        public Builder setMiddlerPrecent(float middlePrecent) {
            this.middlePrecent = middlePrecent;
            return this;
        }

        public ZHDragViewInfo builder() {
            return new ZHDragViewInfo(this);
        }
    }
}
