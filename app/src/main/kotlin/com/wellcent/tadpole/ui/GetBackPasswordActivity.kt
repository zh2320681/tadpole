package com.wellcent.tadpole.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.ui.kDefaultRestHandler
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.ui.showAlertCrouton
import com.shrek.klib.ui.showComfirmCrouton
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.presenter.VerifyOperable
import com.wellcent.tadpole.presenter.success
import com.wellcent.tadpole.util.LooperTask
import org.jetbrains.anko.*

class GetBackPasswordActivity : TadpoleActivity(), VerifyOperable {
    lateinit var accountView: EditText
    lateinit var codeView: EditText
    lateinit var codeBtn: TextView
    lateinit var pwView: EditText
    lateinit var pwAgainView: EditText
    
    lateinit var contentLayout:LinearLayout

    var reqCodeTime:Long = -1
    val alertRequestTask = LooperTask(1000) {
        val left = 60 -(System.currentTimeMillis() - reqCodeTime)/1000
        if(reqCodeTime > 0 && left > 0){
            codeBtn.setText("${left}秒再次发送")
            codeBtn.isEnabled = false
        } else {
            codeBtn.isEnabled = true
            codeBtn.setText("验证码")
            reqCodeTime = -1
            false
        }
        true
    }
    
    override fun initialize(savedInstanceState: Bundle?) {
        verticalLayout {
            gravity = Gravity.CENTER_HORIZONTAL
            backgroundColor = getResColor(R.color.window_background)
            navigateBar("找回密码") {
                setTitleColor(Color.BLACK)
                setNavBg(R.drawable.tabbar_bg)
                addLeftDefaultBtn(R.drawable.icon_back_p) { finish() }
                addRightTxt("完成",getResColor(R.drawable.primary_btn),DimensAdapter.textSpSize(CustomTSDimens.BIG)){
                    getBack()
                }
            }.lparams(MATCH_PARENT, DimensAdapter.nav_height) { bottomMargin = kIntHeight(0.015f) }
            contentLayout = verticalLayout {
                backgroundColor = Color.WHITE
                accountView = addContentCell( "手机号码").invoke(this)
                codeView = addContentCell("验证码"){ sendCode() }.invoke(this)
                pwView = addContentCell( "新密码").invoke(this)
                pwAgainView = addContentCell( "重复新密码").invoke(this)
            }.lparams(MATCH_PARENT, WRAP_CONTENT){ topMargin = kIntHeight(0.01f) }
//            textView("提  交") {
//                textColor = Color.WHITE
//                backgroundResource = R.drawable.primary_btn
//                textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
//                gravity = Gravity.CENTER
//                onMyClick { getBack() }
//            }.lparams(kIntWidth(0.9f),kIntHeight(0.1f))
        }  
    }

    fun addContentCell(title: String, sendVerifyCodeProcess: (()->Unit)?= null ): _LinearLayout.()-> EditText {
        return {
            var titleView: TextView? = null
            var inputView: EditText? = null
            relativeLayout {
                backgroundColor = Color.WHITE
//                verticalPadding = kIntHeight(0.015f)
                horizontalPadding = kIntWidth(0.08f)
                titleView = textView(title) {
                    kRandomId()
                    textColor = getResColor(R.color.text_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                }.lparams(WRAP_CONTENT, WRAP_CONTENT) { centerVertically() }

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
                    codeBtn = rightView!!
                }
                inputView = editText {
                    textColor = getResColor(R.color.text_little_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                    backgroundColor = Color.TRANSPARENT
                    gravity = Gravity.LEFT and Gravity.CENTER_VERTICAL
                }.lparams(MATCH_PARENT, WRAP_CONTENT) { centerVertically()
                    rightOf(titleView!!)
                    leftMargin = kIntWidth(0.02f)
                    rightView?.apply { leftOf(this)}
                }
            }.lparams(MATCH_PARENT, kIntHeight(0.07f)) { }
            textView { backgroundColor = getResColor(R.color.gap_line) }.lparams(MATCH_PARENT, DimensAdapter.dip1.toInt()) { horizontalMargin = kIntWidth(0.08f) }
            inputView!!
        }
    }

    fun sendCode() {
        if(!isAccountAvild()) { return }
        verifyOpt.getCode(accountView.text.toString()).handler(kDefaultRestHandler(" 正在发送验证码,请稍等... ")).success {
            showComfirmCrouton("验证码发送成功",contentLayout)
            reqCodeTime = System.currentTimeMillis()
            alertRequestTask.start()
        }.excute(this)
    }

    fun isAccountAvild() : Boolean {
        if(accountView.text.isEmpty()) {
            showAlertCrouton("请您填写手机号",contentLayout)
            return false
        }
        if(!accountView.text.toString().isMobile()) {
            showAlertCrouton("请您填写正确的手机号",contentLayout)
            return false
        }
        return true
    }
    
    fun getBack() {
        if(!isAccountAvild()) { return }
        val account = accountView.text.toString()
        val code = codeView.text.toString()
        val pw = pwView.text.toString()
        val pwAgain = pwAgainView.text.toString()
        if(pw.isEmpty()) {
            showAlertCrouton("请您填写新密码",contentLayout)
            return
        }
        if(code.isEmpty()) {
            showAlertCrouton("请您填写验证码",contentLayout)
            return
        }
        if(pwAgain.isEmpty()){
            showAlertCrouton("请您填写重复新密码",contentLayout)
            return
        }
        if(!pwAgain.equals(pw)){
            showAlertCrouton("您两次填写的密码不一致",contentLayout)
            pwView.setText("")
            pwAgainView.setText("")
            return 
        }
        verifyOpt.getBackPassword(account,code,pw).handler(kDefaultRestHandler("正在找回密码,请稍等... ")).success {
            toastLongShow("密码修改成功,请重新登录")
            finish()
        }.excute(this)
    }
}