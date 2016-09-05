package com.nightfarmer.coder.main

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.ActionBarDrawerToggle
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
import com.nightfarmer.coder.widget.ProcessFloatingButton
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import com.trello.rxlifecycle.kotlin.bindToLifecycle
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.activity_app_detail.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import okhttp3.ResponseBody
import org.jetbrains.anko.toast
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
        title = "Coder"

        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mainAdapter = MainAdapter()
        recyclerView.adapter = mainAdapter


        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener {
            onRefresh()
        }

        swipeRefreshLayout.post {
            swipeRefreshLayout.isRefreshing = true
            onRefresh()
        }

//        ProcessFloatingButton(this,null)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);// 给左上角图标的左边加上一个返回的图标
//        supportActionBar?.title="yooo"
        var mDrawerToggle = ActionBarDrawerToggle(this, main_activity_layout, toolBar, R.string.drawer_open, R.string.drawer_close);
//声明mDrawerToggle对象,其中R.string.open和R.string.close简单可以用"open"和"close"替代

        mDrawerToggle.syncState();//实现箭头和三条杠图案切换和抽屉拉合的同步

        main_activity_layout.setDrawerListener(mDrawerToggle);//监听实现侧边栏的拉开和闭合,即抽屉drawer的闭合和打开
    }

    private fun onRefresh() {
        Retrofit.Builder()
                .baseUrl(AppInfoService.HOST)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AppInfoService::class.java)
                .getAllProject()
                .map {
                    it.projects.toMutableList()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mainAdapter?.appList = it
                    mainAdapter?.notifyDataSetChanged()

                }, {
                    log(it)
                    toast("网络异常,请重试")
                    swipeRefreshLayout.isRefreshing = false
                }, {
                    swipeRefreshLayout.isRefreshing = false
                })
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
                    myFile.writeResponseBody(it, { c -> log("$c/") })
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
