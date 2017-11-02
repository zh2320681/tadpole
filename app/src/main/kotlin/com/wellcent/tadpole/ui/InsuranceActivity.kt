package com.wellcent.tadpole.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.ui.adapter.HolderBo
import com.shrek.klib.ui.adapter.KAdapter
import com.shrek.klib.ui.adapter.KFragmentPagerAdapter
import com.shrek.klib.ui.kDefaultRestHandler
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.ui.photo.PhotoChoosePop
import com.shrek.klib.view.KActivity
import com.shrek.klib.view.KFragment
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.bo.ClaimImage
import com.wellcent.tadpole.bo.Insurance
import com.wellcent.tadpole.bo.Report
import com.wellcent.tadpole.presenter.*
import com.wellcent.tadpole.ui.custom.InsuranceIntroPop
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.viewPager
import java.lang.ref.WeakReference

class InsuranceActivity : KActivity(), AppOperable {
    lateinit var viewPage: ViewPager
    lateinit var rootView: RelativeLayout
    var photoChoosePop: PhotoChoosePop? = null
    override fun initialize(savedInstanceState: Bundle?) {
        val insurance = intent.getSerializableExtra(ROUTINE_DATA_BINDLE) as? Insurance
        rootView = relativeLayout {
            backgroundColor = Color.WHITE
            imageView(R.drawable.bx_top_bg) { scaleType = ImageView.ScaleType.FIT_XY }.lparams(MATCH_PARENT, kIntHeight(0.3f))
            val nav = navigateBar("我的保险") {
                setTitleColor(Color.WHITE)
                setNavBgColor(Color.TRANSPARENT, false)
                addLeftDefaultBtn(R.drawable.icon_back_g) { finish() }
            }.lparams(MATCH_PARENT, DimensAdapter.nav_height) { bottomMargin = kIntHeight(0.015f) }
            viewPage = viewPager { kRandomId() }.lparams(MATCH_PARENT, MATCH_PARENT) { below(nav) }
        }
        getInsurances(insurance)
    }

    fun initAdapter(insurances: List<Insurance>) {
        val listTemp = arrayListOf<InsuranceHolder>()
        insurances.forEach { listTemp.add(InsuranceHolder(it)) }
        val holderList = listTemp.toTypedArray()
        viewPage.adapter = KFragmentPagerAdapter(this, WeakReference(viewPage), holderList)
    }

    fun getInsurances(insurance: Insurance?) {
        if (insurance != null) {
            var insurances = arrayListOf(insurance!!)
            initAdapter(insurances)
        } else {
            appOpt.insurances().handler(kDefaultRestHandler(" 正在请求保险列表,请稍等... ")).listSuccess {
                initAdapter(it)
            }.excute(this)
        }
    }

    fun showImgPop(imgView: ImageView, imgProcess: (String) -> Unit) {
        photoChoosePop = PhotoChoosePop(this, imgView, false, true) { imgProcess(it) }
        photoChoosePop?.show(rootView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        photoChoosePop?.onActivityResult(requestCode, resultCode, data)
    }
}

class InsuranceHolder() : KFragment(), AppOperable {
    lateinit var insurance: Insurance
    var detailReport: Report? = null
    lateinit var updateView: TextView
    lateinit var checkIngView: TextView
    lateinit var checkedView: TextView
    lateinit var backView: TextView
    lateinit var itemNameView: TextView
    lateinit var resultItemNameView: TextView
    lateinit var bankNumView: EditText
    lateinit var bankKHHView: EditText
    lateinit var acceptLayout: View
    lateinit var contentLayout: View
    lateinit var uploadBtn: TextView
    var firstImgView: ImageView? = null
    lateinit var recycleView: RecyclerView
    lateinit var uploadLayout: RelativeLayout
    lateinit var imgAdapter: KAdapter<ClaimImage, ImageUploadHolder>

    constructor(insurance: Insurance) : this() {
        this.insurance = insurance
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val parentView = UI {
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
                            acceptLayout = initAcceptContent().invoke(this)
                            contentLayout = initContent().invoke(this)
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
                uploadBtn = textView("上 传") {
                    textColor = Color.WHITE
                    backgroundResource = R.drawable.primary_btn
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                    gravity = Gravity.CENTER
                    onMyClick { beforeSubmitCheck() }
                }.lparams(kIntWidth(0.9f), kIntHeight(0.1f)) {
                    centerHorizontally()
                    topMargin = kIntHeight(0.69f)
                }
            }
        }.view
        initValue()
        insurance.claimId?.apply { showDetail(this) }
        return parentView
    }

