package com.nightfarmer.coder.detail

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.RemoteException
import android.support.design.widget.CollapsingToolbarLayout
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.morgoo.droidplugin.pm.PluginManager
import com.morgoo.helper.compat.PackageManagerCompat
import com.nightfarmer.coder.R
import com.nightfarmer.coder.bean.AppFileInfo
import com.nightfarmer.coder.ex.log
import com.nightfarmer.coder.ex.writeResponseBody
import com.nightfarmer.coder.service.AppInfoService
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.activity_app_detail.*
import kotlinx.android.synthetic.main.activity_app_detail.view.*
import okhttp3.ResponseBody
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit

class AppDetailActivity : RxAppCompatActivity() {

    private var appInfo: AppFileInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_detail)
        setSupportActionBar(toolbar)

        val appFile = intent.getSerializableExtra("appInfo") as? File
        appFile?.let {
            appInfo = AppFileInfo(appFile)
            appInfo?.init(packageManager, this)
        }
        title = appInfo?.name.orEmpty()

        backdrop.setImageResource(R.mipmap.ic_launcher)

//        val collapsingToolbar = findViewById(R.id.collapsing_toolbar) as CollapsingToolbarLayout
//        collapsingToolbar.title = "hehehehe"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        bt_download.onClick {
//            testDownLoad()
            appInfo?.let {
                onComplete((appInfo as AppFileInfo).file)
            }
//            val intent = packageManager.getLaunchIntentForPackage(appInfo?.packageInfo?.packageName)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
        }
    }

    override fun onOptionsMenuClosed(menu: Menu?) {
        super.onOptionsMenuClosed(menu)
    }

    override fun onContextMenuClosed(menu: Menu?) {
        super.onContextMenuClosed(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item)
    }

    fun startApp(view: View){
        appInfo?.let {
            onComplete((appInfo as AppFileInfo).file)
        }
    }

    private fun testDownLoad() {
        val sdPath: File
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// SD卡
            sdPath = Environment.getExternalStorageDirectory()
        } else {// 内存
            sdPath = filesDir
        }
        val appFolder = File(File(sdPath, "Coder"), "APP")
        if (!appFolder.exists()) {
            appFolder.mkdirs()
        }
        val file = File(appFolder, "app-debug.apk")
        if (file.exists()) {
            onComplete(file)
            return
        }

//        val myFile = File(getExternalFilesDir(null).toString() + File.separator + "WindowsXP_SP2.exe")

        val downloadService = Retrofit.Builder()
//                .baseUrl("http://speed.myzone.cn/")
                .baseUrl("http://www.jqgj.com.cn/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(AppInfoService::class.java)
//        http://www.jqgj.com.cn/download/jqgj.apk
//        downloadService.downloadFile("WindowsXP_SP2.exe")
        downloadService.downloadFile("download/jqgj.apk")
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .flatMap(Func1<ResponseBody, rx.Observable<kotlin.String>> { responseBody ->
                    Observable.create {
                        file.writeResponseBody(responseBody) { c, t -> it.onNext("$c/$t") }
                        it.onCompleted()
                    }
                })
                .throttleLast(200, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ log("ok......$it");bt_download.text = it }, { log("failed...........$it") }, { onComplete(file) })
    }

    private fun onComplete(file: File) {
        log("complete....")

        if (!PluginManager.getInstance().isConnected()) {
            Toast.makeText(this, "插件服务正在初始化，请稍后再试。。。", Toast.LENGTH_SHORT).show()
        }
        try {
            val pm = this.packageManager
            val info = pm.getPackageArchiveInfo(file.path, 0);

            PluginManager.getInstance().deletePackage(info.packageName, 0);
            if (PluginManager.getInstance().getPackageInfo(info.packageName, 0) != null) {
                Toast.makeText(this, "已经安装了，不能再安装", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "开始安装", Toast.LENGTH_SHORT).show()
                Observable.create<Int> {
                    val re = PluginManager.getInstance().installPackage(file.path, 0)
                    it.onNext(re)
                    it.onCompleted()
                }
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    when (it) {
                                        PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION -> toast("安装失败，文件请求的权限太多")
                                        PackageManagerCompat.INSTALL_FAILED_NOT_SUPPORT_ABI -> toast("宿主不支持插件的abi环境，可能宿主运行时为64位，但插件只支持32位")
                                        PackageManagerCompat.INSTALL_SUCCEEDED -> {
                                            toast("安装完成")
                                            val intent = pm.getLaunchIntentForPackage(info.packageName)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(intent)
                                        }
                                    }
                                },
                                { toast("${it.message}") },
                                { toast("complete") }
                        )
            }
        } catch (e: Exception) {
            toast("eeeeee")
            e.printStackTrace()
            try {
                PluginManager.getInstance().installPackage(file.path, 0)
            } catch (e1: RemoteException) {
                e1.printStackTrace()
            }
        }

    }

}
