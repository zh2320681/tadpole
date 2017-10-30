package com.wellcent.tadpole.ui.Fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.ui.kDefaultRestHandler
import com.shrek.klib.ui.loading.handler.KDefaultRestHandler
import com.shrek.klib.ui.photo.PhotoChoosePop
import com.shrek.klib.view.KFragment
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.bo.SysMessage
import com.wellcent.tadpole.presenter.*
import com.wellcent.tadpole.ui.*
import com.wellcent.tadpole.ui.custom.cardView
import com.wellcent.tadpole.ui.custom.circleImageView
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.startActivity
import java.io.File

class MineFragment : KFragment(),VerifyOperable,AppOperable {
    lateinit var nameView:TextView
    lateinit var timeView:TextView
    lateinit var msgNoticeView:TextView
    lateinit var loginBtn:TextView
    lateinit var faceView:ImageView
    lateinit var userInfoLayout :LinearLayout
    
    lateinit var rootView:View
    
    var msgTemp:List<SysMessage> = arrayListOf<SysMessage>()
    var photoChoosePop:PhotoChoosePop? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI {
            rootView = verticalLayout {
                relativeLayout {
                    faceView = circleImageView {
                        kRandomId()
                        imageResource = R.drawable.icon_headshot
                        borderColor = Color.WHITE
                        borderWidth = kIntWidth(0.01f)
                        onMyClick { verifyOpt.user()?.apply {
                            photoChoosePop = PhotoChoosePop(hostAct,faceView,true){
                                verifyOpt.modifyUserFace(File(it)).handler(KDefaultRestHandler(hostAct,"正在修改头像,请稍等...")).success {
                                    faceView._urlImg(it.avatarImage.serPicPath())
                                }.excute(hostAct)
                            }
                            photoChoosePop?.show(rootView)
                        } }
                    }.lparams(kIntWidth(0.18f),kIntWidth(0.18f)) { centerVertically() }

                    userInfoLayout = verticalLayout {
                        visibility = View.GONE
                        nameView = textView("张三") {
                            textColor = hostAct.getResColor(R.color.text_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.MID_BIG)
                        }.lparams(WRAP_CONTENT, WRAP_CONTENT) {  }
                        timeView = textView("17周+10天") {
                            textColor = hostAct.getResColor(R.color.text_little_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_SMALL)
                        }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }
                    }.lparams{ rightOf( faceView) 
                        leftMargin = kIntWidth(0.03f)
                        centerVertically()
                    }
                    msgNoticeView = textView("您有0条新消息") {
                        onMyClick { startActivity<SystemMsgActivity>(ROUTINE_DATA_BINDLE to msgTemp) }
                        visibility = View.GONE
                        textColor = hostAct.getResColor(R.color.text_little_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_SMALL)
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) { 
                        alignParentRight()
                        topMargin = kIntHeight(0.05f)
                    }
                    loginBtn = textView(" 登 录 ") {
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
                    addMidFuntionCell("我的报告", R.drawable.icon_my_report) { startActivity<ReportActivity>() }.invoke(this)
                    addMidFuntionCell("我的咨询", R.drawable.icon_my_consult) { }.invoke(this)
                }.lparams(MATCH_PARENT, WRAP_CONTENT) {
                    horizontalMargin = kIntWidth(0.05f)
                    bottomMargin = kIntHeight(0.01f)
                }
                linearLayout {
                    addMidFuntionCell("我的保险", R.drawable.icon_my_claims) { startActivity<InsuranceActivity>() }.invoke(this)
                    addMidFuntionCell("我的订单", R.drawable.icon_my_order) { startActivity<OrderActivity>() }.invoke(this)
                }.lparams(MATCH_PARENT, WRAP_CONTENT) {
                    horizontalMargin = kIntWidth(0.05f)
                    bottomMargin = kIntHeight(0.01f)
                }
                addBottomCell("在线客服", "09:00-17:00").invoke(this)
                addBottomCell("意见反馈"){ startActivity<FeedbackActivity>() }.invoke(this)
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
                onMyClick { 
                    if(verifyOpt.user() == null){ startActivity<AccountActivity>() } else { process() }
                }
                verticalLayout {
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
                process?.apply { onMyClick { if(verifyOpt.user() == null){ startActivity<AccountActivity>() } else { this() } } }
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

    override fun onShow() {
        val user = verifyOpt.user()
        if (user == null && userInfoLayout.visibility == View.VISIBLE) {
            userInfoLayout.visibility = View.GONE
            msgNoticeView.visibility = View.GONE
            loginBtn.visibility = View.VISIBLE
            faceView.imageResource = R.drawable.icon_headshot
        } else if(user != null && userInfoLayout.visibility != View.VISIBLE){
            userInfoLayout.visibility = View.VISIBLE
            msgNoticeView.visibility = View.VISIBLE
            loginBtn.visibility = View.GONE

            faceView._urlImg(user!!.avatarImage.serPicPath())
            nameView.text = user!!.name
            timeView.text = "${user!!.pregnant_week.replace("+","周 + ")}天"
            reqMsgCount()
        }
    }
    
    fun reqMsgCount() {
        appOpt.messages().handler(hostAct.kDefaultRestHandler(" 正在获取报告列表,请稍等... ")).listSuccess {
            msgTemp = it
            msgNoticeView.text = "您有${it.size}条新消息"
        }.excute(hostAct)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        photoChoosePop?.onActivityResult(requestCode,resultCode,data)
    }

}