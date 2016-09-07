package com.nightfarmer.coder.function.local

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.nightfarmer.coder.R
import com.nightfarmer.coder.bean.AppFileInfo
import com.nightfarmer.coder.ex.log
import kotlinx.android.synthetic.main.activity_main_content.*
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

class LocalProActivity : AppCompatActivity() {

    private var localProAdapter: LocalProAdapter? = null

    var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_pro)

        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);// 给左上角图标的左边加上一个返回的图标
        title = "本地缓存应用"

        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        localProAdapter = LocalProAdapter()
        recyclerView.adapter = localProAdapter


        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener {
            subscription?.unsubscribe()
            onRefresh()
        }
        swipeRefreshLayout.post {
            swipeRefreshLayout.isRefreshing = true
            onRefresh()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onRefresh() {
        subscription = Observable.just("23code")
                .flatMap {
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
                    Observable.from(appFolder.listFiles())
                }
                .map {
                    val pm = this.packageManager
                    val appFileInfo = AppFileInfo(it)
                    appFileInfo.init(pm, this)
                    appFileInfo
                }
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { localProAdapter?.clear();localProAdapter?.notifyDataSetChanged() }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    log(it)
                    localProAdapter?.add(it)
                    localProAdapter?.notifyItemInserted((localProAdapter?.itemCount ?: 0) - 1)
                }, {
                    log(it)
                })

        //立即结束 显得好看些
        swipeRefreshLayout.isRefreshing = false
    }
}
