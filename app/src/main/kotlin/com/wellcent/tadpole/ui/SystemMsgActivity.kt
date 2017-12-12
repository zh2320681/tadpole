package com.wellcent.tadpole.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.widget.TextView
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.getResColor
import com.shrek.klib.extension.kIntHeight
import com.shrek.klib.extension.kIntWidth
import com.shrek.klib.ui.adapter.HolderBo
import com.shrek.klib.ui.adapter.KAdapter
import com.shrek.klib.ui.kDefaultRestHandler
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.ui.showComfirmCrouton
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.bo.*
import com.wellcent.tadpole.presenter.AppOperable
import com.wellcent.tadpole.presenter.ROUTINE_DATA_BINDLE
import com.wellcent.tadpole.presenter.success
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView

class SystemMsgActivity : TadpoleActivity(),AppOperable {
    lateinit var recyclerView: RecyclerView
    var processMsg:SysMessage? = null
    override fun initialize(savedInstanceState: Bundle?) {
        verticalLayout {
            backgroundColor = getResColor(R.color.window_background)
            navigateBar("系统消息") {
                setTitleColor(Color.BLACK)
                setNavBg(R.drawable.tabbar_bg)
                addLeftDefaultBtn(R.drawable.icon_back_p) { finish() }
            }.lparams(MATCH_PARENT, DimensAdapter.nav_height)
            recyclerView = recyclerView {
                layoutManager = LinearLayoutManager(context)
            }.lparams(MATCH_PARENT, MATCH_PARENT)
        }
    }

    override fun onResume() {
        super.onResume()
        val process = kDefaultRestHandler<ReqMapping<SysMessage>>(" 正在获取消息列表,请稍等... ")
        appOpt.messages(processMsg!= null,this,process){
            recyclerView.adapter = KAdapter<SysMessage, SysMsgHolder>(it) {
                itemConstructor { SysMsgHolder(kIntHeight(0.17f)) }
                itemClickDoing { bo, i ->  itemClick(bo) }
                bindData { holder, bo, i ->
                    holder.timeView.text = bo.send_time
                    holder.titleView.text = bo.title
                    holder.desView.text = bo.content
                }
            }
        }
        
        if(processMsg != null){
            appOpt.setReaded(processMsg!!.id).handler(kDefaultRestHandler(" 正在获取报告列表,请稍等... ")).success{
                showComfirmCrouton("该消息已经值为已读状态!")
                processMsg = null
            }.excute(this)
        }
        
    }
    
    fun itemClick(msg:SysMessage){
        if(msg.type == 1){
            val bo = Article().also { it.id = msg.relate_id }
            startActivity<ArticleActivity>(ROUTINE_DATA_BINDLE to bo)
        } else if(msg.type == 2){
            val bo = Insurance().also { it.id = msg.relate_id }
            startActivity<InsuranceActivity>(ROUTINE_DATA_BINDLE to bo)
        } else if(msg.type == 3){
            startActivity<AdvisoryActivity>(ROUTINE_DATA_BINDLE to true)
        } else if(msg.type == 4){
            val bo = Report().also { it.id = msg.relate_id }
            startActivity<ReportActivity>(ROUTINE_DATA_BINDLE to bo)
        } else if(msg.type == 5){
            startActivity<OrderActivity>()
        }
        processMsg = msg
    }
}

class SysMsgHolder(cellHeight:Int): HolderBo(cellHeight) {
    lateinit var timeView: TextView
    lateinit var desView: TextView
    lateinit var titleView: TextView
    override fun rootViewInit(): AnkoContext<Context>.() -> Unit {
        return {
            verticalLayout { 
                gravity = Gravity.CENTER
                timeView = textView("2017年10月22日 13:22:12") {
                    textColor = context.getResColor(R.color.text_light_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                }.lparams(WRAP_CONTENT, WRAP_CONTENT) { verticalMargin = kIntHeight(0.01f) }
                verticalLayout { 
                    backgroundColor = Color.WHITE
                    horizontalPadding = kIntWidth(0.04f)
                    titleView = textView("标题标题") {
                        textColor = context.getResColor(R.color.text_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) {
                        topMargin = kIntHeight(0.02f)
                    }
                    desView = textView("描述描述描述") {
                        textColor = context.getResColor(R.color.text_little_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                        lines = 2
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT) {
                        verticalMargin = kIntHeight(0.02f)
                    }
                }.lparams(MATCH_PARENT, MATCH_PARENT)
            }
        }
    }
}