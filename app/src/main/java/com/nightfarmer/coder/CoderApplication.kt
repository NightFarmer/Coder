package com.nightfarmer.coder

import android.app.Application
import android.content.Context
import com.morgoo.droidplugin.PluginApplication
import com.morgoo.droidplugin.PluginHelper

/**
 * Created by zhangfan on 16-9-7.
 */
class CoderApplication : Application() {

    private val TAG = PluginApplication::class.java.simpleName

    override fun onCreate() {
        super.onCreate()
        PluginHelper.getInstance().applicationOnCreate(baseContext)
        ColorTheme.init(this,R.style.Theme_Default)
    }


    override fun attachBaseContext(base: Context) {
        PluginHelper.getInstance().applicationAttachBaseContext(base)
        super.attachBaseContext(base)
    }
}