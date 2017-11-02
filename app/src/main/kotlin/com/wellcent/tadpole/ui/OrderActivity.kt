package com.wellcent.tadpole.ui

import android.graphics.Color
import android.graphics.Paint
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
import com.wellcent.tadpole.bo.Order
import com.wellcent.tadpole.presenter.AppOperable
import com.wellcent.tadpole.presenter.listSuccess
import com.wellcent.tadpole.presenter.serPicPath
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.viewPager
import java.lang.ref.WeakReference

class OrderActivity : KActivity(), AppOperable {
    lateinit var viewPage: ViewPager
    override fun initialize(savedInstanceState: Bundle?) {
        relativeLayout {
            backgroundColor = Color.WHITE
            imageView(R.drawable.order_top_bg) { scaleType = ImageView.ScaleType.FIT_XY }.lparams(MATCH_PARENT, kIntHeight(0.3f))
            val nav = navigateBar("我的订单") {
                setTitleColor(Color.WHITE)
                setNavBgColor(Color.TRANSPARENT, false)
                addLeftDefaultBtn(R.drawable.icon_back_g) { finish() }
            }.lparams(MATCH_PARENT, DimensAdapter.nav_height) { bottomMargin = kIntHeight(0.015f) }
            viewPage = viewPager {
                kRandomId()
            }.lparams(MATCH_PARENT, MATCH_PARENT) { below(nav) }
        }

        appOpt.orders().handler(kDefaultRestHandler(" 正在请求订单列表,请稍等... ")).listSuccess {
            val orderFragments = arrayListOf<OrderHolder>()
            it.forEach { orderFragments.add(OrderHolder(it)) }
            viewPage.adapter = KFragmentPagerAdapter<OrderHolder>(this@OrderActivity, WeakReference(viewPage), orderFragments.toTypedArray())
        }.excute(this)
    }
}

class OrderHolder() : KFragment() {
    lateinit var order: Order

    constructor(order: Order) : this() {
        this.order = order
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI {
            relativeLayout {
                relativeLayout {
                    backgroundResource = R.drawable.order_bg
                    imageView() {
                        scaleType = ImageView.ScaleType.FIT_XY
                        _urlImg(order.imagePath.serPicPath())
                    }.lparams(MATCH_PARENT, kIntHeight(0.25f)) { }
                    val statusImg = if (!order.isUsed) R.drawable.order_state0 else R.drawable.order_status1
                    imageView(statusImg) { }.lparams { alignParentRight() }
                    val maskedView = view {
                        kRandomId()
                        backgroundColor = Color.parseColor("#40000000")
                        if(!order.isUsed){ visibility = View.INVISIBLE }
                    }.lparams(MATCH_PARENT, kIntHeight(0.25f)) { }

                    val hMargin = kIntWidth(0.03f)
                    val titleView = textView(order.detectItemName) {
                        kRandomId()
                        textColor = hostAct.getResColor(R.color.text_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.BIG)
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) {
                        topMargin = kIntHeight(0.02f)
                        below(maskedView)
                        horizontalMargin = hMargin
                    }
                    val hosView = textView(order.detectUnitName) {
                        kRandomId()
                        textColor = hostAct.getResColor(R.color.text_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) {
                        topMargin = kIntHeight(0.02f)
                        below(titleView)
                        leftMargin = hMargin
                    }
                    val priceView = textView(order.price) {
                        kRandomId()
                        textColor = hostAct.getResColor(R.color.text_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) {
                        topMargin = kIntHeight(0.018f)
                        below(titleView)
                        alignParentRight()
                        rightMargin = hMargin
                    }
                    val gapLine = view {
                        kRandomId()
                        backgroundColor = hostAct.getResColor(R.color.gap_line)
                    }.lparams(MATCH_PARENT, DimensAdapter.dip1.toInt()) {
                        topMargin = kIntHeight(0.02f)
                        below(priceView)
                        horizontalMargin = hMargin
                    }
                    val orderCreateView = textView("订单创建时间:  ${ order.create_time}") {
                        kRandomId()
                        textColor = hostAct.getResColor(R.color.text_light_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) {
                        topMargin = kIntHeight(0.02f)
                        below(gapLine)
                        horizontalMargin = hMargin
                    }
                    val orderPayView = textView("订单支付时间:  ${ order.pay_time}") {
                        kRandomId()
                        textColor = hostAct.getResColor(R.color.text_light_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) {
                        topMargin = kIntHeight(0.01f)
                        below(orderCreateView)
                        horizontalMargin = hMargin
                    }

                    verticalLayout {
                        gravity = Gravity.CENTER
                        textView("验证码") {
                            textColor = hostAct.getResColor(R.color.text_little_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_SMALL)
                        }.lparams(WRAP_CONTENT, WRAP_CONTENT) { }
                        textView(order.code) {
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.BIGGER)
                            if(order.isUsed){  
                                getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG)
                                textColor = Color.GRAY
                            } else {
                                textColor = hostAct.getResColor(R.color.colorPrimary_purple)
                            }
//                            getPaint().setFlags(0);
                        }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.02f) }
                    }.lparams(MATCH_PARENT, MATCH_PARENT) {
                        below(orderPayView)
                    }
                }.lparams(kIntWidth(0.9f), kIntHeight(0.75f)) {
                    topMargin = kIntHeight(0.03f)
                    centerHorizontally()
                    horizontalMargin = kIntWidth(0.05f)
                }

            }
        }.view

    }

    fun addInfoCell(title: String, weight: Float): _LinearLayout.() -> TextView {
        return {
            var valTextView: TextView? = null
            verticalLayout {
                textView(title) {
                    textColor = hostAct.getResColor(R.color.text_light_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                }.lparams {}
                valTextView = textView("2017-08-20") {
                    textColor = hostAct.getResColor(R.color.text_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                }.lparams {}
            }.lparams(0, WRAP_CONTENT, weight)
            valTextView!!
        }
    }
}