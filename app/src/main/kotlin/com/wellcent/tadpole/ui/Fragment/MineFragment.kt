package com.wellcent.tadpole.ui.Fragment

import android.graphics.Color
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.view.KFragment
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.ui.AccountActivity
import com.wellcent.tadpole.ui.SettingActivity
import com.wellcent.tadpole.ui.custom.cardView
import com.wellcent.tadpole.ui.custom.circleImageView
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

class MineFragment : KFragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI {
            verticalLayout {
                relativeLayout {
                    circleImageView {
                        imageResource = R.drawable.icon_headshot
                        borderColor = Color.WHITE
                        borderWidth = kIntWidth(0.02f)
                    }.lparams { centerVertically() }
                    textView(" 登 录 ") {
                        kRandomId()
                        gravity = Gravity.CENTER
                        backgroundResource = R.drawable.primary_small_btn
                        textColor = Color.WHITE
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                        onMyClick { startActivity<AccountActivity>() }
                    }.lparams {
                        centerVertically()
                        alignParentRight()
                    }
                }.lparams(MATCH_PARENT, WRAP_CONTENT) {
                    verticalMargin = kIntHeight(0.02f)
                    horizontalMargin = kIntWidth(0.05f)
                }
                linearLayout {
                    addMidFuntionCell("我的报告", R.drawable.icon_my_report) { }.invoke(this)
                    addMidFuntionCell("我的咨询", R.drawable.icon_my_consult) { }.invoke(this)
                }.lparams(MATCH_PARENT, WRAP_CONTENT) {
                    horizontalMargin = kIntWidth(0.05f)
                    bottomMargin = kIntHeight(0.01f)
                }
                linearLayout {
                    addMidFuntionCell("我的保险", R.drawable.icon_my_claims) { }.invoke(this)
                    addMidFuntionCell("我的订单", R.drawable.icon_my_order) { }.invoke(this)
                }.lparams(MATCH_PARENT, WRAP_CONTENT) {
                    horizontalMargin = kIntWidth(0.05f)
                    bottomMargin = kIntHeight(0.01f)
                }
                addBottomCell("在线客服", "09:00-17:00").invoke(this)
                addBottomCell("意见反馈").invoke(this)
                addBottomCell("设置"){ startActivity<SettingActivity>() }.invoke(this)
            }
        }.view
    }

    fun addMidFuntionCell(title: String, @DrawableRes icon: Int, process: () -> Unit): _LinearLayout.() -> Unit {
        return {
            val isHasChild = this.childCount > 1
            cardView {
                radius = DimensAdapter.dip5
                preventCornerOverlap = true
                useCompatPadding = true
                val paddingVal = kIntWidth(0.02f)
                setContentPadding(paddingVal, paddingVal, paddingVal, paddingVal)
                setCardBackgroundColor(Color.parseColor("#ffffffff"))

                verticalLayout {
                    onMyClick { process() }
                    imageView(icon) {}.lparams { }
                    textView(title) {
                        textColor = hostAct.getResColor(R.color.text_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    }.lparams { leftMargin = kIntWidth(0.01f) }
                }.lparams { }
            }.lparams(MATCH_PARENT, WRAP_CONTENT, 1f) {
                if (isHasChild) { leftMargin = kIntWidth(0.01f) } else { rightMargin = kIntWidth(0.01f) }
            }
        }
    }

    //添加底部的cell
    fun addBottomCell(title: String, rightTitle: String? = null, process: (() -> Unit)? = null): _LinearLayout.() -> Unit {
        return {
            relativeLayout {
                process?.apply { onMyClick { this() } }
                textView(title) {
                    textColor = hostAct.getResColor(R.color.text_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                }.lparams(WRAP_CONTENT, WRAP_CONTENT) { centerVertically() }
                val arrowView = imageView(R.drawable.icon_right_arrows) { kRandomId() }.lparams {
                    alignParentRight()
                    centerVertically()
                }
                rightTitle?.apply {
                    textView(this) {
                        textColor = hostAct.getResColor(R.color.text_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) {
                        centerVertically()
                        leftOf(arrowView)
                        rightMargin = kIntWidth(0.03f)
                    }
                }
            }.lparams(MATCH_PARENT, WRAP_CONTENT) {
                verticalMargin = kIntHeight(0.02f)
                horizontalMargin = kIntWidth(0.08f)
            }
            textView { backgroundColor = hostAct.getResColor(R.color.gap_line) }.lparams(MATCH_PARENT, DimensAdapter.dip1.toInt()) { horizontalMargin = kIntWidth(0.08f) }
        }
    }

}