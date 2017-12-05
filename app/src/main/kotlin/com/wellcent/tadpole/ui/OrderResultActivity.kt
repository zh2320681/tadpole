package com.wellcent.tadpole.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.bo.GoodsPayResult
import org.jetbrains.anko.*

class OrderResultActivity : TadpoleActivity() {
    val goodsPayResult by lazy { intent.getSerializableExtra("RESULT") as GoodsPayResult }
    override fun initialize(savedInstanceState: Bundle?) {
        verticalLayout { 
            relativeLayout { 
                backgroundResource = R.drawable.order_top_bg
                textView("完成"){
                    textColor = Color.WHITE
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    onMyClick { finish() }
                }.lparams {  alignParentRight()
                    verticalMargin = kIntHeight(0.01f) 
                    horizontalMargin = kIntHeight(0.02f)}

                var resultInfo = "订单支付失败"
                var imgId = R.drawable.icon_order_info
                if(goodsPayResult.isSuccess){
                    resultInfo = "订单支付成功"
                    imgId = R.drawable.icon_order_success
                }
                val logoView = imageView(imgId) { kRandomId() }.lparams { centerInParent() }
                val resultView = textView(resultInfo){
                    kRandomId()
                    textColor = getResColor(R.color.text_color)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.BIGGER)
                }.lparams {  centerHorizontally()
                    bottomOf(logoView)
                    verticalMargin = kIntHeight(0.03f)}
                textView("支付方式: ${goodsPayResult.payType.title}  支付金额：￥${goodsPayResult.goods.price}"){
                    textColor = getResColor(R.color.text_color)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                }.lparams {
                    centerHorizontally()
                    bottomOf(resultView)
                }
            }.lparams(MATCH_PARENT, kIntHeight(0.4f))
            
            verticalLayout { 
                gravity = Gravity.CENTER
                linearLayout { 
                    imageView { _urlImg(goodsPayResult.goods.image_path) }.lparams(0, WRAP_CONTENT,1f){ }
                    verticalLayout {
                        textView(goodsPayResult.goods.name){
                            textColor = getResColor(R.color.text_little_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_SMALL)
                            lines = 2
                        }.lparams { }
                        textView("暂无描述"){
                            textColor = getResColor(R.color.text_little_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_SMALL)
                        }.lparams { topMargin = kIntHeight(0.01f) }
                    }.lparams(0, WRAP_CONTENT,3f)
                }.lparams(kIntWidth(0.8f), WRAP_CONTENT)

                textView("查看我的订单") {
                    textColor = Color.WHITE
                    backgroundResource = R.drawable.primary_btn
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                    gravity = Gravity.CENTER
                    //ROUTINE_DATA_BINDLE to goodsPayResult.orderId
                    onMyClick { 
                        startActivity<OrderActivity>() 
                        finish()
                    }
                }.lparams(kIntWidth(0.8f), kIntHeight(0.09f)) {
                    verticalMargin = kIntWidth(0.01f)
                }
            }.lparams(MATCH_PARENT, MATCH_PARENT)
        }
    }
}