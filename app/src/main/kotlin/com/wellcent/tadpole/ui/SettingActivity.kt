package com.wellcent.tadpole.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.ui.showComfirmCrouton
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.presenter.ROUTINE_DATA_BINDLE
import com.wellcent.tadpole.presenter.VerifyOperable
import org.jetbrains.anko.*
import java.util.*

class SettingActivity : TadpoleActivity(),VerifyOperable{
    override fun initialize(savedInstanceState: Bundle?) {
        verticalLayout {
            backgroundColor = getResColor(R.color.window_background)
            navigateBar("设置") {
                setTitleColor(Color.BLACK)
                setNavBg(R.drawable.tabbar_bg)
                addLeftDefaultBtn(R.drawable.icon_back_p){ finish() }
            }.lparams(MATCH_PARENT, DimensAdapter.nav_height){ bottomMargin = kIntHeight(0.015f) }
            addContentCell("个人信息"){ startActivity<ModifyActivity>() }.invoke(this)
            addContentCell("密码设置"){ startActivity<ModifyActivity>( ROUTINE_DATA_BINDLE to ModifyType.PASSWORD.code) }.invoke(this)
            addContentCell("清除缓存").invoke(this)
            textView("退出登录"){
                gravity = Gravity.CENTER
                textColor = Color.RED
                textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                verticalPadding = kIntHeight(0.02f)
                backgroundColor = Color.WHITE
                onMyClick {
                    SweetAlertDialog(this@SettingActivity, SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("您确认退出登录吗?")
                            .setConfirmText(" 登 出 ")
                            .setConfirmClickListener {
                                verifyOpt.logOut()
                                finish()
                            }.setCancelText(" 取 消 ").show()
                }
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
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) { centerVertically() 
                        alignParentRight()}
                    this.onMyClick { uiThread(1000){ showComfirmCrouton("缓存清理成功!")
                        cacheSize!!.text = "0.0M"
                    } } 
                }
            }.lparams(MATCH_PARENT, WRAP_CONTENT) { }
            textView { backgroundColor = getResColor(R.color.gap_line) }.lparams(MATCH_PARENT, DimensAdapter.dip1.toInt()) { horizontalMargin = kIntWidth(0.08f) }
        }
    }
}