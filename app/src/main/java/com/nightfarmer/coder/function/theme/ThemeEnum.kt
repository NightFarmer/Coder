package com.nightfarmer.coder.function.theme

import android.support.annotation.ColorRes
import android.support.annotation.StyleRes
import com.nightfarmer.coder.R

/**
 * Created by zhangfan on 16-9-7.
 */
enum class ThemeEnum(val title: String, @StyleRes val res: Int, @ColorRes val primaryColor: Int) {

    Default("默认", R.style.Theme_Default, R.color.colorPrimary),
    Purple("紫色", R.style.Theme_Purple, R.color.colorPrimary_Purple),
    Blue("蓝色", R.style.Theme_Blue, R.color.colorPrimary_Blue),
    Yellow("黄色", R.style.Theme_Yellow, R.color.colorPrimary_Yellow),
    Red("红色", R.style.Theme_Red, R.color.colorPrimary_Red),

}