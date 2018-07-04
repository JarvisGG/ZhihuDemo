package com.jarvis.zhihudemo.widgets;

/**
 * @author yyf @ Zhihu Inc.
 * @since 07-03-2018
 */
public final class FloatValueHolder {
    private float mValue = 0.0f;
    /**
     * Constructs a holder for a float value that is initialized to 0.
     */
    public FloatValueHolder() {
    }
    /**
     * Constructs a holder for a float value that is initialized to the input value.
     *
     * @param value the value to initialize the value held in the FloatValueHolder
     */
    public FloatValueHolder(float value) {
        setValue(value);
    }
    /**
     * Sets the value held in the FloatValueHolder instance.
     *
     * @param value float value held in the FloatValueHolder instance
     */
    public void setValue(float value) {
        mValue = value;
    }
    /**
     * Returns the float value held in the FloatValueHolder instance.
     *
     * @return float value held in the FloatValueHolder instance
     */
    public float getValue() {
        return mValue;
    }
}