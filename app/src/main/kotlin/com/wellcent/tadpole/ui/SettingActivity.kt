package com.wellcent.tadpole.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.ui.showComfirmCrouton
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import org.jetbrains.anko.*
import java.util.*

class SettingActivity : TadpoleActivity(){
    override fun initialize(savedInstanceState: Bundle?) {
        verticalLayout {
            backgroundColor = getResColor(R.color.window_background)
            navigateBar("设置") {
                setTitleColor(Color.BLACK)
                setNavBg(R.drawable.tabbar_bg)
                addLeftDefaultBtn(R.drawable.icon_back_p){ finish() }
            }.lparams(MATCH_PARENT, DimensAdapter.nav_height){ bottomMargin = kIntHeight(0.015f) }
            addContentCell("个人信息"){}.invoke(this)
            addContentCell("密码设置"){}.invoke(this)
            addContentCell("清除缓存").invoke(this)
            textView("退出登录"){
                gravity = Gravity.CENTER
                textColor = Color.RED
                textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                verticalPadding = kIntHeight(0.02f)
                backgroundColor = Color.WHITE
            }.lparams(MATCH_PARENT, WRAP_CONTENT){ topMargin = kIntHeight(0.015f) }
        }
    }
    
    fun addContentCell(title:String ,process:(()->Unit)? = null):_LinearLayout.()-> TextView? {
        return {
            var cacheSize:TextView? = null
            relativeLayout {
                backgroundColor = Color.WHITE
                verticalPadding = kIntHeight(0.02f)
                horizontalPadding = kIntWidth(0.08f)
                process?.apply { onMyClick { this() } }
                textView(title) {
                    textColor = getResColor(R.color.text_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                }.lparams(WRAP_CONTENT, WRAP_CONTENT) { centerVertically() }

                if(process != null) {
                    val arrowView = imageView(R.drawable.icon_right_arrows) { kRandomId() }.lparams {
                        alignParentRight()
                        centerVertically()
                    }
                } else {
                    cacheSize = textView(String.format("%.2fM",Random().nextFloat())) {
                        textColor = getResColor(R.color.text_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                        onMyClick { uiThread(1000){ showComfirmCrouton("缓存清理成功!") 
                            text = "0.0M"
                        } }
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) { centerVertically() 
                        alignParentRight()}
                }
            }.lparams(MATCH_PARENT, WRAP_CONTENT) { }
            textView { backgroundColor = getResColor(R.color.gap_line) }.lparams(MATCH_PARENT, DimensAdapter.dip1.toInt()) { horizontalMargin = kIntWidth(0.08f) }
        }
    }
}