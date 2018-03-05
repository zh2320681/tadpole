package com.wellcent.tadpole.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.gcl.dsm.ui.custom.indicator.RectangleIndicatorView
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.ui.CommonUiSetup.textSize
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
import com.wellcent.tadpole.ui.custom.rectangleIndicatorView
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.startActivityForResult
import org.jetbrains.anko.support.v4.viewPager
import java.lang.ref.WeakReference

class ReportActivity : KActivity(), AppOperable {
    lateinit var viewPage: ViewPager
    lateinit var indicatorView: RectangleIndicatorView
    override fun initialize(savedInstanceState: Bundle?) {
        val report = intent.getSerializableExtra(ROUTINE_DATA_BINDLE) as? Report
        relativeLayout {
            backgroundColor = Color.WHITE
            imageView(R.drawable.report_top_bg) { scaleType = ImageView.ScaleType.FIT_XY }.lparams(MATCH_PARENT, kIntHeight(0.3f))
            val nav = navigateBar("我的报告") {
                setTitleColor(Color.WHITE)
                setNavBgColor(Color.TRANSPARENT, false)
                addLeftDefaultBtn(R.drawable.icon_back_g) { finish() }
            }.lparams(MATCH_PARENT, DimensAdapter.nav_height) { bottomMargin = kIntHeight(0.015f) }
            viewPage = viewPager { kRandomId() }.lparams(MATCH_PARENT, MATCH_PARENT) { below(nav) }
            //底部的指示器
            indicatorView = rectangleIndicatorView(getResColor(R.color.colorPrimary_blue)) {
                kRandomId()
            }.lparams(MATCH_PARENT, WRAP_CONTENT) {
                verticalMargin = kIntHeight(0.03f)
                alignParentBottom()
            }
        }
        getReport(report)
    }

    fun initAdapter(reports: List<Report>) {
        if (reports.size == 0) {
            return
        }
        if (reports.size <= 1) {
            indicatorView.visibility = View.GONE
        } else {
            indicatorView.visibility = View.VISIBLE
            indicatorView.totalNumber = reports.size
            indicatorView.selectIndex = 0
            indicatorView.invalidate()
        }
        val listTemp = arrayListOf<ReportHolder>()
        reports.forEach { listTemp.add(ReportHolder(it)) }
        val holderList = listTemp.toTypedArray()
        viewPage.adapter = KFragmentPagerAdapter<ReportHolder>(this, WeakReference(viewPage), holderList) { position, oldFragment, newFragment ->
            indicatorView.selectIndex = position
            newFragment.showDetail()
        }
        if (holderList.size > 0) {
            holderList[0].showDetail()
        }
    }

    fun getReport(detail: Report?) {
        var reports: List<Report>? = null
        if (detail != null) {
            appOpt.reportDetail(detail!!).handler(kDefaultRestHandler(" 正在请求报表信息,请稍等... ")).success {
                initAdapter(arrayListOf(it.detail!!))
            }.excute(this)
            return
        } else {
            reports = appOpt.reportsCache()
        }
        if (reports != null) {
            initAdapter(reports!!)
        } else {
            appOpt.reports().handler(kDefaultRestHandler(" 正在请求报表列表,请稍等... ")).listSuccess {
                initAdapter(it)
            }.excute(this)
        }
    }
}


class ReportHolder() : KFragment(), AppOperable {
    val textColor1 = Color.parseColor("#D5D5D5")
    val textColor2 = Color.parseColor("#696969")
    val APPLY_INSURE = 0x98
    lateinit var report: Report
    lateinit var checkIngView: TextView
    lateinit var checkedView: TextView
    lateinit var detectNameView: TextView
    lateinit var timeView: TextView
    lateinit var unitNameView: TextView
    lateinit var itemView: TextView

    lateinit var detectLayout: LinearLayout

//    lateinit var resultView: TextView
    lateinit var introView: TextView
    lateinit var remarkView: TextView

    lateinit var noReportLayout: LinearLayout
    lateinit var resultScrollView: View
    lateinit var insuranceBtn: TextView
    lateinit var pdfBtn: TextView

