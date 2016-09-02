package com.nightfarmer.coder.main

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import com.morgoo.droidplugin.pm.PluginManager
import com.nightfarmer.coder.main.MainAdapter
import com.nightfarmer.coder.R
import com.nightfarmer.coder.bean.AppFileInfo
import com.nightfarmer.coder.bean.ProjectInfo
import com.nightfarmer.coder.ex.log
import com.nightfarmer.coder.service.AppInfoService
import com.nightfarmer.coder.ex.writeResponseBody
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import com.trello.rxlifecycle.kotlin.bindToLifecycle
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.*
import java.util.concurrent.TimeUnit

class MainActivity : RxAppCompatActivity() {

    private var mainAdapter: MainAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolBar)

        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mainAdapter = MainAdapter()
        recyclerView.adapter = mainAdapter

        Observable.just("23code")
                .map {
                    val sdPath: File
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// SD卡
                        sdPath = Environment.getExternalStorageDirectory()
                    } else {// 内存
                        sdPath = filesDir
                    }
//                    val appFolder = File(File(sdPath, "Coder"), "APP")
                    val appFolder = File(sdPath, "23code")
                    if (!appFolder.exists()) {
                        appFolder.mkdirs()
                    }
                    appFolder
                }
                .map {
                    val pm = this.packageManager
                    it.listFiles().map {
                        val info = pm.getPackageArchiveInfo(it.path, 0)
                        val appFileInfo = AppFileInfo(it)
//                        val info = PluginManager.getInstance().getPackageInfo(pkg, 0)

                        appFileInfo.init(pm, this)
//                        appFileInfo.name = pm.getApplicationLabel(info.applicationInfo).toString()
                        appFileInfo
                    }.toMutableList()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    log(it)
//                    mainAdapter?.appList = it
//                    mainAdapter?.notifyDataSetChanged()
                }, { log(it) })

//        test1()

//        test2()

//        testDownLoad()
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener {
            onRefresh()
        }
        swipeRefreshLayout.post { swipeRefreshLayout.isRefreshing = true }
//        onRefresh()

        Retrofit.Builder()
                .baseUrl(AppInfoService.HOST)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AppInfoService::class.java)
                .getAllProject()
                .map {
                    var fileInfo = AppFileInfo(File(""))
                    fileInfo.name = it.projects.get(0).name
                    fileInfo
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
//                    log(it.projects.get(0))

                    mainAdapter?.appList = mutableListOf(it)
                    mainAdapter?.notifyDataSetChanged()

                }, {
                    log(it)
                }
                )
    }

    private fun onRefresh() {
        Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { swipeRefreshLayout.isRefreshing = false }
    }

    private fun testDownLoad() {
        val myFile = File(getExternalFilesDir(null).toString() + File.separator + "WindowsXP_SP2.exe")

        val downloadService = Retrofit.Builder()
                .baseUrl("http://speed.myzone.cn/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(AppInfoService::class.java)

        downloadService.downloadFile("WindowsXP_SP2.exe")
                .bindToLifecycle(this)
                .doOnNext {
                    log("saving....on" + Thread.currentThread().name)
                    myFile.writeResponseBody(it, { c, t -> log("$c/$t") })
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ log("ok......") }, { log("failed...........$it") })
    }


    private fun test2() {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()

        val service = retrofit.create(AppInfoService::class.java)

        service.xxx("nightfarmer")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it.forEach { log(it) } }, { print(it) })

    }

    private fun test1() {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service = retrofit.create(AppInfoService::class.java)

        val listRepos = service.listRepos("octocat")

        listRepos.enqueue(object : Callback<List<ProjectInfo>> {
            override fun onFailure(call: Call<List<ProjectInfo>>?, t: Throwable?) {
                log(t)
            }

            override fun onResponse(call: Call<List<ProjectInfo>>?, response: Response<List<ProjectInfo>>?) {
                print(response)
                response?.body()?.forEach {
                    log(it)
                }
            }
        })
        log(listRepos)
    }

}