    fun showDetail(claimId: String) {
        appOpt.insuranceDetail(claimId).handler(hostAct.kDefaultRestHandler(" 正在请求保险详情,请稍等... ")).success {
            insurance = it.detail!!
            initValue()
        }.excute(hostAct)
    }

    fun initValue() {
        if (insurance.status == 2) {
            acceptLayout.visibility = View.VISIBLE
            contentLayout.visibility = View.GONE
        } else {
            acceptLayout.visibility = View.GONE
            contentLayout.visibility = View.VISIBLE
        }
        if (insurance.status == 0) {
            checkIngView.alpha = 0.5f
        }
        if (insurance.status != 2) {
            checkedView.alpha = 0.5f
        }
        itemNameView.text = insurance.detectItemName
        bankNumView.setText(insurance.bank_card)
        bankKHHView.setText(insurance.bank_name)
        bankNumView.isEnabled = insurance.status == 0
        bankKHHView.isEnabled = insurance.status == 0
        //图片列表
        val list = arrayListOf<ClaimImage>()
        list.addAll(insurance.claimImageList)
        val isEditable = (insurance.status == 0 || insurance.status == 2)
        if (isEditable) {
            list.add(ClaimImage())
        }
        imgAdapter = KAdapter<ClaimImage, ImageUploadHolder>(list) {
            itemConstructor { ImageUploadHolder(kIntWidth(0.7f) / 4) }
            itemClickDoing { bo, i -> }
            bindData { holder, bo, i ->
                if (i == 0) {
                    firstImgView = holder.showImg
                }
                holder.initContent(bo, isEditable, { selectImage(it) }, { delImage(i) })
            }
        }
        recycleView.adapter = imgAdapter
        if (insurance.claimImageList.size == 0) {
            uploadLayout.visibility = View.VISIBLE
        } else {
            uploadLayout.visibility = View.GONE
        }
        resultItemNameView.text = insurance.detectItemName
        if (insurance.status == 1 || insurance.status == 2) {
            uploadBtn.visibility = View.GONE
        } 
    }

    fun beforeSubmitCheck() {
        if (bankNumView.text.isEmpty()) {
            hostAct.toastLongShow("您还未输入银行卡号")
            return
        }
        if (bankKHHView.text.toString().isEmpty()) {
            hostAct.toastLongShow("您还未输入开户行信息")
            return
        }
        if (imgAdapter.itemCount == 0) {
            hostAct.toastLongShow("您还未上传资料")
            return
        }
        submitImgs()
    }

    //提交
    fun submit() {
        val backNum = bankNumView.text.toString()
        val bankKHH = bankKHHView.text.toString()
        appOpt.saveInsurance(insurance.detect_item_id, insurance.reportId, backNum, bankKHH).handler(
                hostAct.kDefaultRestHandler(" 正在提交保险信息,请稍等... ")).success {
            //            hostAct.toastLongShow("保险信息提交成功!")
            SweetAlertDialog(hostAct, SweetAlertDialog.SUCCESS_TYPE)
                    .setContentText("保险申报成功!")
                    .setConfirmText(" 确 定 ")
                    .setConfirmClickListener {
                        hostAct.setResult(Activity.RESULT_OK)
                        hostAct.finish()
                    }.setCancelText(" 取 消 ").show()
        }.excute(hostAct)
    }

    //提交图片
    fun submitImgs() {
        var urls = arrayListOf<String>()
        var localFiles = arrayListOf<String>()
        imgAdapter.sourceData.forEach {
            if (it.image_path.isNotEmpty()) {
                urls.add(it.image_path)
            }
            if (it.localImgPath?.isNotEmpty() ?: false) {
                localFiles.add(it.localImgPath!!)
            }
        }
        appOpt.insuranceSaveImgs(insurance.detect_item_id, insurance.reportId, urls, localFiles).handler(
                hostAct.kDefaultRestHandler(" 正在上传保险资料,请稍等... ")).success {
            hostAct.toastLongShow("保险资料上传成功!")
            submit()
        }.excute(hostAct)
    }

