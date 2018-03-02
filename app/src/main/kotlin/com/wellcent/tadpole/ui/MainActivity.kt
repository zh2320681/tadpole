package com.wellcent.tadpole.ui

import android.content.Intent
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.app.FragmentTransaction
import android.view.View
import android.widget.ImageView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.getResColor
import com.shrek.klib.extension.kRandomId
import com.shrek.klib.extension.onMyClick
import com.shrek.klib.extension.uiThread
import com.shrek.klib.view.KFragment
import com.wellcent.tadpole.R
import com.wellcent.tadpole.presenter.VerifyOperable
import com.wellcent.tadpole.ui.Fragment.MainFragment
import com.wellcent.tadpole.ui.Fragment.MineFragment
import com.wellcent.tadpole.ui.Fragment.NewsFragment
import org.jetbrains.anko.*

class MainActivity : TadpoleActivity(),VerifyOperable {
    lateinit var currFragmentRelation: FragmentRelation

    private val clickProcess:(FragmentRelation)->Unit = { switchFragment(it) }
    val report by lazy { FragmentRelation(0, R.drawable.icon_report_nor, R.drawable.icon_report_nor, R.drawable.icon_report_pre, MainFragment(),clickProcess) }
    val news by lazy { FragmentRelation(1, R.drawable.icon_news_nor_left, R.drawable.icon_news_nor_right, R.drawable.icon_news_pre, NewsFragment(),clickProcess) }
    val mine by lazy { FragmentRelation(2, R.drawable.icon_me_nor, R.drawable.icon_report_nor, R.drawable.icon_me_pre, MineFragment(),clickProcess) }
    val guideResArray = arrayOf(R.drawable.main_guide1,R.drawable.main_guide2,R.drawable.main_guide3)
    var currGuideIndex = -1
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
            if(verifyOpt.isShowMainGuide){
                currGuideIndex = 0
                imageView(guideResArray[currGuideIndex]){
                    scaleType = ImageView.ScaleType.FIT_XY
                    onMyClick {
                        currGuideIndex++
                        if(currGuideIndex < guideResArray.size){
                            imageResource = guideResArray[currGuideIndex]
                        } else {
                            currGuideIndex = -1
                            visibility = View.GONE
                            verifyOpt.isShowMainGuide = false
                        }
                    }
                }.lparams(MATCH_PARENT, MATCH_PARENT)
            }
        }
        currFragmentRelation = report
        showFragment(report,news,mine)
    }

    override fun onResume() {
        super.onResume()
        currFragmentRelation.content.onShow()
    }
    internal fun setCurrFragmentRelation(trans: FragmentTransaction, showFragment: FragmentRelation) {
        showFragment.select(showFragment.layoutLevel)
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
            arrayOf(report,news,mine).forEach { it.select(newFragmentRelation.layoutLevel) }
//            tempFragmentRelation.select(newFragmentRelation.layoutLevel)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        currFragmentRelation.content.onActivityResult(requestCode,resultCode,data)
    }
    
    class FragmentRelation(var layoutLevel: Int, @DrawableRes var norIcon: Int, @DrawableRes var norIcon1: Int, @DrawableRes var preIcon: Int, var content: KFragment, var clickProcess:(FragmentRelation)->Unit) {
        lateinit var imgView: ImageView
        fun layout(): _LinearLayout.() -> Unit {
            return {
                imgView = imageView(norIcon) {
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    onMyClick { clickProcess(this@FragmentRelation) }
                }.lparams(MATCH_PARENT, MATCH_PARENT, 1f)
            }
        }

        fun select(selectLevel:Int) {
            with(imgView) {
                if (selectLevel == layoutLevel) {
                    imageResource = preIcon
                } else {
                    if(selectLevel < layoutLevel){ imageResource = norIcon } else { imageResource = norIcon1 }
                }
            }
        }
    }

    override fun activityBackDoing(): Boolean {
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setContentText("您确认退出程序吗?")
                .setConfirmText(" 退 出 ")
                .setConfirmClickListener {
                    finish()
//                    it.dismissWithAnimation()
                    uiThread(500){ System.exit(0) }
                }.setCancelText(" 取 消 ").show()
        return true
    }
    
}
