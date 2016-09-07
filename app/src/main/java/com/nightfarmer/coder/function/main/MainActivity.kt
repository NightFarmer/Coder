package com.nightfarmer.coder.function.main

import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.TypedValue
import com.nightfarmer.coder.R
import com.nightfarmer.coder.ColorTheme
import com.nightfarmer.coder.function.about.AboutActivity
import com.nightfarmer.coder.ex.log
import com.nightfarmer.coder.function.local.LocalProActivity
import com.nightfarmer.coder.service.AppInfoService
import com.nightfarmer.coder.function.theme.ThemeChooseActivity
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.theme_choose_item.view.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

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

        swipeRefreshLayout.setColorSchemeResources(ColorTheme.getThemeColorRes(this, R.attr.colorPrimary))
        swipeRefreshLayout.setOnRefreshListener {
            onRefresh()
        }

        swipeRefreshLayout.post {
            swipeRefreshLayout.isRefreshing = true
            onRefresh()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true);// 给左上角图标的左边加上一个返回的图标
//        supportActionBar?.title="yooo"
        val mDrawerToggle = ActionBarDrawerToggle(this, main_activity_layout, toolBar, R.string.drawer_open, R.string.drawer_close);
//声明mDrawerToggle对象,其中R.string.open和R.string.close简单可以用"open"和"close"替代

        mDrawerToggle.syncState();//实现箭头和三条杠图案切换和抽屉拉合的同步

        main_activity_layout.addDrawerListener(mDrawerToggle);//监听实现侧边栏的拉开和闭合,即抽屉drawer的闭合和打开


        nav_view.setNavigationItemSelectedListener {
            it.isChecked = true

            when (it.itemId) {
                R.id.menu_home -> {
                }
                R.id.menu_local -> {
                    startActivity<LocalProActivity>()
                }
                R.id.menu_theme -> {
                    startActivity<ThemeChooseActivity>()
                }
                R.id.menu_about -> {
                    startActivity<AboutActivity>()
                }
                else -> {
                }
            }

            true
        }

        nav_view.getHeaderView(0).iv_logo.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN)
    }

    override fun onResume() {
        super.onResume()
        nav_view.menu.getItem(0).isChecked = true
    }

    var preBackPressTime = 0L
    override fun onBackPressed() {
        if (main_activity_layout.isDrawerOpen(nav_view)) {
            main_activity_layout.closeDrawer(nav_view)
            return
        }
        if (System.currentTimeMillis() - preBackPressTime > 3000) {
            toast("再按一次退出")
            preBackPressTime = System.currentTimeMillis()
            return
        }
        super.onBackPressed()
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


    //===================================================================================================
//
//    private fun testDownLoad() {
//        val myFile = File(getExternalFilesDir(null).toString() + File.separator + "WindowsXP_SP2.exe")
//
//        val downloadService = Retrofit.Builder()
//                .baseUrl("http://speed.myzone.cn/")
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .build()
//                .create(AppInfoService::class.java)
//
//        downloadService.downloadFile("WindowsXP_SP2.exe")
//                .bindToLifecycle(this)
//                .doOnNext {
//                    log("saving....on" + Thread.currentThread().name)
//                    myFile.writeResponseBody(it, { c -> log("$c/") })
//                }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ log("ok......") }, { log("failed...........$it") })
//    }
//
//
//    private fun test2() {
//        val retrofit = Retrofit.Builder()
//                .baseUrl("https://api.github.com/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .build()
//
//        val service = retrofit.create(AppInfoService::class.java)
//
//        service.xxx("nightfarmer")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ it.forEach { log(it) } }, { print(it) })
//
//    }
//
//    private fun test1() {
//        val retrofit = Retrofit.Builder()
//                .baseUrl("https://api.github.com/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//
//        val service = retrofit.create(AppInfoService::class.java)
//
//        val listRepos = service.listRepos("octocat")
//
//        listRepos.enqueue(object : Callback<List<ProjectInfo>> {
//            override fun onFailure(call: Call<List<ProjectInfo>>?, t: Throwable?) {
//                log(t)
//            }
//
//            override fun onResponse(call: Call<List<ProjectInfo>>?, response: Response<List<ProjectInfo>>?) {
//                print(response)
//                response?.body()?.forEach {
//                    log(it)
//                }
//            }
//        })
//        log(listRepos)
//    }

}
