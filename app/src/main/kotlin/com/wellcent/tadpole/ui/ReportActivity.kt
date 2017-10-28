package com.wellcent.tadpole.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.viewPager
import java.lang.ref.WeakReference

class ReportActivity : KActivity() {
    override fun initialize(savedInstanceState: Bundle?) {
        relativeLayout {
            backgroundColor = Color.WHITE
            imageView(R.drawable.report_top_bg) { scaleType = ImageView.ScaleType.FIT_XY }.lparams(MATCH_PARENT, kIntHeight(0.3f))
            val nav = navigateBar("我的报告") {
                setTitleColor(Color.WHITE)
                setNavBgColor(Color.TRANSPARENT, false)
                addLeftDefaultBtn(R.drawable.icon_back_g) { finish() }
            }.lparams(MATCH_PARENT, DimensAdapter.nav_height) { bottomMargin = kIntHeight(0.015f) }
            viewPager {
                kRandomId()
                adapter = KFragmentPagerAdapter<ReportHolder>(this@ReportActivity, WeakReference(this), arrayOf(ReportHolder(),ReportHolder()))
            }.lparams(MATCH_PARENT, MATCH_PARENT) { below(nav) }
        }
    }
}


class ReportHolder : KFragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI {
            relativeLayout {
                val contentLayout = verticalLayout {
                    kRandomId()
                    gravity = Gravity.CENTER_HORIZONTAL
                    backgroundResource = R.drawable.report_bg
                    linearLayout {
                        textView("检测中") {
                            gravity = Gravity.CENTER_HORIZONTAL
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                            textColor = context.getResColor(R.color.colorPrimary_blue)
                            topDrawable(R.drawable.icon_progress, 0)
                        }.lparams { }
                        imageView(R.drawable.dotted_line) { }.lparams { topMargin = kIntHeight(0.035f) }
                        textView("检测完成") {
                            gravity = Gravity.CENTER_HORIZONTAL
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                            textColor = context.getResColor(R.color.colorPrimary_blue)
                            topDrawable(R.drawable.icon_finish, 0)
                        }.lparams { }
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                    linearLayout {
                        addInfoCell("接收日期",1.7f).invoke(this)
                        addInfoCell("送检单位",1f).invoke(this)
                    }.lparams(MATCH_PARENT, WRAP_CONTENT){ topMargin = kIntHeight(0.02f) }
                    textView("检测项目"){
                        textColor = hostAct.getResColor(R.color.text_light_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                    }.lparams(MATCH_PARENT, WRAP_CONTENT){ topMargin = kIntHeight(0.02f) }
                    textView("胎儿13号染色体三体综合症"){
                        textColor = hostAct.getResColor(R.color.text_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                        lines = 3
                    }.lparams(MATCH_PARENT, WRAP_CONTENT){ topMargin = kIntHeight(0.01f)  }
                    //内容
                    relativeLayout { 
                        var noReportLayout = verticalLayout { 
                            gravity = Gravity.CENTER
                            imageView(R.drawable.icon_wait) {  }.lparams {  }
                            textView("报告未出结果,请耐心等待"){
                                textColor = hostAct.getResColor(R.color.colorPrimary_blue)
                                textSize = DimensAdapter.textSpSize(CustomTSDimens.BIG)
                            }.lparams(WRAP_CONTENT, WRAP_CONTENT){ topMargin = kIntHeight(0.01f)  }
                        }.lparams{ centerInParent() }
                    }.lparams(MATCH_PARENT, MATCH_PARENT)
                }.lparams(kIntWidth(0.9f), kIntHeight(0.7f)) {
                    topMargin = kIntHeight(0.04f)
                    centerHorizontally()
                    horizontalMargin = kIntWidth(0.05f)
                }
                
                imageView(R.drawable.report_topline) { }.lparams(kIntWidth(0.89f), WRAP_CONTENT) {
                    centerHorizontally()
                    topMargin = kIntHeight(0.039f)
                }

                textView("咨询医生") {
                    textColor = Color.WHITE
                    backgroundResource = R.drawable.primary_btn
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                    gravity = Gravity.CENTER
                    onMyClick { }
                }.lparams(kIntWidth(0.9f),kIntHeight(0.1f)){ centerHorizontally()
                    topMargin = kIntHeight(0.69f)
                }
            }
        }.view

    }
    
    fun addInfoCell(title:String,weight:Float):_LinearLayout.()->TextView {
        return {
            var valTextView:TextView? = null
            verticalLayout { 
                textView(title){
                    textColor = hostAct.getResColor(R.color.text_light_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                }.lparams{}
                valTextView = textView("2017-08-20"){
                    textColor = hostAct.getResColor(R.color.text_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                }.lparams{}
            }.lparams(0, WRAP_CONTENT,weight)
            valTextView!!
        }
    }
}