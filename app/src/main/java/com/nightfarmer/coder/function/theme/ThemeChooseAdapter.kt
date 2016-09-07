package com.nightfarmer.coder.function.theme

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.support.annotation.StyleRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nightfarmer.coder.R
import com.nightfarmer.coder.ColorTheme
import kotlinx.android.synthetic.main.theme_choose_item.view.*
import org.jetbrains.anko.onClick

/**
 * Created by zhangfan on 16-9-7.
 */

class ThemeChooseAdapter() : RecyclerView.Adapter<ThemeChooseAdapter.ThemeItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ThemeItemHolder? {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.theme_choose_item, parent, false)
        return ThemeItemHolder(view)
    }

    override fun onBindViewHolder(holder: ThemeItemHolder, position: Int) {
        val context = holder.itemView.context
        val themeEnum = ThemeEnum.values()[position]
        holder.style = themeEnum.res
        if (holder.style == ColorTheme.style) {
            holder.itemView?.theme_choose_btn?.isChecked = true
            val typedValue = TypedValue();
            context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
            holder.itemView.theme_choose_btn.setTextColor(typedValue.data)
            holder.itemView.theme_choose_btn.text = "使用中"
            holder.itemView.theme_color_dot.text = "√"
        } else {
            holder.itemView?.theme_choose_btn?.isChecked = false
            holder.itemView.theme_choose_btn.setTextColor(ContextCompat.getColor(context, R.color.secondary_text))
            holder.itemView.theme_choose_btn.text = "使用"
            holder.itemView.theme_color_dot.text = ""
        }
        val bgd = GradientDrawable()
        bgd.setColor(ContextCompat.getColor(context, themeEnum.primaryColor))
        bgd.setCornerRadius(40f)
        holder.itemView.tv_title.setTextColor(ContextCompat.getColor(context, themeEnum.primaryColor))
        holder.itemView.tv_title.text = themeEnum.title
        holder.itemView.theme_color_dot.setBackgroundDrawable(bgd)
    }

    override fun getItemCount(): Int {
        return ThemeEnum.values().size
    }


    inner class ThemeItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @StyleRes var style: Int? = null

        init {
            itemView.onClick { onClick() }
            itemView.theme_choose_btn.onClick { onClick() }
        }

        fun onClick() {
            val style = this.style
            style?.let {
                ColorTheme.setStyle(itemView.context as Activity, style)
            }
        }
    }
}