    fun initStatusContent(): _LinearLayout.() -> Unit {
        return {
            linearLayout {
                updateView = textView("上传资料") {
                    gravity = Gravity.CENTER_HORIZONTAL
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                    textColor = context.getResColor(R.color.colorPrimary_orange)
                    topDrawable(R.drawable.icon_bx_upload, 0)
                }.lparams { }
                imageView(R.drawable.bx_dotted_line) { }.lparams { topMargin = kIntHeight(0.035f) }
                checkIngView = textView("检测中") {
                    gravity = Gravity.CENTER_HORIZONTAL
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                    textColor = context.getResColor(R.color.colorPrimary_orange)
                    topDrawable(R.drawable.icon_bx_progress, 0)
                }.lparams { }
                imageView(R.drawable.bx_dotted_line) { }.lparams { topMargin = kIntHeight(0.035f) }
                checkedView = textView("检测完成") {
                    gravity = Gravity.CENTER_HORIZONTAL
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                    textColor = context.getResColor(R.color.colorPrimary_orange)
                    topDrawable(R.drawable.icon_bx_finish, 0)
                }.lparams { }
            }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
        }
    }

    fun initContent(): _LinearLayout.() -> View {
        return {
            verticalLayout {
                gravity = Gravity.CENTER_HORIZONTAL
                textView("保险项目") {
                    textColor = hostAct.getResColor(R.color.text_light_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.02f) }
                itemNameView = textView(insurance.detectItemName) {
                    textColor = hostAct.getResColor(R.color.text_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    lines = 2
                }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                textView("收款银行卡号&开户行信息") {
                    textColor = hostAct.getResColor(R.color.text_light_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                bankNumView = editText(insurance.bank_card) {
                    hint = "请输入开户行信息"
                    hintTextColor = hostAct.getResColor(R.color.text_light_black)
                    textColor = hostAct.getResColor(R.color.text_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    backgroundColor = Color.TRANSPARENT
                }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                bankKHHView = editText(insurance.bank_name) {
                    hint = "请输入银行卡号"
                    hintTextColor = hostAct.getResColor(R.color.text_light_black)
                    textColor = hostAct.getResColor(R.color.text_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    backgroundColor = Color.TRANSPARENT
                }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                textView("上传材料:") {
                    textColor = hostAct.getResColor(R.color.text_light_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }

                relativeLayout {
                    //多个图片loading
                    recycleView = recyclerView {
                        layoutManager = GridLayoutManager(context, 4)
                        addItemDecoration(HorizontalDividerItemDecoration.Builder(context).color(Color.TRANSPARENT).build())
//                    if(insurance.claimImageList.size == 0){ visibility = View.GONE } else { visibility = View.VISIBLE }
                    }.lparams(MATCH_PARENT, WRAP_CONTENT) { verticalMargin = kIntHeight(0.01f) }

                    uploadLayout = relativeLayout {
                        onMyClick { selectImage(firstImgView!!) }
                        backgroundResource = R.drawable.bx_dotted_frame
                        textView("点此上传资料") {
                            textColor = hostAct.getResColor(R.color.colorPrimary_orange)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                            topDrawable(R.drawable.icon_bx_add, kIntHeight(0.01f))
                        }.lparams { centerInParent() }
                    }.lparams(MATCH_PARENT, kIntHeight(0.15f)) { verticalMargin = kIntHeight(0.01f) }
                }.lparams(MATCH_PARENT, WRAP_CONTENT) { verticalMargin = kIntHeight(0.018f) }

                textView("需要上传那些资料呢?") {
                    textColor = hostAct.getResColor(R.color.colorPrimary_orange)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    bottomDrawable(R.drawable.bx_dotted_line_c, 0)
                    onMyClick { (hostAct as? InsuranceActivity)?.apply { InsuranceIntroPop(hostAct).show(it.rootView) } }
                }.lparams(WRAP_CONTENT, WRAP_CONTENT) {
                    topMargin = kIntHeight(0.01f)
                    bottomMargin = kIntHeight(0.04f)
                }
            }.lparams(MATCH_PARENT, WRAP_CONTENT)
        }
    }

    fun initAcceptContent(): _LinearLayout.() -> View {
        return {
            verticalLayout {
                gravity = Gravity.CENTER_HORIZONTAL
                imageView(R.drawable.icon_bx_accept) {}.lparams { topMargin = kIntHeight(0.02f) }
                textView("您的保险已成功受理!") {
                    textColor = hostAct.getResColor(R.color.colorPrimary_orange)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.BIGGER)
                }.lparams { topMargin = kIntHeight(0.01f) }
                resultItemNameView = textView(insurance.detectItemName) {
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
                    leftDrawable(R.drawable.icon_bx_phone, kIntWidth(0.01f))
                }.lparams(WRAP_CONTENT, WRAP_CONTENT) { verticalMargin = kIntHeight(0.02f) }
            }.lparams(MATCH_PARENT, WRAP_CONTENT)
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

    fun selectImage(imgView: ImageView) {
        (hostAct as? InsuranceActivity)?.apply {
            showImgPop(imgView) {
                uploadLayout.visibility = View.GONE
                recycleView.visibility = View.VISIBLE
                val bo = ClaimImage().apply { localImgPath = it }
                (imgAdapter.sourceData as? ArrayList)?.add(maxOf(imgAdapter.itemCount - 1, 0), bo)
                imgAdapter.notifyDataSetChanged()
            }
        }
    }

    fun delImage(position: Int) {
        val dialog = SweetAlertDialog(hostAct, SweetAlertDialog.WARNING_TYPE)
                .setContentText("您确认删除图片吗?")
                .setConfirmText(" 删 除 ")
        dialog.setConfirmClickListener {
            (imgAdapter.sourceData as? ArrayList)?.removeAt(position)
            imgAdapter.notifyDataSetChanged()
            if (imgAdapter.itemCount <= 1) {
                uploadLayout.visibility = View.VISIBLE
                recycleView.visibility = View.GONE
            }
            dialog.dismiss()
        }.setCancelText(" 取 消 ")
        dialog.show()
    }

    fun initData() {
        if (insurance.status == 0) {

        } else if (insurance.status == 1) {

        }
    }
}

//上传图片的holder
class ImageUploadHolder(cellHeight: Int) : HolderBo(cellHeight) {
    lateinit var addImgLayout: RelativeLayout
    lateinit var showImgLayout: RelativeLayout
    lateinit var showImg: ImageView
    lateinit var delImgView: ImageView

    override fun rootViewInit(): AnkoContext<Context>.() -> Unit {
        return {
            relativeLayout {
                addImgLayout = relativeLayout {
                    backgroundResource = R.drawable.bx_dotted_frame
                    imageView(R.drawable.icon_bx_add) { }.lparams { centerInParent() }
                }.lparams(MATCH_PARENT, MATCH_PARENT) { horizontalMargin = kIntWidth(0.01f) }
                showImgLayout = relativeLayout {
                    showImg = imageView(R.drawable.icon_headshot) { scaleType = ImageView.ScaleType.FIT_XY }.lparams(MATCH_PARENT, MATCH_PARENT) { }
                    delImgView = imageView(R.drawable.icon_delete) {
                        onMyClick { }
                    }.lparams { alignParentRight() }
                }.lparams(MATCH_PARENT, MATCH_PARENT) { horizontalMargin = kIntWidth(0.01f) }
            }
        }
    }

    fun initContent(imageBo: ClaimImage, isEnable: Boolean, addClick: (ImageView) -> Unit, reduceClick: () -> Unit) {
        val serPathNotExist = imageBo.image_path.isEmpty()
        if (imageBo.localImgPath == null && serPathNotExist) {
            addImgLayout.visibility = View.VISIBLE
            showImgLayout.visibility = View.GONE
            addImgLayout.onMyClick { addClick(showImg) }
        } else {
            addImgLayout.visibility = View.GONE
            showImgLayout.visibility = View.VISIBLE
            showImg._urlImg(imageBo.image_path.serPicPath())
            if (!isEnable) {
                delImgView.visibility = View.GONE
            } else {
                delImgView.visibility = View.VISIBLE
                delImgView.onMyClick { reduceClick() }
            }
        }
    }
}