    constructor(report: Report) : this() {
        this.report = report
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val parentView = UI {
            relativeLayout {
                val contentLayout = scrollView {
                    kRandomId()
                    isVerticalScrollBarEnabled = false
                    backgroundResource = R.drawable.report_bg
                    
                   verticalLayout {
                        gravity = Gravity.CENTER_HORIZONTAL
                        linearLayout {
                            checkIngView = textView("检测中") {
                                gravity = Gravity.CENTER_HORIZONTAL
                                textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                                textColor = context.getResColor(R.color.colorPrimary_blue)
                                topDrawable(R.drawable.icon_progress, 0)
                            }.lparams { }
                            imageView(R.drawable.dotted_line) { }.lparams { topMargin = kIntHeight(0.035f) }
                            checkedView = textView("检测完毕") {
                                gravity = Gravity.CENTER_HORIZONTAL
                                textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                                textColor = context.getResColor(R.color.colorPrimary_blue)
                                topDrawable(R.drawable.icon_finish, 0)
                            }.lparams { }
                        }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                        linearLayout {
                            detectNameView = addInfoCell("受检者姓名", report.detect_name ?: "", 1.7f).invoke(this)
                            timeView = addInfoCell("接收日期", report.detect_unit, 1f).invoke(this)
                        }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.02f) }
                        textView("送检单位/科室") {
                            textColor = textColor1
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                        }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.02f) }
                        unitNameView = textView(report.detect_item) {
                            textColor = textColor2
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.BIG)
                            lines = 2
                        }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                        textView("检测项目") {
                            textColor = textColor1
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                        }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.02f) }
                        itemView = textView(report.detect_item) {
                            //                        textColor = hostAct.getResColor(R.color.text_black)
                            textColor = textColor2
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.BIG)
                            lines = 2
                        }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                        //内容
                        relativeLayout {
                            noReportLayout = verticalLayout {
                                gravity = Gravity.CENTER
                                imageView(R.drawable.icon_wait) { }.lparams { }
                                textView("报告未出结果,请耐心等待") {
                                    textColor = hostAct.getResColor(R.color.colorPrimary_blue)
                                    textSize = DimensAdapter.textSpSize(CustomTSDimens.BIG)
                                }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                            }.lparams { centerInParent() }

                            resultScrollView = verticalLayout {
                                textView("检出突变结果") {
                                    textColor = textColor1
                                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                                }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.02f) }
                                detectLayout = verticalLayout { }.lparams(MATCH_PARENT, WRAP_CONTENT) {
                                    topMargin = kIntHeight(0.01f)
                                }
//                                resultView = addResultCell("检测结果", report.detect_result).invoke(this)
                                introView = addResultCell("结果解释", report.result_explain).invoke(this)
                                remarkView = addResultCell("备注", report.remark).invoke(this)
                            }.lparams(MATCH_PARENT, WRAP_CONTENT) 
