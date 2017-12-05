package com.wellcent.tadpole.ui

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.alipay.sdk.app.PayTask
import com.shrek.klib.colligate.BaseUtils
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.colligate.span.setText
import com.shrek.klib.colligate.span.style
import com.shrek.klib.extension.*
import com.shrek.klib.ui.kDefaultRestHandler
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.ui.showAlertCrouton
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.wellcent.tadpole.R
import com.wellcent.tadpole.bo.*
import com.wellcent.tadpole.presenter.AppOperable
import com.wellcent.tadpole.presenter.ROUTINE_DATA_BINDLE
import com.wellcent.tadpole.presenter.serPicPath
import com.wellcent.tadpole.presenter.success
import com.wellcent.tadpole.ui.custom.UnitChoosePop
import com.wellcent.tadpole.wxapi.WXPayEntryActivity
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.*

class GoodsAcvtivity : TadpoleActivity(), AppOperable {
    lateinit var topBgView: RelativeLayout
    lateinit var topBgImageView: ImageView
    lateinit var goodsNameView: TextView
    lateinit var markView: TextView
    lateinit var rootView: View
    lateinit var unitnNameView: TextView
    lateinit var priceView: TextView
    lateinit var payLayout: View
    var payTypeViews = arrayListOf<ImageView>()
    var selectUnit: DetectUnit? = null

    val goodsId by lazy { intent.getStringExtra(ROUTINE_DATA_BINDLE) }
    var goods: Goods? = null

