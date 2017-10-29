package com.wellcent.tadpole.ui

import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.ViewPager
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
import com.shrek.klib.ui.kDefaultRestHandler
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.view.KActivity
import com.shrek.klib.view.KFragment
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.bo.Report
import com.wellcent.tadpole.presenter.AppOperable
import com.wellcent.tadpole.presenter.ROUTINE_DATA_BINDLE
import com.wellcent.tadpole.presenter.listSuccess
import com.wellcent.tadpole.presenter.success
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.viewPager
import java.lang.ref.WeakReference

class InsuranceActivity : KActivity(), AppOperable {
    lateinit var viewPage: ViewPager
    override fun initialize(savedInstanceState: Bundle?) {
        val report = intent.getSerializableExtra(ROUTINE_DATA_BINDLE) as? Report
        relativeLayout {
            backgroundColor = Color.WHITE
            imageView(R.drawable.bx_top_bg) { scaleType = ImageView.ScaleType.FIT_XY }.lparams(MATCH_PARENT, kIntHeight(0.3f))
            val nav = navigateBar("我的保险") {
                setTitleColor(Color.WHITE)
                setNavBgColor(Color.TRANSPARENT, false)
                addLeftDefaultBtn(R.drawable.icon_back_g) { finish() }
            }.lparams(MATCH_PARENT, DimensAdapter.nav_height) { bottomMargin = kIntHeight(0.015f) }
            viewPage = viewPager { kRandomId() }.lparams(MATCH_PARENT, MATCH_PARENT) { below(nav) }
        }
        getReport(report)
    }

    fun initAdapter(reports:List<Report>) {
        val listTemp = arrayListOf<ReportHolder>()
        reports.forEach { listTemp.add(ReportHolder(it)) }
        val holderList = listTemp.toTypedArray()
        viewPage.adapter = KFragmentPagerAdapter<ReportHolder>(this, WeakReference(viewPage), holderList) { positon, oldFragment, newFragment ->
            newFragment.showDetail()
        }
        if(holderList.size>0){ holderList[0].showDetail() }
    }
    
    fun getReport(detail: Report?) {
        var reports:List<Report>? = null
        if (detail != null) { reports = arrayListOf(detail!!) } else { reports = appOpt.reportsCache() }
        if(reports != null){ initAdapter(reports!!) } else {
            appOpt.reports().handler(kDefaultRestHandler(" 正在请求报表列表,请稍等... ")).listSuccess {
                initAdapter(it)
            }.excute(this)
        }
    } 
}

class InsuranceHolder() : KFragment(), AppOperable {
    lateinit var report: Report
    var detailReport: Report? = null
    lateinit var updateView: TextView
    lateinit var checkIngView: TextView
    lateinit var checkedView: TextView
    lateinit var backView: TextView
    lateinit var itemView: TextView
    
    constructor(report: Report) : this() {
        this.report = report
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI {
            relativeLayout {
                val contentLayout = verticalLayout {
                    kRandomId()
                    gravity = Gravity.CENTER_HORIZONTAL
                    backgroundResource = R.drawable.report_bg
                    linearLayout {
                        updateView = textView("上传资料") {
                            gravity = Gravity.CENTER_HORIZONTAL
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                            textColor = context.getResColor(R.color.colorPrimary_blue)
                            topDrawable(R.drawable.icon_bx_upload, 0)
                        }.lparams { }
                        imageView(R.drawable.dotted_line) { }.lparams { topMargin = kIntHeight(0.035f) }
                        checkIngView = textView("检测中") {
                            gravity = Gravity.CENTER_HORIZONTAL
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                            textColor = context.getResColor(R.color.colorPrimary_blue)
                            topDrawable(R.drawable.icon_bx_progress, 0)
                        }.lparams { }
                        imageView(R.drawable.dotted_line) { }.lparams { topMargin = kIntHeight(0.035f) }
                        checkedView = textView("检测完成") {
                            gravity = Gravity.CENTER_HORIZONTAL
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                            textColor = context.getResColor(R.color.colorPrimary_blue)
                            topDrawable(R.drawable.icon_bx_finish, 0)
                        }.lparams { }
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                    textView("保险项目") {
                        textColor = hostAct.getResColor(R.color.text_light_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                    }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.02f) }
                    itemView = textView(report.detect_item) {
                        textColor = hostAct.getResColor(R.color.text_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                        lines = 2
                    }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                    textView("收款银行卡开户行信息") {
                        textColor = hostAct.getResColor(R.color.text_light_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                    }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.02f) }
                    itemView = editText(report.detect_item) {
                        textColor = hostAct.getResColor(R.color.text_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                        backgroundColor = Color.TRANSPARENT
                    }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                    //内容
                    relativeLayout {
                        var noReportLayout = verticalLayout {
                            gravity = Gravity.CENTER
                            imageView(R.drawable.icon_wait) { }.lparams { }
                            textView("报告未出结果,请耐心等待") {
                                textColor = hostAct.getResColor(R.color.colorPrimary_blue)
                                textSize = DimensAdapter.textSpSize(CustomTSDimens.BIG)
                            }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                        }.lparams { centerInParent() }
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
                }.lparams(kIntWidth(0.9f), kIntHeight(0.1f)) {
                    centerHorizontally()
                    topMargin = kIntHeight(0.69f)
                }
            }
        }.view

        initValue(report)
    }

    fun addInfoCell(title: String, content: String, weight: Float): _LinearLayout.() -> TextView {
        return {
            var valTextView: TextView? = null
            verticalLayout {
                textView(title) {
                    textColor = hostAct.getResColor(R.color.text_light_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                }.lparams {}
                valTextView = textView(content) {
                    textColor = hostAct.getResColor(R.color.text_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                }.lparams {}
            }.lparams(0, WRAP_CONTENT, weight)
            valTextView!!
        }
    }
    
    fun showDetail() {
        if(detailReport != null){
            initValue(detailReport!!)
            return 
        }
        appOpt.reportDetail(report).handler(hostAct.kDefaultRestHandler(" 正在请求报告详情,请稍等... ")).success {
            detailReport = it.detail
            initValue(detailReport!!)
        }.excute(hostAct)
    }
    
    fun initValue(reportData: Report){
        if (reportData.report_status == 0) { checkIngView.alpha = 0.5f }
        if (reportData.report_status != 2) { checkedView.alpha = 0.5f }
        itemView.text = report.detect_item
    }
}