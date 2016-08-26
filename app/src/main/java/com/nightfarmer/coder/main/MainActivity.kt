package com.nightfarmer.coder.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import com.nightfarmer.coder.main.MainAdapter
import com.nightfarmer.coder.R
import com.nightfarmer.coder.bean.AppInfo
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
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.*

class MainActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolBar)

        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = MainAdapter()

//        test1()

//        test2()

        testDownLoad()
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

        listRepos.enqueue(object : Callback<List<AppInfo>> {
            override fun onFailure(call: Call<List<AppInfo>>?, t: Throwable?) {
                log(t)
            }

            override fun onResponse(call: Call<List<AppInfo>>?, response: Response<List<AppInfo>>?) {
                print(response)
                response?.body()?.forEach {
                    log(it)
                }
            }
        })
        log(listRepos)
    }

}
