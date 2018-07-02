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

public class ZHViewPagerInfo {
    public int oriection;
    /**
     * 缓存数
     */
    public int offsetScreenPageLimit;

    /**
     * 向上切换拖动距离
     */
    public float topSwitchMultiple;

    /**
     * 向上切换拖动距离
     */
    public float bottomSwitchMultiple;

    /**
     * 切换动画插值机器
     */
    public Interpolator interpolator;

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

    /**
     * 用户允许top拖拽距离
     */
    public float topLimitOffset;

    /**
     * 用户允许bottom拖拽距离
     */
    public float bottomLimitOffset;

    /**
     * 阻尼差值
     */
    public float topFator;

    /**
     * 阻尼差值
     */
    public float bottomFactor;

    public ZHViewPagerInfo(Builder builder) {
        this.oriection = builder.oriection;
        this.offsetScreenPageLimit = builder.offsetScreenPageLimit;
        this.topSwitchMultiple = builder.topSwitchMultiple;
        this.bottomSwitchMultiple = builder.bottomSwitchMultiple;
        this.interpolator = builder.interpolator;
        this.topFator = builder.topFator;
        this.bottomFactor = builder.bottomFactor;
        this.time = builder.time;
        this.preOffset = builder.preOffset;
        this.nextOffset = builder.nextOffset;
        this.topLimitOffset = builder.topLimitOffset;
        this.bottomLimitOffset = builder.bottomLimitOffset;
    }

    public static class Builder {
        private int oriection;
        private int offsetScreenPageLimit;
        private Interpolator interpolator;
        public float topFator;
        public float bottomFactor;
        public int time;
        public int preOffset;
        public int nextOffset;
        public float topLimitOffset;
        public float bottomLimitOffset;
        public float topSwitchMultiple;
        public float bottomSwitchMultiple;

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

        public Builder setTopSwitchMultiple(float topSwitchMultiple) {
            this.topSwitchMultiple = topSwitchMultiple;
            return this;
        }

        public Builder setBottomSwitchMultiple(float bottomSwitchMultiple) {
            this.bottomSwitchMultiple = bottomSwitchMultiple;
            return this;
        }

        public Builder setTopFator(float topFator) {
            this.topFator = topFator;
            return this;
        }

        public Builder setBottomFactor(float bottomFactor) {
            this.bottomFactor = bottomFactor;
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

        public Builder setTopLimitOffset(float topLimitOffset) {
            this.topLimitOffset = topLimitOffset;
            return this;
        }

        public Builder setBottomLimitOffset(float bottomLimitOffset) {
            this.bottomLimitOffset = bottomLimitOffset;
            return this;
        }

        public ZHViewPagerInfo builder() {
            return new ZHViewPagerInfo(this);
        }
    }
}
