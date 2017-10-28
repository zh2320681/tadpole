package com.wellcent.tadpole.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.getResColor
import com.shrek.klib.extension.kIntHeight
import com.shrek.klib.extension.kIntWidth
import com.shrek.klib.extension.onMyClick
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.view.KActivity
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import org.jetbrains.anko.*

class FeedbackActivity : KActivity(){
    override fun initialize(savedInstanceState: Bundle?) {
        verticalLayout {
            gravity = Gravity.CENTER_HORIZONTAL
            backgroundColor = getResColor(R.color.window_background)
            navigateBar("意见反馈") {
                setTitleColor(Color.BLACK)
                setNavBg(R.drawable.tabbar_bg)
                addLeftDefaultBtn(R.drawable.icon_back_p) { finish() }
            }.lparams(MATCH_PARENT, DimensAdapter.nav_height) { bottomMargin = kIntHeight(0.015f) }
            linearLayout { 
                backgroundColor  = Color.WHITE
                editText { 
                    hint = "请输入您的意见与建议"
                    gravity = Gravity.TOP
                    lines = 10
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_SMALL)
                    hintTextColor = getResColor(R.color.text_light_black)
                    backgroundColor = Color.TRANSPARENT
                }.lparams(MATCH_PARENT,kIntHeight(0.35f)) { horizontalMargin = kIntWidth(0.02f) }
            }.lparams(MATCH_PARENT, WRAP_CONTENT)

            textView("提 交") {
                textColor = Color.WHITE
                backgroundResource = R.drawable.primary_btn
                textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                gravity = Gravity.CENTER
                onMyClick { }
            }.lparams(kIntWidth(0.9f),kIntHeight(0.1f))
        }
    }
}