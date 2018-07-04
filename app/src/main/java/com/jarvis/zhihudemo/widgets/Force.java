package com.jarvis.zhihudemo.widgets;

/**
 * @author yyf @ Zhihu Inc.
 * @since 07-03-2018
 */
interface Force {
    // Acceleration based on position.
    float getAcceleration(float position, float velocity);
    boolean isAtEquilibrium(float value, float velocity);
}