    override fun initialize(savedInstanceState: Bundle?) {
        rootView = relativeLayout {
            topBgView = relativeLayout {
                kRandomId()
                topBgImageView = imageView { scaleType = ImageView.ScaleType.FIT_XY }.lparams(MATCH_PARENT, MATCH_PARENT) { }
                navigateBar("") {
                    setNavBgColor(Color.TRANSPARENT, false)
                    addLeftDefaultBtn(R.drawable.icon_back_p) { finish() }
                }.lparams(MATCH_PARENT, DimensAdapter.nav_height)
                priceView = textView {
                    gravity = Gravity.CENTER
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    textColor = Color.WHITE
                    backgroundResource = R.drawable.goods_price_bg
                }.lparams {
                    alignParentBottom()
                    alignParentRight()
                    horizontalMargin = kIntWidth(0.02f)
                    verticalMargin = kIntHeight(0.02f)
                }
            }.lparams(MATCH_PARENT, kIntHeight(0.3f))
            val bottomLayout = linearLayout {
                kRandomId()
                gravity = Gravity.CENTER
                backgroundResource = R.drawable.tabbar_bg
                textView("选择购买") {
                    textColor = Color.WHITE
                    backgroundResource = R.drawable.primary_btn
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                    gravity = Gravity.CENTER
                    onMyClick {
                        if (!BaseUtils.isStringValid(unitnNameView.text.toString())) {
                            showAlertCrouton("请先选择检测机构!")
                            return@onMyClick
                        }
                        payLayout.visibility = View.VISIBLE
                    }
                }.lparams(kIntWidth(0.8f), kIntHeight(0.09f)) {
                    verticalMargin = kIntWidth(0.01f)
                }
            }.lparams(MATCH_PARENT, WRAP_CONTENT) { alignParentBottom() }
            scrollView {
                verticalLayout {
                    horizontalPadding = kIntWidth(0.02f)
                    goodsNameView = textView("") {
                        verticalPadding = kIntHeight(0.02f)
                        textColor = getResColor(R.color.text_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.02f) }
                    textView { backgroundColor = getResColor(R.color.gap_line) }.lparams(MATCH_PARENT, DimensAdapter.dip1.toInt())
                    relativeLayout {
                        verticalPadding = kIntHeight(0.02f)
                        onMyClick {
                            val unitChoosePop = UnitChoosePop(this@GoodsAcvtivity) {
                                selectUnit = it
                                unitnNameView.text = it?.name
                            }
                            unitChoosePop.show(rootView)
                        }
                        textView("请选择检测机构") {
                            textColor = getResColor(R.color.text_little_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                        }.lparams(WRAP_CONTENT, WRAP_CONTENT) { }
                        unitnNameView = textView("") {
                            textColor = getResColor(R.color.text_little_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                            rightDrawable(R.drawable.icon_right_arrows, kIntWidth(0.02f))
                        }.lparams(WRAP_CONTENT, WRAP_CONTENT) {
                            alignParentRight()
                            centerVertically()
                        }
                    }.lparams(MATCH_PARENT, WRAP_CONTENT) { }
                    textView { backgroundColor = getResColor(R.color.gap_line) }.lparams(MATCH_PARENT, DimensAdapter.dip1.toInt())
                    //备注
                    markView = textView("") {
                        textColor = getResColor(R.color.text_light_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                }.lparams(MATCH_PARENT, WRAP_CONTENT)
            }.lparams(MATCH_PARENT, MATCH_PARENT) {
                above(bottomLayout)
                below(topBgView)
            }

            //支付方式选择
            payLayout = relativeLayout {
                visibility = View.GONE
                backgroundColor = Color.parseColor("#60000000")
                verticalLayout {
                    backgroundColor = Color.WHITE
                    textView("选择支付方式") {
                        textColor = getResColor(R.color.text_light_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) {
                        topMargin = kIntHeight(0.05f)
                        bottomMargin = kIntHeight(0.02f)
                        horizontalMargin = kIntWidth(0.02f)
                    }
                    payTypeViews.add(addPayLayout("  支付宝", R.drawable.icon_alipay, true).invoke(this))
                    payTypeViews.add(addPayLayout("  微信", R.drawable.icon_tenpay).invoke(this))
                    textView() {
                        setText("应付金额".style { foregroundColor(getResColor(R.color.text_color)) } +
                                "￥2037".style { foregroundColor(Color.RED) })
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                        gravity = Gravity.CENTER
                    }.lparams(MATCH_PARENT, WRAP_CONTENT) {
                        verticalMargin = kIntHeight(0.02f)
                        horizontalMargin = kIntWidth(0.02f)
                    }
                    view { backgroundColor = getResColor(R.color.window_background) }.lparams(MATCH_PARENT, kIntHeight(0.01f))
                    linearLayout {
                        textView("取消") {
                            textColor = getResColor(R.color.text_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                            gravity = Gravity.CENTER
                            verticalPadding = kIntHeight(0.02f)
                            onMyClick { payLayout.visibility = View.GONE }
                        }.lparams(MATCH_PARENT, MATCH_PARENT, 1f)
                        textView("下一步") {
                            textColor = getResColor(R.color.text_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                            gravity = Gravity.CENTER
                            verticalPadding = kIntHeight(0.02f)
                            onMyClick {
                                var position = 0
                                payTypeViews.forEachIndexed { index, imageView -> if (imageView.isSelected) position = index }
                                if (position == 0) {
                                    alipay()
                                } else {
                                    wxPay()
                                }
                            }
                        }.lparams(MATCH_PARENT, MATCH_PARENT, 1f)
                    }.lparams(MATCH_PARENT, WRAP_CONTENT) { }
                }.lparams(MATCH_PARENT, WRAP_CONTENT) {
                    alignParentBottom()
                }
            }.lparams(MATCH_PARENT, MATCH_PARENT)
        }
        goodsDetail()
    }

    fun addPayLayout(title: String, icon: Int, isDefautSelect: Boolean = false): _LinearLayout.() -> ImageView {
        return {
            var imgView: ImageView? = null
            relativeLayout {
                textView(title) {
                    textColor = getResColor(R.color.text_little_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    gravity = Gravity.CENTER_VERTICAL
                    leftDrawable(icon, kIntWidth(0.01f))
                }.lparams(WRAP_CONTENT, WRAP_CONTENT)
                imgView = imageView(R.drawable.radio_btn) {
                    kRandomId()
                    isSelected = isDefautSelect
                    onMyClick {
                        payTypeViews.forEach { view ->
                            view.isSelected = (view.id == this@imageView.id)
                        }
                    }
                }.lparams {
                    alignParentRight()
                    centerVertically()
                }
            }.lparams(MATCH_PARENT, WRAP_CONTENT) {
                verticalMargin = kIntHeight(0.01f)
                horizontalMargin = kIntWidth(0.02f)
            }
            imgView!!
        }
    }

    fun goodsDetail() {
        val handler = kDefaultRestHandler<ReqMapping<Goods>>("正在请求详情信息,请稍等...")
        appOpt.goods(this, goodsId, handler) {
            if (it == null) {
                SweetAlertDialog(this@GoodsAcvtivity, SweetAlertDialog.SUCCESS_TYPE)
                        .setContentText("未找到详情信息,请稍候再试!")
                        .setConfirmText(" 确 定 ")
                        .setConfirmClickListener {
                            finish()
                        }.show()
            } else {
                this.goods = it
                priceView.text = "￥${it!!.price}"
                topBgImageView._urlImg(it!!.image_path.serPicPath())
                goodsNameView.text = it.name
                markView.text = Html.fromHtml(it!!.remark)
            }
        }
    }

    fun alipay() {
        appOpt.aliPayOrder(goodsId, selectUnit!!.id).handler(kDefaultRestHandler("获取支付宝支付信息中,请稍等...")).success {
            val callAlipay = async(CommonPool) {
                val alipay = PayTask(this@GoodsAcvtivity)
                AlipyPayResult(alipay.payV2(it.orderStr, true))
            }
            launch(UI) {
                val isPaySuccess = callAlipay.await().resultStatus.equals("9000", true)
                if(isPaySuccess){
                    val payResult = GoodsPayResult(isPaySuccess, PayType.ALIPAY, goods!!)
                    startActivity<OrderResultActivity>("RESULT" to payResult)
                    finish()
                } else { showAlertCrouton("付款失败!") }
            }
        }.excute(this)
    }

    fun wxPay() {
        appOpt.wxPayOrder(goodsId, selectUnit!!.id).handler(kDefaultRestHandler("获取微信支付信息中,请稍等...")).success {
            it.orderStr?.also { detailTemp ->
                val msgApi = WXAPIFactory.createWXAPI(this, detailTemp.appid,false)
                msgApi.registerApp(detailTemp.appid)
                val request = PayReq().apply {
                    appId = detailTemp.appid
                    partnerId = "1491963982"
                    prepayId = detailTemp.prepayid
                    packageValue = "Sign=WXPay"
                    nonceStr = detailTemp.noncestr
                    timeStamp = detailTemp.timestamp
                    sign = detailTemp.sign
                }
                msgApi.sendReq(request)
                WXPayEntryActivity.goodsPayResult = GoodsPayResult(true, PayType.WEICHAT, goods!!)
                finish()
            }
        }.excute(this)
    }
}

class AlipyPayResult(rawResult: Map<String, String>?) {
    var resultStatus: String? = null
    var result: String? = null
    var memo: String? = null

    init {
        if (rawResult != null) {
            resultStatus = rawResult["resultStatus"]
            result = rawResult["result"]
            memo = rawResult["memo"]
        }
    }
}