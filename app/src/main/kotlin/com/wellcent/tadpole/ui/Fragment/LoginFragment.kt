package com.wellcent.tadpole.ui.Fragment

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.ui.kDefaultRestHandler
import com.shrek.klib.ui.showAlertCrouton
import com.shrek.klib.ui.showComfirmCrouton
import com.shrek.klib.view.KFragment
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.presenter.VerifyOperable
import com.wellcent.tadpole.presenter.success
import com.wellcent.tadpole.ui.GetBackPasswordActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

class LoginFragment : KFragment(), VerifyOperable {
    lateinit var accountView: EditText
    lateinit var pwView: EditText
    lateinit var parentLayout: LinearLayout
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI {
            parentLayout = verticalLayout {
                gravity = Gravity.CENTER_HORIZONTAL
//                verifyOpt.getStoneUserName() 18012778237 abc123  13962113937  123456
                accountView = lineInit(R.drawable.icon_user, "手机号码", "", R.drawable.icon_login_delete) {imgView,inputView ->
                    inputView.setText("")
                }.invoke(this)
                pwView = lineInit(R.drawable.icon_password, "密码", "", R.drawable.icon_word_hide) {imgView,inputView ->
                    imgView.isSelected = !imgView.isSelected
                    if (isSelected) {
                        imgView.imageResource = R.drawable.icon_word_hide
                        inputView.inputType = InputType.TYPE_CLASS_TEXT
                    } else {
                        imgView.imageResource = R.drawable.icon_word_show
                        inputView.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }
                }.invoke(this)
                textView("登  录") {
                    textColor = Color.WHITE
                    gravity = Gravity.CENTER
                    backgroundResource = R.drawable.primary_btn
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.MID_BIG)
                    onMyClick { login() }
                }.lparams(kIntWidth(0.85f), kIntHeight(0.1f)) {
                    topMargin = kIntHeight(0.025f)
                }

                textView("忘记密码了？") {
                    textColor = hostAct.getResColor(R.color.text_little_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    onMyClick { startActivity<GetBackPasswordActivity>() }
                }.lparams { }
            }
        }.view
    }

    fun lineInit(@DrawableRes icon: Int, hitiTitle: String, defTxt: String, drawableRes: Int, clickProcess: (ImageView,EditText) -> Unit): _LinearLayout.() -> EditText {
        return {
            var editText: EditText? = null
            relativeLayout {
                val rightView = imageView(drawableRes) {
                    kRandomId()
                    onMyClick { clickProcess.invoke(this,editText!!) }
                }.lparams {
                    alignParentRight()
                    centerVertically()
                }
                editText = editText(defTxt) {
                    hint = hitiTitle
                    hintTextColor = context.getResColor(R.color.text_light_black)
                    leftDrawable(icon, kIntWidth(0.02f))
                    textColor = hostAct.getResColor(R.color.text_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    backgroundColor = Color.TRANSPARENT
                    if (drawableRes != R.drawable.icon_login_delete) {
                        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }
                }.lparams(MATCH_PARENT, WRAP_CONTENT) { leftOf(rightView) }
            }.lparams(MATCH_PARENT, WRAP_CONTENT) { verticalMargin = kIntHeight(0.012f) }
            textView { backgroundColor = hostAct.getResColor(R.color.gap_line) }.lparams(MATCH_PARENT, DimensAdapter.dip1.toInt())
            editText!!
        }
    }

    fun login() {
        val phone = accountView.text.toString()
        val pw = pwView.text.toString()
        if (phone.isEmpty()) {
            hostAct.showAlertCrouton("请您填写手机号", parentLayout)
            return
        }
        if (pw.isEmpty()) {
            hostAct.showAlertCrouton("请您填写密码", parentLayout)
            return
        }
        if (!phone.isMobile()) {
            hostAct.showAlertCrouton("请您填写正确的手机号", parentLayout)
            return
        }
        verifyOpt.userLogin(phone, pw).handler(hostAct.kDefaultRestHandler(" 正在验证帐号信息,请稍等... ")).success {
            hostAct.showComfirmCrouton("登录成功!", parentLayout)
            uiThread(1000) {
                hostAct.setResult(Activity.RESULT_OK)
                hostAct.finish()
            }
        }.excute(hostAct)

    }
}

