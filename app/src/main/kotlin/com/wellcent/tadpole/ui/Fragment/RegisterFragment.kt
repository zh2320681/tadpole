package com.wellcent.tadpole.ui.Fragment

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.ui.showAlertCrouton
import com.shrek.klib.view.KFragment
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

class RegisterFragment : KFragment() {
    lateinit var accountView: EditText
    lateinit var codeView: EditText
    lateinit var pwView: EditText
    lateinit var parentLayout: LinearLayout
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI {
            parentLayout = verticalLayout {
                gravity = Gravity.CENTER_HORIZONTAL
                accountView = lineInit( "手机号码").invoke(this)
                codeView = lineInit("验证码"){ }.invoke(this)
                pwView = lineInit( "设置登录密码").invoke(this)
                textView("注  册") {
                    textColor = Color.WHITE
                    gravity = Gravity.CENTER
                    backgroundResource = R.drawable.primary_btn
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.MID_BIG)
                    onMyClick {  }
                }.lparams(kIntWidth(0.65f), kIntHeight(0.1f)) {
                    topMargin = kIntHeight(0.025f)
                }
                textView("点击上面的'注册'按钮，即表示您同意") {
                    textColor = hostAct.getResColor(R.color.text_light_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_SMALL)
                }.lparams {  }
                textView("小蝌蚪服务协议") {
                    textColor = Color.parseColor("#8586c7")
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_SMALL)
                }.lparams {  }
            }
        }.view
    }


    fun lineInit( hitiTitle: String, sendVerifyCodeProcess: (()->Unit)?= null ): _LinearLayout.() -> EditText {
        return {
            var editText: EditText? = null
            relativeLayout {
                var rightView: TextView? = null
                sendVerifyCodeProcess?.apply {
                    rightView = textView("获取验证码" ) {
                        kRandomId()
                        gravity = Gravity.CENTER
                        backgroundResource = R.drawable.primary_small_btn
                        textColor = Color.WHITE
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_SMALL)
                        onMyClick { sendVerifyCodeProcess?.invoke() }
                    }.lparams(WRAP_CONTENT,WRAP_CONTENT) {
                        alignParentRight()
                        centerVertically()
                    }
                }
                editText = editText {
                    hint = hitiTitle
                    textColor = hostAct.getResColor(R.color.text_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    backgroundColor = Color.TRANSPARENT
                }.lparams(MATCH_PARENT, WRAP_CONTENT) { rightView?.apply { leftOf(this) }
                    centerVertically()}
            }.lparams(MATCH_PARENT, WRAP_CONTENT) { verticalMargin = kIntHeight(0.012f) }
            textView { backgroundColor = hostAct.getResColor(R.color.gap_line) }.lparams(MATCH_PARENT, DimensAdapter.dip1.toInt())
            editText!!
        }
    }

    fun register() {
        if(!isAccountAvild()) { return }
        if(pwView.text.isEmpty()) {
            hostAct.showAlertCrouton("请您填写密码",parentLayout)
            return
        }
        if(codeView.text.isEmpty()) {
            hostAct.showAlertCrouton("请您填写验证码",parentLayout)
            return
        }
    }
    
    fun sendCode() {
        if(!isAccountAvild()) { return }
    }
    
    fun isAccountAvild() : Boolean {
        if(accountView.text.isEmpty()) {
            hostAct.showAlertCrouton("请您填写手机号",parentLayout)
            return false
        }
        if(!accountView.text.toString().isMobile()) {
            hostAct.showAlertCrouton("请您填写正确的手机号",parentLayout)
            return false
        }
        return true
    }
    
}