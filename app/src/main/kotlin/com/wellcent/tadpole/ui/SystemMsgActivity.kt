package com.wellcent.tadpole.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.widget.TextView
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.getResColor
import com.shrek.klib.extension.kIntHeight
import com.shrek.klib.extension.kIntWidth
import com.shrek.klib.extension.toastLongShow
import com.shrek.klib.ui.adapter.HolderBo
import com.shrek.klib.ui.adapter.KAdapter
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView

class SystemMsgActivity : TadpoleActivity() {
    lateinit var recyclerView: RecyclerView
    override fun initialize(savedInstanceState: Bundle?) {
        verticalLayout {
            backgroundColor = getResColor(R.color.window_background)
            navigateBar("系统消息") {
                setTitleColor(Color.BLACK)
                setNavBg(R.drawable.tabbar_bg)
                addLeftDefaultBtn(R.drawable.icon_back_p) { finish() }
            }.lparams(MATCH_PARENT, DimensAdapter.nav_height)
            recyclerView = recyclerView {
                layoutManager = LinearLayoutManager(context)
                adapter = KAdapter<String, SysMsgHolder>(arrayListOf("1111111", "2222222", "333333", "44444", "55555", "66666")) {
                    itemConstructor { SysMsgHolder(kIntHeight(0.17f)) }
                    itemClickDoing { s, i -> toastLongShow("我点击了${s}") }
                    bindData { holder, s, i ->
                    }
                }
            }.lparams(MATCH_PARENT, MATCH_PARENT)
        }
    }
}

class SysMsgHolder(cellHeight:Int): HolderBo(cellHeight) {
    lateinit var timeView: TextView
    lateinit var desView: TextView
    lateinit var titleView: TextView
    override fun rootViewInit(): AnkoContext<Context>.() -> Unit {
        return {
            verticalLayout { 
                gravity = Gravity.CENTER
                timeView = textView("2017年10月22日 13:22:12") {
                    textColor = context.getResColor(R.color.text_light_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                }.lparams(WRAP_CONTENT, WRAP_CONTENT) { verticalMargin = kIntHeight(0.01f) }
                verticalLayout { 
                    backgroundColor = Color.WHITE
                    horizontalPadding = kIntWidth(0.04f)
                    titleView = textView("标题标题") {
                        textColor = context.getResColor(R.color.text_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) {
                        topMargin = kIntHeight(0.02f)
                    }
                    desView = textView("描述描述描述") {
                        textColor = context.getResColor(R.color.text_little_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                        lines = 2
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) {
                        verticalMargin = kIntHeight(0.02f)
                    }
                }.lparams(MATCH_PARENT, MATCH_PARENT)
            }
        }
    }
}