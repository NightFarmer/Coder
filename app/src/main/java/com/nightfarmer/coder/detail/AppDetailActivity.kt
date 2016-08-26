package com.nightfarmer.coder.detail

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.nightfarmer.coder.R
import com.nightfarmer.coder.ex.log
import com.nightfarmer.coder.ex.writeResponseBody
import com.nightfarmer.coder.service.AppInfoService
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.activity_app_detail.*
import okhttp3.ResponseBody
import org.jetbrains.anko.onClick
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit

class AppDetailActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_detail)
        setSupportActionBar(toolbar)
        title = "详情"
        bt_download.onClick {
            testDownLoad()
        }
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
                .flatMap(Func1<ResponseBody, rx.Observable<kotlin.String>> { responseBody ->
                    Observable.create { myFile.writeResponseBody(responseBody, { c, t -> it.onNext("$c/$t") }) }
                })
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
//                .xxxxxxx
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ log("ok......$it");bt_download.text = it }, { log("failed...........$it") }, { log("complete....") })
    }
}
