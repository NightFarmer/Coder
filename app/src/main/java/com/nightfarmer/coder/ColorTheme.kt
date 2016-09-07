package com.nightfarmer.coder

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.AttrRes
import android.support.annotation.StyleRes
import android.util.TypedValue
import rx.Observable

/**
 * Created by zhangfan on 16-9-7.
 */
object ColorTheme : Application.ActivityLifecycleCallbacks {

    var activityList = arrayListOf<Activity>()
    val ThemeChacheKey = "themeCacheData"

    @StyleRes
    var style: Int = 0
        private set

    fun setStyle(activity: Activity, @StyleRes style: Int, data: Bundle? = null) {
        ColorTheme.style = style
        Observable.from(activityList)
                .skipLast(1)
                .subscribe {
                    it.recreate()
                }
        val intent = Intent(activity, activity.javaClass)
        intent.putExtra(ThemeChacheKey, data)
        activity.startActivity(intent)
//        activity.overridePendingTransition(
//                R.animator.alpha_int, R.animator.alpha_out);
        activity.overridePendingTransition(0, 0);
        activity.finish()
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        activity?.let {
            activityList.add(activity)
            val styleRes = ColorTheme.style
            activity.setTheme(styleRes)
        }
    }

    override fun onActivityStarted(activity: Activity?) {

    }

    override fun onActivityDestroyed(activity: Activity?) {
        activity?.let {
            activityList.remove(activity)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

    }

    override fun onActivityStopped(activity: Activity?) {

    }

    override fun onActivityResumed(activity: Activity?) {

    }

    override fun onActivityPaused(activity: Activity?) {

    }

    fun init(app: Application, style: Int = 0) {
        app.registerActivityLifecycleCallbacks(this)
        this.style = style
    }

    fun getThemeColor(context: Context, @AttrRes attr: Int): Int {
        val typedValue = TypedValue();
        context.theme.resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }

    fun getThemeColorRes(context: Context, @AttrRes attr: Int): Int {
        val typedValue = TypedValue();
        context.theme.resolveAttribute(attr, typedValue, true);
        return typedValue.resourceId;
    }
}