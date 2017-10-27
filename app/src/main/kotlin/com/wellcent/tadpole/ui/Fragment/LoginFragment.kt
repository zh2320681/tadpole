package com.wellcent.tadpole.ui.Fragment

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
import com.shrek.klib.ui.showAlertCrouton
import com.shrek.klib.view.KFragment
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

class LoginFragment : KFragment() {
    lateinit var accountView: EditText
    lateinit var pwView: EditText
    lateinit var parentLayout: LinearLayout
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI {
            parentLayout = verticalLayout {
                gravity = Gravity.CENTER_HORIZONTAL
                accountView = lineInit(R.drawable.icon_user, "手机号码").invoke(this)
                pwView = lineInit(R.drawable.icon_password, "密码", true).invoke(this)
                textView("登  录") {
                    textColor = Color.WHITE
                    gravity = Gravity.CENTER
                    backgroundResource = R.drawable.primary_btn
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.MID_BIG)
                    onMyClick { login() }
                }.lparams(kIntWidth(0.65f), kIntHeight(0.1f)) {
                    topMargin = kIntHeight(0.025f)
                }

                textView("忘记密码了？") {
                    textColor = hostAct.getResColor(R.color.text_little_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    onMyClick {  }
                }.lparams {  }
            }
        }.view
    }

    fun lineInit(@DrawableRes icon: Int, hitiTitle: String, isPassword: Boolean = false): _LinearLayout.() -> EditText {
        return {
            var editText: EditText? = null
            relativeLayout {
                var rightView: ImageView? = null
                if (isPassword) {
                    rightView = imageView(R.drawable.icon_word_hide) {
                        kRandomId()
                        onMyClick {
                            isSelected = !isSelected
                            if (isSelected) {
                                imageResource = R.drawable.icon_word_hide
                                editText?.inputType = InputType.TYPE_CLASS_TEXT
                            } else {
                                imageResource = R.drawable.icon_word_show
                                editText?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            }
                        }
                    }.lparams {
                        alignParentRight()
                        centerVertically()
                    }
                }
                editText = editText {
                    hint = hitiTitle
                    leftDrawable(icon, kIntWidth(0.02f))
                    textColor = hostAct.getResColor(R.color.text_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    backgroundColor = Color.TRANSPARENT
                    if (isPassword) {
                        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }
                }.lparams(MATCH_PARENT, WRAP_CONTENT) { rightView?.apply { leftOf(this) } }
            }.lparams(MATCH_PARENT, WRAP_CONTENT) { verticalMargin = kIntHeight(0.012f) }
            textView { backgroundColor = hostAct.getResColor(R.color.gap_line) }.lparams(MATCH_PARENT, DimensAdapter.dip1.toInt())
            editText!!
        }
    }
    
    fun login() {
        if(accountView.text.isEmpty()) {
            hostAct.showAlertCrouton("请您填写手机号",parentLayout)
            return 
        }
        if(pwView.text.isEmpty()) {
            hostAct.showAlertCrouton("请您填写密码",parentLayout)
            return
        }
        if(!accountView.text.toString().isMobile()) {
            hostAct.showAlertCrouton("请您填写正确的手机号",parentLayout)
            return
        }
    }
}

