package com.nightfarmer.coder.bean

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import com.morgoo.droidplugin.pm.PluginManager
import java.io.File
import java.io.Serializable

/**
 * Created by zhangfan on 16-8-30.
 */
class AppFileInfo(var file: File) : Serializable {

    var name: String? = null

    override fun toString(): String {
        return name.orEmpty()
    }

    var icon: Drawable? = null

    var versionName: String? = null

    var versionCode: Int? = null

    var packageInfo: PackageInfo? = null

    fun init(pm: PackageManager, context: Context) {
        var resources: Resources? = null
        val info = pm.getPackageArchiveInfo(file.getPath(), 0)

        try {
            resources = getResources(context, file.path)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            if (resources != null) {
                icon = resources.getDrawable(info.applicationInfo.icon)
            }
        } catch (e: Exception) {
            icon = pm.defaultActivityIcon
        }

        try {
            if (resources != null) {
                name = resources.getString(info.applicationInfo.labelRes)
            }
        } catch (e: Exception) {
            name = info.packageName
        }


        versionName = info.versionName
        versionCode = info.versionCode
        packageInfo = info
    }

    @Throws(Exception::class)
    fun getResources(context: Context, apkPath: String): Resources {
        val PATH_AssetManager = "android.content.res.AssetManager"
        val assetMagCls = Class.forName(PATH_AssetManager)
        val assetMagCt = assetMagCls.getConstructor()
        val assetMag = assetMagCt.newInstance()
        var typeArgs = arrayOfNulls<Class<*>>(1)
        typeArgs[0] = String::class.java
        val assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath",
                *typeArgs)
        var valueArgs = arrayOfNulls<Any>(1)
        valueArgs[0] = apkPath
        assetMag_addAssetPathMtd.invoke(assetMag, *valueArgs)
        var res = context.resources
        typeArgs = arrayOfNulls<Class<*>>(3)
        typeArgs[0] = assetMag.javaClass
        typeArgs[1] = res.displayMetrics.javaClass
        typeArgs[2] = res.configuration.javaClass
        val resCt = Resources::class.java.getConstructor(*typeArgs)
        valueArgs = arrayOfNulls<Any>(3)
        valueArgs[0] = assetMag
        valueArgs[1] = res.displayMetrics
        valueArgs[2] = res.configuration
        res = resCt.newInstance(*valueArgs) as Resources
        return res
    }
}