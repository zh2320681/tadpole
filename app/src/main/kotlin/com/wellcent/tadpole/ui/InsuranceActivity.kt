package com.wellcent.tadpole.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.ui.adapter.HolderBo
import com.shrek.klib.ui.adapter.KAdapter
import com.shrek.klib.ui.adapter.KFragmentPagerAdapter
import com.shrek.klib.ui.kDefaultRestHandler
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.view.KActivity
import com.shrek.klib.view.KFragment
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.bo.Report
import com.wellcent.tadpole.bo.UploadImg
import com.wellcent.tadpole.presenter.AppOperable
import com.wellcent.tadpole.presenter.ROUTINE_DATA_BINDLE
import com.wellcent.tadpole.presenter.listSuccess
import com.wellcent.tadpole.presenter.success
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
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

    fun initAdapter(reports: List<Report>) {
        val listTemp = arrayListOf<InsuranceHolder>()
        reports.forEach { listTemp.add(InsuranceHolder(it)) }
        val holderList = listTemp.toTypedArray()
        viewPage.adapter = KFragmentPagerAdapter(this, WeakReference(viewPage), holderList) { positon, oldFragment, newFragment ->
            newFragment.showDetail()
        }
        if (holderList.size > 0) {
            holderList[0].showDetail()
        }
    }

    fun getReport(detail: Report?) {
        var reports: List<Report>? = null
        if (detail != null) {
            reports = arrayListOf(detail!!)
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

class InsuranceHolder() : KFragment(), AppOperable {
    lateinit var report: Report
    var detailReport: Report? = null
    lateinit var updateView: TextView
    lateinit var checkIngView: TextView
    lateinit var checkedView: TextView
    lateinit var backView: TextView
    lateinit var bankNumView: TextView
    lateinit var bankKHHView: TextView

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
                    scrollView {
                        isVerticalScrollBarEnabled = false
                        verticalLayout {
                            gravity = Gravity.CENTER
                            initStatusContent().invoke(this)
                            initContent().invoke(this)
                            initAcceptContent().invoke(this)
                        }.lparams(MATCH_PARENT, MATCH_PARENT)
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

    fun initStatusContent(): _LinearLayout.() -> Unit {
        return {
            linearLayout {
                updateView = textView("上传资料") {
                    gravity = Gravity.CENTER_HORIZONTAL
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                    textColor = context.getResColor(R.color.colorPrimary_blue)
                    topDrawable(R.drawable.icon_bx_upload, 0)
                }.lparams { }
                imageView(R.drawable.bx_dotted_line) { }.lparams { topMargin = kIntHeight(0.035f) }
                checkIngView = textView("检测中") {
                    gravity = Gravity.CENTER_HORIZONTAL
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                    textColor = context.getResColor(R.color.colorPrimary_blue)
                    topDrawable(R.drawable.icon_bx_progress, 0)
                }.lparams { }
                imageView(R.drawable.bx_dotted_line) { }.lparams { topMargin = kIntHeight(0.035f) }
                checkedView = textView("检测完成") {
                    gravity = Gravity.CENTER_HORIZONTAL
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                    textColor = context.getResColor(R.color.colorPrimary_blue)
                    topDrawable(R.drawable.icon_bx_finish, 0)
                }.lparams { }
            }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
        }
    }
    
    fun initContent(): _LinearLayout.() -> Unit {
        return {
            textView("保险项目") {
                textColor = hostAct.getResColor(R.color.text_light_black)
                textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
            }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.02f) }
            textView(report.detect_item) {
                textColor = hostAct.getResColor(R.color.text_black)
                textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                lines = 2
            }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
            textView("收款银行卡号&开户行信息") {
                textColor = hostAct.getResColor(R.color.text_light_black)
                textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
            }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.02f) }
            bankNumView = editText() {
                hint = "请输入开户行信息"
                hintTextColor = hostAct.getResColor(R.color.text_light_black)
                textColor = hostAct.getResColor(R.color.text_black)
                textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                backgroundColor = Color.TRANSPARENT
            }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
            bankKHHView = editText() {
                hint = "请输入银行卡号"
                hintTextColor = hostAct.getResColor(R.color.text_light_black)
                textColor = hostAct.getResColor(R.color.text_black)
                textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                backgroundColor = Color.TRANSPARENT
            }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
            textView("上传材料:") {
                textColor = hostAct.getResColor(R.color.text_light_black)
                textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
            }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.02f) }
            relativeLayout {
                backgroundResource = R.drawable.bx_dotted_frame
                textView("点此上传资料") {
                    textColor = hostAct.getResColor(R.color.colorPrimary_orange)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                    topDrawable(R.drawable.icon_bx_add, kIntHeight(0.01f))
                    onMyClick { }
                }.lparams { centerInParent() }
            }.lparams(MATCH_PARENT, kIntHeight(0.2f)) { verticalMargin = kIntHeight(0.02f) }
            //多个图片loading
            recyclerView {
                layoutManager = GridLayoutManager(context, 4)
                adapter = KAdapter<UploadImg, ImageUploadHolder>(arrayListOf(UploadImg(null, null, null))) {
                    itemConstructor { ImageUploadHolder(kIntWidth(0.7f) / 4) }
                    itemClickDoing { bo, i -> }
                    bindData { holder, bo, i ->
                        holder.initContent(bo)
                    }
                }
            }.lparams(MATCH_PARENT, WRAP_CONTENT) { verticalMargin = kIntHeight(0.02f) }
            textView("需要上传那些资料呢?") {
                textColor = hostAct.getResColor(R.color.colorPrimary_orange)
                textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                bottomDrawable(R.drawable.bx_dotted_line_c, 0)
            }.lparams(WRAP_CONTENT, WRAP_CONTENT) { verticalMargin = kIntHeight(0.02f) }
        }
    }

    fun initAcceptContent(): _LinearLayout.() -> Unit {
        return {
            imageView(R.drawable.icon_bx_accept){}.lparams { topMargin = kIntHeight(0.02f) }
            textView("您的保险已成功受理!"){
                textColor = hostAct.getResColor(R.color.colorPrimary_orange)
                textSize = DimensAdapter.textSpSize(CustomTSDimens.BIGGER)
            }.lparams { topMargin = kIntHeight(0.01f)  }
            textView(report.detect_item) {
                textColor = hostAct.getResColor(R.color.text_black)
                textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                lines = 2
            }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
            textView("保险款项将于15个工作日内汇入您所提供的银行卡中,如有任何疑问,请及时联系平安保险客服电话") {
                textColor = hostAct.getResColor(R.color.text_light_black)
                textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_SMALL)
                lines = 2
            }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
            textView("400-000-0000") {
                textColor = hostAct.getResColor(R.color.colorPrimary_orange)
                textSize = DimensAdapter.textSpSize(CustomTSDimens.ABNORMAL_BIG)
                leftDrawable(R.drawable.icon_bx_phone,kIntWidth(0.01f))
            }.lparams(WRAP_CONTENT, WRAP_CONTENT) { verticalMargin = kIntHeight(0.02f) }
        }
    }

    fun addInfoCell(title: String, content: String, weight: Float): _LinearLayout.() -> TextView {
        return {
            var valTextView: TextView? = null
            verticalLayout {
                textView(title) {
                    textColor = hostAct.getResColor(R.color.text_light_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                }.lparams { }
                valTextView = textView(content) {
                    textColor = hostAct.getResColor(R.color.text_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                }.lparams { }
            }.lparams(0, WRAP_CONTENT, weight)
            valTextView!!
        }
    }

    fun showDetail() {
        if (detailReport != null) {
            initValue(detailReport!!)
            return
        }
        appOpt.reportDetail(report).handler(hostAct.kDefaultRestHandler(" 正在请求报告详情,请稍等... ")).success {
            detailReport = it.detail
            initValue(detailReport!!)
        }.excute(hostAct)
    }

    fun initValue(reportData: Report) {
        if (reportData.report_status == 0) {
            checkIngView.alpha = 0.5f
        }
        if (reportData.report_status != 2) {
            checkedView.alpha = 0.5f
        }
    }
}

//上传图片的holder
class ImageUploadHolder(cellHeight: Int) : HolderBo(cellHeight) {
    lateinit var addImgLayout: RelativeLayout
    lateinit var showImgLayout: RelativeLayout
    lateinit var delImgView: ImageView

    override fun rootViewInit(): AnkoContext<Context>.() -> Unit {
        return {
            relativeLayout {
                addImgLayout = relativeLayout {
                    backgroundResource = R.drawable.bx_dotted_frame
                    imageView(R.drawable.icon_bx_add) { }.lparams { centerInParent() }
                }.lparams(MATCH_PARENT, MATCH_PARENT){ horizontalMargin = kIntWidth(0.01f) }
                showImgLayout = relativeLayout {
                    imageView { scaleType = ImageView.ScaleType.FIT_XY }.lparams { }
                    delImgView = imageView(R.drawable.icon_delete) {
                        onMyClick { }
                    }.lparams { alignParentRight() }
                }.lparams(MATCH_PARENT, MATCH_PARENT){ horizontalMargin = kIntWidth(0.01f) }
            }
        }
    }

    fun initContent(imageBo: UploadImg) {
        if(imageBo.localPath == null){
            addImgLayout.visibility = View.VISIBLE
            showImgLayout.visibility = View.GONE
        }
        if(imageBo.localPath?.isEmpty()?:false){
            addImgLayout.visibility = View.GONE
            showImgLayout.visibility = View.VISIBLE
        }
    }
}
