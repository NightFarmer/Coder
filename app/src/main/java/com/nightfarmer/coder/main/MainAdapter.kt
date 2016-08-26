package com.nightfarmer.coder.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.nightfarmer.coder.R
import com.nightfarmer.coder.ScrollingActivity
import com.nightfarmer.coder.detail.AppDetailActivity
import kotlinx.android.synthetic.main.layout_app_item.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity

/**
 * Created by zhangfan on 16-8-25.
 */
class MainAdapter : RecyclerView.Adapter<MainAdapter.MyHolder>() {
    override fun getItemCount(): Int {
        return 30
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyHolder? {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_app_item, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {

        Glide.with(holder.itemView.context)
                .load("http://nightfarmer.github.io/public/static/image/BezierDrawer.gif")
                .placeholder(R.mipmap.ic_launcher)
//                .load(Uri.parse("http://img.blog.csdn.net/20150826183423554"))
                .into(holder.itemView.image)

        holder.itemView.title.text = "test.."
        holder.type = position % 2;
    }


    inner class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        var type = 0;

        init {
            view.onClick {
                if (type==0){

                view.context.startActivity<AppDetailActivity>()
                }else{
                view.context.startActivity<ScrollingActivity>()

                }
            }
        }
    }
}