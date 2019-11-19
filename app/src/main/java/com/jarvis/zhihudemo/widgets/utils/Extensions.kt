package com.jarvis.zhihudemo.widgets.utils

import android.content.res.Resources

/**
 * @author yyf
 * @since 11-19-2019
 */
/**
 * UI
 */
val Number.dp2px get() = (toInt() * Resources.getSystem().displayMetrics.density).toInt()
val Number.px2dp get() = (toInt() / Resources.getSystem().displayMetrics.density).toInt()