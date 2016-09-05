package com.nightfarmer.coder.main

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.nightfarmer.coder.R
import com.nightfarmer.coder.ScrollingActivity
import com.nightfarmer.coder.bean.AppFileInfo
import com.nightfarmer.coder.bean.ProjectInfo
import com.nightfarmer.coder.detail.AppDetailActivity
import com.nightfarmer.coder.local.LocalProActivity
import kotlinx.android.synthetic.main.layout_app_item.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity

/**
 * Created by zhangfan on 16-8-25.
 */
class MainAdapter : RecyclerView.Adapter<MainAdapter.MyHolder>() {

    //    var appList: MutableList<AppFileInfo> = mutableListOf()
    var appList: MutableList<ProjectInfo> = mutableListOf()

    override fun getItemCount(): Int {
        return appList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyHolder? {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_app_item, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {

        val image = holder.itemView.image
        Glide.with(holder.itemView.context)
                .load("http://nightfarmer.github.io/public/static/image/BezierDrawer.gif")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.mipmap.ic_launcher)
//                .load(Uri.parse("http://img.blog.csdn.net/20150826183423554"))
                .into(image)

        val appFileInfo = appList[position]
        holder.itemView.title.text = appFileInfo.name.orEmpty()
        holder.itemView.icon.visibility = View.GONE
        holder.data = appFileInfo
//        }
    }


    inner class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        var data: ProjectInfo? = null

        init {
            view.onClick {
                data?.let {
                    val intent = Intent(view.context, AppDetailActivity::class.java)
                    intent.putExtra("appInfo", data)
                    view.context.startActivity(intent)
                }
            }
        }
    }
}