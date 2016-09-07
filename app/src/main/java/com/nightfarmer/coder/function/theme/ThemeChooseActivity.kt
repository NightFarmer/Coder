package com.nightfarmer.coder.function.theme

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.nightfarmer.coder.R
import com.nightfarmer.coder.ColorTheme
import com.nightfarmer.coder.ex.log
import com.nightfarmer.coder.function.main.MainActivity
import kotlinx.android.synthetic.main.activity_theme_choose.*
import org.jetbrains.anko.startActivity

class ThemeChooseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setTheme(R.style.AppTheme2)
        setContentView(R.layout.activity_theme_choose)
        setSupportActionBar(toolbar)
        title = "选择主题"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = ThemeChooseAdapter()
    }

    fun t1(v: View) {
        ColorTheme.setStyle(this, R.style.Theme_Red)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
