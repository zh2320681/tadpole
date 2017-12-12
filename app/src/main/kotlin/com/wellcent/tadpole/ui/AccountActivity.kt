package com.wellcent.tadpole.ui

import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.ui.adapter.KFragmentPagerAdapter
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.view.KActivity
import com.shrek.klib.view.KFragment
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.presenter.ROUTINE_DATA_BINDLE
import com.wellcent.tadpole.ui.Fragment.LoginFragment
import com.wellcent.tadpole.ui.Fragment.RegisterFragment
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.viewPager
import java.lang.ref.WeakReference

class AccountActivity : KActivity() {
    lateinit var loginLabel: TextView
    lateinit var registerLabel: TextView
    lateinit var slider: View
    lateinit var viewPage: ViewPager
    lateinit var cententLayout: LinearLayout

    lateinit var loginFragment:LoginFragment
    lateinit var registerFragment:RegisterFragment
    
    override fun initialize(savedInstanceState: Bundle?) {
        verticalLayout {
            backgroundColor = Color.WHITE
            relativeLayout {
                backgroundResource = R.drawable.mf_top
                var navView = navigateBar {
                    setNavBgColor(Color.TRANSPARENT, false)
                    addLeftDefaultBtn(R.drawable.icon_back_g) {
                        finish()
                    }
                }.lparams(MATCH_PARENT, DimensAdapter.nav_height)
                imageView(R.drawable.nav_logo) { scaleType = ImageView.ScaleType.CENTER_INSIDE }.lparams { centerInParent() }
            }.lparams(MATCH_PARENT, 0, 1f)
            cententLayout = verticalLayout {
                linearLayout {
                    registerLabel = initLabel("注 册",0).invoke(this)
                    loginLabel = initLabel("登 录",1).invoke(this)
                }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.05f) }
                slider = view {
                    kRandomId()
                    backgroundColor = getResColor(R.color.colorPrimaryDark)
                }.lparams(kIntWidth(0.1f), DimensAdapter.dip2.toInt()) { topMargin = kIntHeight(0.01f) }
                textView { backgroundColor = getResColor(R.color.gap_line) }.lparams(MATCH_PARENT, DimensAdapter.dip1.toInt())
                viewPage = viewPager {
                    kRandomId()
                    registerFragment = RegisterFragment().apply { registerProcess = {
                        viewPage.setCurrentItem(1,true)
                        loginFragment.accountView.setText(it)
                    } }
                    loginFragment = LoginFragment()
                    adapter = KFragmentPagerAdapter<KFragment>(this@AccountActivity, WeakReference(this), arrayOf(registerFragment,loginFragment )) { positon, oldFragment, newFragment ->
                        val opt = if(positon == 0){ registerLabel to loginLabel } else {  loginLabel to registerLabel }
                        opt.first.textColor = getResColor(R.color.colorPrimary )
                        opt.second.textColor = getResColor(R.color.text_black )
                    }.apply {
                        onPageScrolled = { p1, p2, p3 ->
                            val rate = if (p1 == 1) { 1f } else { p2  }
                            val offsetX = (loginLabel.screenX - registerLabel.screenX) * rate + registerLabel.screenX - cententLayout.screenX
                            (slider.layoutParams as LinearLayout.LayoutParams)?.apply {
                                leftMargin = offsetX.toInt()
                                slider.layoutParams = this
                            }
                        }
                    }
                }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
            }.lparams(MATCH_PARENT, 0, 2.5f) { horizontalMargin = kIntWidth(0.05f) }
        }
        if(intent.getIntExtra(ROUTINE_DATA_BINDLE,AccountProcess.LOGIN.code) == AccountProcess.LOGIN.code){ viewPage.setCurrentItem(1,false) }
    }

    fun initLabel(title: String,pagePosition:Int): _LinearLayout.() -> TextView {
        return {
            var textView: TextView? = null
            relativeLayout {
                textView = textView(title) {
                    onMyClick { viewPage.setCurrentItem(pagePosition,true) }
                    textColor = getResColor(R.color.text_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.MID_BIG)
                }.lparams { centerInParent() }
            }.lparams(0, WRAP_CONTENT, 1f)
            textView!!
        }
    }
}

enum class AccountProcess(val code:Int) { LOGIN(999), REGISTER(998) }