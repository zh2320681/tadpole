package com.wellcent.tadpole.ui

import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.app.FragmentTransaction
import android.widget.ImageView
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.getResColor
import com.shrek.klib.extension.kRandomId
import com.shrek.klib.extension.onMyClick
import com.shrek.klib.view.KFragment
import com.wellcent.tadpole.R
import com.wellcent.tadpole.ui.Fragment.MainFragment
import com.wellcent.tadpole.ui.Fragment.MineFragment
import com.wellcent.tadpole.ui.Fragment.NewsFragment
import org.jetbrains.anko.*

class MainActivity : TadpoleActivity() {
    lateinit var currFragmentRelation: FragmentRelation

    private val clickProcess:(FragmentRelation)->Unit = { switchFragment(it) }
    val report by lazy { FragmentRelation(0, R.drawable.icon_report_nor, R.drawable.icon_report_pre, MainFragment(),clickProcess) }
    val news by lazy { FragmentRelation(1, R.drawable.icon_news_nor, R.drawable.icon_news_pre, NewsFragment(),clickProcess) }
    val mine by lazy { FragmentRelation(2, R.drawable.icon_me_nor, R.drawable.icon_me_pre, MineFragment(),clickProcess) }

    override fun initialize(savedInstanceState: Bundle?) {
        relativeLayout {
            backgroundColor = getResColor(R.color.window_background)
            val tabLayout = linearLayout { kRandomId()
//                backgroundColor = Color.WHITE
                backgroundResource = R.drawable.tabbar_bg
                arrayOf(report,news,mine).forEach { it.layout().invoke(this@linearLayout) }
            }.lparams(MATCH_PARENT, WRAP_CONTENT) { alignParentBottom() }
            val contentLayout = relativeLayout { kRandomId()  }.lparams(MATCH_PARENT, MATCH_PARENT) { above(tabLayout)}
            fragmentOpt {opt-> arrayOf(report,news,mine).forEach { opt.add(contentLayout.id,it.content)}  }
        }
        currFragmentRelation = report
        showFragment(report,news,mine)
    }

    override fun onResume() {
        super.onResume()
        currFragmentRelation.content.onShow()
    }
    internal fun setCurrFragmentRelation(trans: FragmentTransaction, showFragment: FragmentRelation) {
        showFragment.select()
        if (currFragmentRelation == showFragment) { return }
        if (this.currFragmentRelation != null) {
            this.currFragmentRelation.content.onHide()
        }
        this.currFragmentRelation = showFragment
        trans.show(showFragment.content)
        showFragment.content.onShow()
    }

    protected fun switchFragment(newFragmentRelation: FragmentRelation) {
        if (currFragmentRelation == newFragmentRelation) {
            return
        }
        fragmentOpt { trans ->
            val tempFragmentRelation = currFragmentRelation
            tempFragmentRelation.select()
            if (newFragmentRelation.layoutLevel >= tempFragmentRelation.layoutLevel) {
                trans.setCustomAnimations(R.anim.right_to_left_in, R.anim.right_to_left_out,
                        R.anim.left_to_right_in, R.anim.left_to_right_out)
            } else {
                trans.setCustomAnimations(R.anim.left_to_right_in, R.anim.left_to_right_out,
                        R.anim.right_to_left_in, R.anim.right_to_left_out)
            }
            trans.hide(tempFragmentRelation.content)
            setCurrFragmentRelation(trans, newFragmentRelation)
        }
    }

    protected fun showFragment(showFragment: FragmentRelation, vararg hideFragments: FragmentRelation) {
        fragmentOpt { trans ->
            if (hideFragments != null) {
                for (fr in hideFragments) { trans.hide(fr.content) }
            }
            setCurrFragmentRelation(trans, showFragment)
        }
    }

    class FragmentRelation(var layoutLevel: Int, @DrawableRes var norIcon: Int, @DrawableRes var preIcon: Int, var content: KFragment, var clickProcess:(FragmentRelation)->Unit) {
        lateinit var imgView: ImageView
        fun layout(): _LinearLayout.() -> Unit {
            return {
                imgView = imageView(norIcon) {
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    onMyClick { clickProcess(this@FragmentRelation) }
                }.lparams(MATCH_PARENT, MATCH_PARENT, 1f)
            }
        }

        fun select() {
            with(imgView) {
                isSelected = !isSelected
                if (imgView.isSelected) {
                    imageResource = preIcon
                } else {
                    imageResource = norIcon
                }
            }
        }
    }
}
