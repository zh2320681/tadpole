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
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.colligate.span.setText
import com.shrek.klib.colligate.span.style
import com.shrek.klib.extension.*
import com.shrek.klib.ui.kDefaultRestHandler
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.bo.DetectUnit
import com.wellcent.tadpole.bo.Goods
import com.wellcent.tadpole.bo.ReqMapping
import com.wellcent.tadpole.presenter.AppOperable
import com.wellcent.tadpole.presenter.ROUTINE_DATA_BINDLE
import com.wellcent.tadpole.presenter.serPicPath
import com.wellcent.tadpole.ui.custom.UnitChoosePop
import org.jetbrains.anko.*

class GoodsAcvtivity : TadpoleActivity(),AppOperable {
    lateinit var topBgView: RelativeLayout
    lateinit var topBgImageView:ImageView
    lateinit var goodsNameView:TextView
    lateinit var markView:TextView
    lateinit var rootView: View
    lateinit var unitnNameView:TextView
    lateinit var priceView:TextView
    lateinit var payLayout:View
    var payTypeViews = arrayListOf<ImageView>()
    var selectUnit:DetectUnit? = null
    
    val goodsId by lazy { intent.getStringExtra(ROUTINE_DATA_BINDLE) }
    override fun initialize(savedInstanceState: Bundle?) {
        rootView = relativeLayout {
            topBgView = relativeLayout {
                kRandomId()
                topBgImageView = imageView{ scaleType = ImageView.ScaleType.FIT_XY }.lparams(MATCH_PARENT, MATCH_PARENT){ }
                navigateBar("") {
                    setNavBgColor(Color.TRANSPARENT, false)
                    addLeftDefaultBtn(R.drawable.icon_back_p) { finish() }
                }.lparams(MATCH_PARENT,DimensAdapter.nav_height)
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
            val bottomLayout = linearLayout { kRandomId()
                gravity = Gravity.CENTER
                backgroundResource = R.drawable.tabbar_bg
                textView("选择购买") {
                    textColor = Color.WHITE
                    backgroundResource = R.drawable.primary_btn
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                    gravity = Gravity.CENTER
                    onMyClick { payLayout.visibility = View.VISIBLE }
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
                            val unitChoosePop = UnitChoosePop(this@GoodsAcvtivity){
                                selectUnit = it
                                unitnNameView.text = it?.name
                            }
                            unitChoosePop.show(rootView)
                        }
                        textView("请选择检测机构") {
                            textColor = getResColor(R.color.text_little_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                        }.lparams(WRAP_CONTENT, WRAP_CONTENT) {  }
                        unitnNameView = textView("") {
                            textColor = getResColor(R.color.text_little_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                            rightDrawable(R.drawable.icon_right_arrows,kIntWidth(0.02f))
                        }.lparams(WRAP_CONTENT, WRAP_CONTENT) {  alignParentRight()
                            centerVertically() }
                    }.lparams(MATCH_PARENT, WRAP_CONTENT){ }
                    textView { backgroundColor = getResColor(R.color.gap_line) }.lparams(MATCH_PARENT, DimensAdapter.dip1.toInt())
                    //备注
                    markView = textView("") {
                        textColor = getResColor(R.color.text_light_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                }.lparams(MATCH_PARENT, WRAP_CONTENT)
            }.lparams(MATCH_PARENT, MATCH_PARENT){
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
                    payTypeViews.add( addPayLayout("  支付宝",R.drawable.icon_alipay).invoke(this) )
                    payTypeViews.add( addPayLayout("  微信",R.drawable.icon_tenpay).invoke(this) )
                    textView() {
                        setText("应付金额".style { foregroundColor(getResColor(R.color.text_color)) } +
                                "￥2037".style { foregroundColor(Color.RED) })
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                        gravity = Gravity.CENTER
                    }.lparams(MATCH_PARENT, WRAP_CONTENT) {
                        verticalMargin = kIntHeight(0.02f)
                        horizontalMargin = kIntWidth(0.02f)
                    }
                    view{backgroundColor = getResColor(R.color.window_background)}.lparams(MATCH_PARENT,kIntHeight(0.01f))
                    linearLayout { 
                        textView("取消"){
                            textColor = getResColor(R.color.text_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                            gravity = Gravity.CENTER
                            verticalPadding = kIntHeight(0.02f)
                            onMyClick {  payLayout.visibility = View.GONE }
                        }.lparams(MATCH_PARENT, MATCH_PARENT,1f)
                        textView("下一步"){
                            textColor = getResColor(R.color.text_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                            gravity = Gravity.CENTER
                            verticalPadding = kIntHeight(0.02f)
                            onMyClick {  startActivity<OrderSuceessActivity>() }
                        }.lparams(MATCH_PARENT, MATCH_PARENT,1f)
                    }.lparams(MATCH_PARENT, WRAP_CONTENT){  }
                }.lparams(MATCH_PARENT, WRAP_CONTENT){
                    alignParentBottom()
                }
            }.lparams(MATCH_PARENT, MATCH_PARENT)
        }
        goodsDetail()
    }
    
    fun addPayLayout(title:String,icon:Int): _LinearLayout.()->ImageView {
        return {
            var imgView:ImageView? = null
            relativeLayout {
                textView(title) {
                    textColor = getResColor(R.color.text_little_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    gravity = Gravity.CENTER_VERTICAL
                    leftDrawable(icon,kIntWidth(0.01f))
                }.lparams(WRAP_CONTENT, WRAP_CONTENT)
                imgView = imageView(R.drawable.radio_btn){ 
                    kRandomId()
                    onMyClick { payTypeViews.forEach { 
                       isSelected = (it.id == this.id) 
                    } }
                }.lparams { alignParentRight()
                    centerVertically() }
            }.lparams(MATCH_PARENT, WRAP_CONTENT){ 
                verticalMargin = kIntHeight(0.01f)
                horizontalMargin = kIntWidth(0.02f)
            }
            imgView!!
        }
    }
    
    fun goodsDetail(){
        val handler = kDefaultRestHandler<ReqMapping<Goods>>("正在请求详情信息,请稍等...")
        appOpt.goods(this,goodsId,handler){
            if(it == null){
                SweetAlertDialog(this@GoodsAcvtivity, SweetAlertDialog.SUCCESS_TYPE)
                        .setContentText("未找到详情信息,请稍候再试!")
                        .setConfirmText(" 确 定 ")
                        .setConfirmClickListener {
                            finish()
                        }.show()
            } else {
                priceView.text = "￥${it!!.price}"
                topBgImageView._urlImg(it!!.image_path.serPicPath())
                goodsNameView.text = it.name
                markView.text = Html.fromHtml(it!!.remark)
            }
        }
    }
    
}