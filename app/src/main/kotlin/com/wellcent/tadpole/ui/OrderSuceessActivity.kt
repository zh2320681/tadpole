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
import org.jetbrains.anko.*

class OrderSuceessActivity : TadpoleActivity() {
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
                val logoView = imageView(R.drawable.icon_order_success) { kRandomId() }.lparams { centerInParent() }
                val resultView = textView("订单支付成功"){
                    kRandomId()
                    textColor = Color.WHITE
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.BIGGER)
                }.lparams {  centerHorizontally()
                    bottomOf(logoView)
                    verticalMargin = kIntHeight(0.04f)}
                textView("支付方式: 支付宝  支付金额：￥2039"){
                    textColor = Color.WHITE
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                }.lparams {
                    centerHorizontally()
                    bottomOf(resultView)
                }
            }.lparams(MATCH_PARENT, kIntHeight(0.4f))
            
            verticalLayout { 
                gravity = Gravity.CENTER
                linearLayout { 
                    imageView {  }.lparams(0, WRAP_CONTENT,1f){ }
                    verticalLayout {
                        textView("支付方式: 支付宝  支付金额：￥2039"){
                            textColor = getResColor(R.color.text_little_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_SMALL)
                            lines = 2
                        }.lparams { }
                        textView("支付方式: 支付宝  支付金额：￥2039"){
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
                    onMyClick { startActivity<OrderActivity>() }
                }.lparams(kIntWidth(0.8f), kIntHeight(0.09f)) {
                    verticalMargin = kIntWidth(0.01f)
                }
            }.lparams(MATCH_PARENT, MATCH_PARENT)
        }
    }
}