//                        }.lparams(MATCH_PARENT, MATCH_PARENT)
                        }.lparams(MATCH_PARENT, MATCH_PARENT){ bottomMargin = kIntHeight(0.05f) }

                    }.lparams(MATCH_PARENT,MATCH_PARENT)
                }.lparams(kIntWidth(0.9f), kIntHeight(0.7f)) {
                    topMargin = kIntHeight(0.04f)
                    centerHorizontally()
                    horizontalMargin = kIntWidth(0.05f)
                }
                imageView(R.drawable.report_topline) { }.lparams(kIntWidth(0.89f), WRAP_CONTENT) {
                    centerHorizontally()
                    topMargin = kIntHeight(0.039f)
                }

                linearLayout {
                    gravity = Gravity.CENTER
                    textView("咨询医生") {
                        textColor = Color.WHITE
                        backgroundResource = R.drawable.primary_btn
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                        gravity = Gravity.CENTER
                        onMyClick { startActivity<AdvisoryActivity>(ROUTINE_DATA_BINDLE to true) }
                    }.lparams(MATCH_PARENT, MATCH_PARENT, 1f)
                    
                    insuranceBtn = textView("申请售后") {
                        textColor = Color.WHITE
                        backgroundResource = R.drawable.primary_btn
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                        gravity = Gravity.CENTER
                        onMyClick {
                            startActivityForResult<InsuranceActivity>(APPLY_INSURE, ROUTINE_DATA_BINDLE to report.converInsurance())
                        }
                    }.lparams(MATCH_PARENT, MATCH_PARENT, 1f)
                    pdfBtn = textView("查看PDF") {
                        textColor = Color.WHITE
                        backgroundResource = R.drawable.primary_btn
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                        gravity = Gravity.CENTER
                        isEnabled = report.pdf != null
                        onMyClick {
                            report.pdf?.apply {
                                startActivity<PDFActivity>(ROUTINE_DATA_BINDLE to report.pdf)
                            }
                        }
                    }.lparams(MATCH_PARENT, MATCH_PARENT, 1f)

                }.lparams(kIntWidth(0.9f), kIntHeight(0.1f)) {
                    centerHorizontally()
                    topMargin = kIntHeight(0.69f)
                }

            }
        }.view
        initValue()
        return parentView
    }

    fun addInfoCell(title: String, content: String, weight: Float): _LinearLayout.() -> TextView {
        return {
            var valTextView: TextView? = null
            verticalLayout {
                textView(title) {
                    //                    textColor = hostAct.getResColor(R.color.text_light_black)
                    textColor = textColor1
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                }.lparams {}
                valTextView = textView(content) {
                    //                    textColor = hostAct.getResColor(R.color.text_black)
                    textColor = textColor2
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                }.lparams {}
            }.lparams(0, WRAP_CONTENT, weight)
            valTextView!!
        }
    }

    fun addResultCell(title: String, content: String?): _LinearLayout.() -> TextView {
        return {
            var valTextView: TextView? = null
            textView(title) {
                textColor = textColor1
                textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
            }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }

            valTextView = textView(content) {
                textColor = textColor2
                textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
            }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
            valTextView!!
        }
    }

    fun showDetail() {
        appOpt.reportDetail(report).handler(hostAct.kDefaultRestHandler(" 正在请求报告详情,请稍等... ")).success {
            report = it.detail!!
            initValue()
        }.excute(hostAct)
    }

    fun initValue() {
        if (report.report_status == 0) {
            checkIngView.alpha = 0.5f
        }
        if (report.report_status != 2) {
            checkedView.alpha = 0.5f
        }
        timeView.text = report.receive_date
        unitNameView.text = report.detect_unit
        itemView.text = report.detect_item
        detectNameView.text = report.detect_name
//        resultView.text = report.detect_result
        introView.text = report.result_explain
        remarkView.text = report.remark
        pdfBtn.isEnabled = report.pdf != null
        //初始化突变结果
        detectLayout.removeAllViews()
//        val arrayTemp = report.results?.replace("[", "")?.replace("]", "") ?: ""
//        arrayTemp.split(",").forEach { }
        val txtView = WebView(hostAct).apply {
            loadData(report.results?:"", "text/html; charset=UTF-8", null)
        }
//        val txtView = TextView(hostAct).apply {
//            text = Html.fromHtml(report.results?:"")
//        }
        val llp = linearLP(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
        detectLayout.addView(txtView, llp)
        
        if (report.report_status == 0) {
            checkIngView.alpha = 0.5f
            checkedView.alpha = 0.5f
            noReportLayout.visibility = View.VISIBLE
            resultScrollView.visibility = View.GONE
            insuranceBtn.visibility = View.GONE
        } else if (report.report_status == 1) {
            checkIngView.alpha = 1f
            checkedView.alpha = 0.5f
            noReportLayout.visibility = View.VISIBLE
            resultScrollView.visibility = View.GONE
            insuranceBtn.visibility = View.GONE
        } else if (report.report_status == 2) {
            checkIngView.alpha = 1f
            checkedView.alpha = 1f
            noReportLayout.visibility = View.GONE
            resultScrollView.visibility = View.VISIBLE
            insuranceBtn.visibility = View.VISIBLE
//            if( report.claimId?.isNotEmpty()?:false ){ insuranceBtn.text = "查看保险" } else { insuranceBtn.text = "申请保险" }
        }
        insuranceBtn.isEnabled = report.canApply == 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APPLY_INSURE && resultCode == Activity.RESULT_OK) {
            showDetail()
        }
    }
}