package com.jarvis.zhihudemo.view.widget;

import android.view.animation.Interpolator;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/19 下午5:49
 * @changeRecord [修改记录] <br/>
 */

public class ViewPagerInfo {
    /**
     * 方向
     */
    public int oriection;
    /**
     * 缓存数
     */
    public int offsetScreenPageLimit;
    /**
     * 最小切换拖动距离
     */
    public float switchMultiple;
    /**
     * 切换动画插值机器
     */
    public Interpolator interpolator;
    /**
     * 阻尼差值
     */
    public float fator;
    /**
     * 切换时间
     */
    public int time;

    /**
     * 上一个漏出距离
     * @param builder
     */
    public int preOffset;

    /**
     * 下一个漏出距离
     * @param builder
     */
    public int nextOffset;

    public ViewPagerInfo(Builder builder) {
        this.oriection = builder.oriection;
        this.offsetScreenPageLimit = builder.offsetScreenPageLimit;
        this.switchMultiple = builder.switchMultiple;
        this.interpolator = builder.interpolator;
        this.fator = builder.fator;
        this.time = builder.time;
        this.preOffset = builder.preOffset;
        this.nextOffset = builder.nextOffset;
    }

    public static class Builder {
        private int oriection;
        private int offsetScreenPageLimit;
        private float switchMultiple;
        private Interpolator interpolator;
        public float fator;
        public int time;
        public int preOffset;
        public int nextOffset;

        public Builder setOriection(int oriection) {
            this.oriection = oriection;
            return this;
        }

        public Builder setOffsetScreenPageLimit(int offsetScreenPageLimit) {
            this.offsetScreenPageLimit = offsetScreenPageLimit;
            return this;
        }

        public Builder setInterpolator(Interpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public Builder setSwitchMultiple(float switchMultiple) {
            this.switchMultiple = switchMultiple;
            return this;
        }

        public Builder setFator(float fator) {
            this.fator = fator;
            return this;
        }

        public Builder setTime(int time) {
            this.time = time;
            return this;
        }

        public Builder setPreOffset(int preOffset) {
            this.preOffset = preOffset;
            return this;
        }

        public Builder setNextOffset(int nextOffset) {
            this.nextOffset = nextOffset;
            return this;
        }

        public ViewPagerInfo builder() {
            return new ViewPagerInfo(this);
        }
    }
}
