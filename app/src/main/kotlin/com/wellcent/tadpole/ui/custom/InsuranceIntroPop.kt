package com.wellcent.tadpole.ui.custom

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import org.jetbrains.anko.*

class InsuranceIntroPop(var hostAct: Activity) : PopupWindow(hostAct) {

    init {
        val rootView = kApplication.UI {
            relativeLayout {
                backgroundColor = Color.parseColor("#50000000")
                scrollView {
                    verticalLayout {
                        backgroundResource = R.drawable.bx_pop_bg
                        linearLayout {
                            gravity = Gravity.RIGHT
                            imageView(R.drawable.icon_pop_close) { onMyClick { dismiss() } }.lparams { }
                        }.lparams(MATCH_PARENT, WRAP_CONTENT)
                        textView("1. 有效身份证(正反面)") {
                            textColor = hostAct.getResColor(R.color.text_color)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.BIG)
                        }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                        linearLayout {
                            imageView {
                                imageResource = R.drawable.card_positive
                                scaleType = ImageView.ScaleType.FIT_XY
                            }.lparams(MATCH_PARENT, MATCH_PARENT, 1f) { rightMargin = kIntWidth(0.01f) }
                            imageView {
                                imageResource = R.drawable.card_negative
                                scaleType = ImageView.ScaleType.FIT_XY
                            }.lparams(MATCH_PARENT, MATCH_PARENT, 1f) { leftMargin = kIntWidth(0.01f) }
                        }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                        textView("2. 收款银行卡，开户行信息") {
                            textColor = hostAct.getResColor(R.color.text_color)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.BIG)
                        }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                        imageView {
                            imageResource = R.drawable.bank_card
                            scaleType = ImageView.ScaleType.FIT_XY
                        }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                        textView("3. 产前诊断报告，在医院接受产前诊断所产生的原始收费发票原件，费用明细原件，费用清单原件") {
                            textColor = hostAct.getResColor(R.color.text_color)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.BIG)
                        }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                        linearLayout {
                            imageView {
                                imageResource = R.drawable.cost_details
                                scaleType = ImageView.ScaleType.FIT_XY
                            }.lparams(MATCH_PARENT, MATCH_PARENT, 1f) { rightMargin = kIntWidth(0.01f) }

                            imageView {
                                imageResource = R.drawable.cost_list
                                scaleType = ImageView.ScaleType.FIT_XY
                            }.lparams(MATCH_PARENT, MATCH_PARENT, 1f) { rightMargin = kIntWidth(0.01f) }

                            imageView {
                                imageResource = R.drawable.diagnosis
                                scaleType = ImageView.ScaleType.FIT_XY
                            }.lparams(MATCH_PARENT, MATCH_PARENT, 1f) { rightMargin = kIntWidth(0.01f) }
                        }.lparams(MATCH_PARENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                        imageView {
                            imageResource = R.drawable.invoice
                        }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                    }.lparams(MATCH_PARENT, MATCH_PARENT)
                }.lparams(kIntWidth(0.9f), kIntHeight(0.8f)) { centerInParent() }
            }

        }.view

        contentView = rootView
        width = kIntWidth(1f)
        height = kIntHeight(1f)
        isFocusable = true
        isOutsideTouchable = true
        setBackgroundDrawable(BitmapDrawable())
        inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        setOnDismissListener { backgroundAlpha(1f) }
    }

    fun backgroundAlpha(bgAlpha: Float) {
        val lp = hostAct.window.attributes
        lp.alpha = bgAlpha // 0.0-1.0
        hostAct.window.attributes = lp
    }

    fun show(parentView: View) {
        showAtLocation(parentView, Gravity.CENTER, 0, 0)
    }
}