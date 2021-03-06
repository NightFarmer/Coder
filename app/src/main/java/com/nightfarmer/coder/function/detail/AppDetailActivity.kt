package com.nightfarmer.coder.function.detail

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.os.RemoteException
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.morgoo.droidplugin.pm.PluginManager
import com.morgoo.helper.compat.PackageManagerCompat
import com.nightfarmer.coder.R
import com.nightfarmer.coder.bean.AppFileInfo
import com.nightfarmer.coder.bean.ProjectInfo
import com.nightfarmer.coder.ex.log
import com.nightfarmer.coder.ex.setTextColor
import com.nightfarmer.coder.ex.writeResponseBody
import com.nightfarmer.coder.service.AppInfoService
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.activity_app_detail.*
import kotlinx.android.synthetic.main.activity_app_detail.view.*
import kotlinx.android.synthetic.main.layout_app_item.*
import okhttp3.ResponseBody
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit

class AppDetailActivity : RxAppCompatActivity() {

    private var proInfo: ProjectInfo? = null
    private var appFileInfo: AppFileInfo? = null

    private var subscribtion: Subscription? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        proInfo = intent.getSerializableExtra("appInfo") as? ProjectInfo
        val appFile = intent.getSerializableExtra("appFile") as? File

        if (appFile != null) {
            appFileInfo = AppFileInfo(appFile)
            appFileInfo?.init(packageManager, this)
        } else {
            readLocalFile()
        }

//        backdrop.setImageResource(R.mipmap.ic_launcher)
        title = (appFileInfo?.name ?: proInfo?.name).orEmpty()
        webView.loadUrl("https://github.com/DroidPluginTeam/DroidPlugin/blob/master/readme.md")
        tv_describe.text = "简介：${proInfo?.describe}"

        initUIInfo()

        floatingBtn.onClick {
            if (appFileInfo == null) {
                downLoad()
            } else {
                val appFileInfo = appFileInfo as AppFileInfo
                onComplete(appFileInfo.file)
//                val intent = packageManager.getLaunchIntentForPackage(appFileInfo.packageInfo?.packageName)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
            }
        }

    }

    private fun initUIInfo() {
        if (appFileInfo == null) {
            tv_state.text = "未下载"
            floatingBtn.setImageResource(R.drawable.ic_get_app_white_48dp)
        } else {
            floatingBtn.progress = 1f
            tv_state.text = "已下载"
            floatingBtn.setImageResource(R.drawable.ic_play_arrow_white_48dp)
        }
    }

    private fun readLocalFile() {
        val sdPath: File
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// SD卡
            sdPath = Environment.getExternalStorageDirectory()
        } else {// 内存
            sdPath = filesDir
        }
        val appFolder = File(File(sdPath, "Coder"), "APP")
        val file = File(appFolder, proInfo?.apk)
        if (file.exists()) {
            try {
                val info = AppFileInfo(file)
                info.init(packageManager, this)
                appFileInfo = info
            } catch(e: Exception) {
            }
        } else {
            appFileInfo = null
        }
    }


    fun showWebView(view: View) {
//        webView.loadUrl("https://github.com/DroidPluginTeam/DroidPlugin/blob/master/readme.md")
        webView.visibility = View.VISIBLE
    }

    override fun onOptionsMenuClosed(menu: Menu?) {
        super.onOptionsMenuClosed(menu)
    }

    override fun onContextMenuClosed(menu: Menu?) {
        super.onContextMenuClosed(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.menu_delete -> deleteAppFile()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAppFile() {
        Observable.create<String> {
            val fileInfo = appFileInfo
            if (fileInfo == null || !fileInfo.file.exists()) {
                it.onNext("文件不存在")
            } else {
                val delete = fileInfo.file.delete()
                if (delete) {
                    it.onNext("删除成功")
                } else {
                    it.onNext("删除失败")
                }
            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Snackbar.make(main_content, "$it!", Snackbar.LENGTH_SHORT).setTextColor(R.color.white).show()
                    readLocalFile()
                    initUIInfo()
                }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_detail, menu)
        return true
    }


    private fun downLoad() {
        if (!(subscribtion?.isUnsubscribed ?: true)) return
        Snackbar.make(main_content, "开始下载!", Snackbar.LENGTH_SHORT).setTextColor(R.color.white).show()
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
//        val file = File(appFolder, "ThemeLib.apk")
        val file = File(appFolder, proInfo?.apk)
//        if (file.exists()) {
//            onComplete(file)
//            return
//        }

//        val myFile = File(getExternalFilesDir(null).toString() + File.separator + "WindowsXP_SP2.exe")
//        https://nightfarmer.github.io/apkDepot/project/ThemeLib/sample.apk
        val downloadService = Retrofit.Builder()
//                .baseUrl("http://speed.myzone.cn/")
                .baseUrl("https://nightfarmer.github.io/apkDepot/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(AppInfoService::class.java)
//        http://www.jqgj.com.cn/download/jqgj.apk
//        downloadService.downloadFile("WindowsXP_SP2.exe")
        subscribtion = downloadService.downloadFile("project/ThemeLib/sample.apk")
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .flatMap(Func1<ResponseBody, rx.Observable<kotlin.String>> { responseBody ->
                    Observable.create {
                        file.writeResponseBody(responseBody) { c -> it.onNext("$c/${responseBody.contentLength()}") }
                        it.onCompleted()
                    }
                })
                .throttleLast(200, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    log("ok......$it")/*;bt_download.text = it*/
                    val split = it.split("/")
                    floatingBtn.progress = split[0].toFloat() / split[1].toFloat()
                    tv_state.text = it
                }, {
                    log("failed...........$it")
                    tv_state.text = "下载失败"
                }, {
                    onComplete(file)
                    tv_state.text = "已下载"
                    floatingBtn.setImageResource(R.drawable.ic_play_arrow_white_48dp)
                })
    }

    private fun onComplete(file: File) {
        log("complete....")

        if (file.exists()) {
            try {
                val info = AppFileInfo(file)
                info.init(packageManager, this)
                appFileInfo = info
            } catch(e: Exception) {
            }
        }

        if (!PluginManager.getInstance().isConnected()) {
            Toast.makeText(this, "插件服务正在初始化，请稍后再试。。。", Toast.LENGTH_SHORT).show()
        }
        try {
            val pm = this.packageManager
            val info = pm.getPackageArchiveInfo(file.path, 0);

//            PluginManager.getInstance().deletePackage(info.packageName, 0);
            if (PluginManager.getInstance().getPackageInfo(info.packageName, 0) != null) {
//                Toast.makeText(this, "已经安装了，不能再安装", Toast.LENGTH_SHORT).show()
                val intent = pm.getLaunchIntentForPackage(info.packageName)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
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
