package com.wellcent.tadpole.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.ui.adapter.HolderBo
import com.shrek.klib.ui.adapter.KAdapter
import com.shrek.klib.ui.kDefaultRestHandler
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.ui.photo.PhotoChoosePop
import com.shrek.klib.ui.showAlertCrouton
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.bo.ChartContent
import com.wellcent.tadpole.presenter.*
import com.wellcent.tadpole.ui.custom.circleImageView
import com.wellcent.tadpole.util.LooperTask
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import java.io.File
import java.util.*

class AdvisoryActivity : TadpoleActivity(), VerifyOperable, AppOperable {
    val isDoctors by lazy { intent.getBooleanExtra(ROUTINE_DATA_BINDLE,false) }
    lateinit var rootView: RelativeLayout
    lateinit var recyclerView: RecyclerView
    lateinit var inputView:EditText
    var photoChoosePop: PhotoChoosePop? = null
    var adapter: KAdapter<ChartContent, ChartHolder>? = null
    val unRequestTask = LooperTask(20000) {
        val restExcuter = if(isDoctors) appOpt.doctorsChartUnReadMessages() else appOpt.sysChartUnReadMessages()
        restExcuter.listSuccess { notifyDataSetChanged(it) }.error {  }.excute(this@AdvisoryActivity)
        true
    }
    
    override fun initialize(savedInstanceState: Bundle?) {
        rootView = relativeLayout {
            backgroundColor = getResColor(R.color.window_background)
            val nav = navigateBar("我的咨询") {
                setTitleColor(Color.WHITE)
                setNavBg(R.drawable.chart_nav_bg)
                isFocusable = true
                isFocusableInTouchMode = true
                addLeftDefaultBtn(R.drawable.icon_back_g) { finish() }
            }.lparams(MATCH_PARENT, DimensAdapter.nav_height) { bottomMargin = kIntHeight(0.015f) }
            val bottomLayout = relativeLayout {
                kRandomId()
                backgroundResource = R.drawable.tabbar_bg
                val addBtn = imageView(R.drawable.icon_chart_add) {
                    kRandomId()
                    onMyClick { sendImg() }
                }.lparams {
                    alignParentRight()
                    centerVertically()
                    horizontalMargin = kIntWidth(0.02f)
                }
                inputView = editText {
                    backgroundResource = R.drawable.chart_input_bg
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                    textColor = getResColor(R.color.text_black)
                    horizontalPadding = kIntWidth(0.02f)
                    verticalPadding = kIntHeight(0.01f)
                    gravity = Gravity.CENTER_VERTICAL
                    inputType = EditorInfo.TYPE_CLASS_TEXT
                    imeOptions = EditorInfo.IME_ACTION_SEND
                    setOnEditorActionListener { v, actionId, event ->
                        if (actionId == KeyEvent.KEYCODE_ENTER || actionId == KeyEvent.KEYCODE_BACK) {
                            val inputString = inputView.text.toString()
                            if(inputString.isEmpty()){ showAlertCrouton("您还没有输入内容") } else {
                                sendMessage(inputString, null)
                            }
                            true
                        }
                        false
                    }
                }.lparams(MATCH_PARENT, WRAP_CONTENT) {
                    centerVertically()
                    leftMargin = kIntWidth(0.01f)
                    verticalMargin = kIntHeight(0.01f)
                    leftOf(addBtn)
                }
            }.lparams(MATCH_PARENT, WRAP_CONTENT) {
                alignParentBottom()
                topMargin = kIntHeight(0.01f)
            }
            recyclerView = recyclerView {
                backgroundColor = resColor(R.color.window_background)
                layoutManager = LinearLayoutManager(context)
            }.lparams(MATCH_PARENT, MATCH_PARENT) {
                below(nav)
                above(bottomLayout)
            }
        }
        val restExcuter = if(isDoctors) appOpt.doctorsChartMessages() else appOpt.sysChartMessages()
        restExcuter.handler(kDefaultRestHandler(" 正在请求历时聊天记录,请稍等... ")).listSuccess {
            adapter = KAdapter<ChartContent, ChartHolder>(it) {
                itemConstructor { ChartHolder() }
                itemClickDoing { bo, i ->
                    bo.image_path?.apply { if(bo.type == 2){ startActivity<ImageZoomActivity>(ROUTINE_DATA_BINDLE to this)} }
                    bo.detect_item_id?.apply { if(bo.type == 3){ startActivity<GoodsAcvtivity>(ROUTINE_DATA_BINDLE to this)} }
                }
                bindData { holder, bo, i -> holder.initData(this@AdvisoryActivity,bo) }
            }
            recyclerView.adapter = adapter
            notifyDataSetChanged()
            if(isDoctors) { getDoctorPeriod() }
            uiThread(20000){ unRequestTask.start() }
        }.excute(this)
    }

    fun getDoctorPeriod(){
        verifyOpt.getDoctorPeriod().handler(kDefaultRestHandler(" 正在请求历时聊天记录,请稍等... ")).success { 
            notifyDataSetChanged(arrayListOf(ChartContent().apply { 
                type = 999
                name = it.period
            }))
        }.excute(this)
    }
    fun sendMessage(msg: String?, imgFile: File?) {
        val contentTemp = instanceChartContent(msg,imgFile)
        notifyDataSetChanged(listOf(contentTemp))
        adapter?.notifyDataSetChanged()
        val restExcuter = if(isDoctors) appOpt.doctorsSendMessage(msg, imgFile) else appOpt.sysSendMessage(msg, imgFile)
        restExcuter.success {
            contentTemp.localStatus = 0
            inputView.setText("")
            notifyDataSetChanged()
        }.error {
            contentTemp.localStatus = 777
            notifyDataSetChanged()
            inputView.setText("")
        }.excute(this)
    }

    fun instanceChartContent(msg: String?, imgFile: File?): ChartContent {
        return ChartContent().apply {
            isImage = msg == null
            send_time = Date().stringFormat("yyyy-MM-dd HH:mm:ss")
            user_id = verifyOpt.user()!!.id
            avatarImage = verifyOpt.user()!!.avatarImage
            name = verifyOpt.user()!!.name?:""
            hasRead = 1
            type = 1
            content = msg
            localImgPath = imgFile
            localStatus = 666
        }
    }
    
    fun notifyDataSetChanged(chartContent:List<ChartContent>? = null){
        chartContent?.apply { (adapter!!.sourceData as? ArrayList)?.addAll(adapter!!.itemCount,chartContent)  }
        adapter!!.notifyDataSetChanged()
        recyclerView.smoothScrollToPosition(adapter!!.itemCount)
    }

    fun sendImg(){
        photoChoosePop = PhotoChoosePop(this@AdvisoryActivity, null, false, true) {
            sendMessage(null,File(it))
        }
        requestPermissionsWithCallBack(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermissionsWithCallBack(arrayOf(Manifest.permission.CAMERA)) {
                photoChoosePop?.show(rootView)
            }
        }
        
    }
    
    override fun onStop() {
        super.onStop()
        unRequestTask.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        unRequestTask.stop()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        photoChoosePop?.onActivityResult(requestCode,resultCode,data)
    }
}

class ChartHolder() : HolderBo(0) {
    lateinit var timeView: TextView
    lateinit var faceView: ImageView
    lateinit var userNameView: TextView
    lateinit var contentView: TextView
    lateinit var contentImgView: ImageView
    lateinit var contentLayout: LinearLayout
    lateinit var progressView: ProgressBar
    lateinit var failView: ImageView
    lateinit var faceLayout: LinearLayout
    lateinit var chatContenLayout: RelativeLayout
    lateinit var noticeView:TextView
    lateinit var parentView:RelativeLayout
    lateinit var priceView:TextView
    var lastDate: Date? = null
    override fun rootViewInit(): AnkoContext<Context>.() -> Unit {
        return {
            verticalLayout {
                layoutParams = viewGroupLP(MATCH_PARENT, WRAP_CONTENT)
                gravity = Gravity.CENTER_HORIZONTAL

                noticeView = textView {
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                    textColor = Color.WHITE
                    backgroundResource = R.drawable.doctor_period_bg
                    gravity = Gravity.CENTER
                }.lparams(kIntWidth(0.5f), WRAP_CONTENT){ verticalMargin = kIntHeight(0.01f) }
                
                timeView = textView("2017-02-03 12:22:33") {
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                    textColor = context.getResColor(R.color.text_light_black)
                }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.01f) }

                parentView = relativeLayout {
                    faceLayout = verticalLayout {
                        kRandomId()
                        gravity = Gravity.CENTER
                        faceView = circleImageView {
                            kRandomId()
                            imageResource = R.drawable.icon_headshot
                            borderColor = Color.WHITE
                            borderWidth = kIntWidth(0.01f)
                        }.lparams(kIntWidth(0.1f), kIntWidth(0.1f)) { }

                        userNameView = textView("张三") {
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_SMALL)
                            textColor = context.getResColor(R.color.text_little_black)
                        }.lparams { }
                    }.lparams(WRAP_CONTENT, WRAP_CONTENT)

                    chatContenLayout = relativeLayout {
                        kRandomId()
                        contentLayout = verticalLayout {
                            kRandomId()
                            backgroundResource = R.drawable.chat_message_bg
                            contentImgView = imageView(R.drawable.icon_chart_msg_warn) { scaleType = ImageView.ScaleType.FIT_XY }.lparams {}
                            contentView = textView("拉的撒大三大四") {
                                textColor = context.getResColor(R.color.text_black)
                                textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_SMALL)
                            }.lparams(WRAP_CONTENT, WRAP_CONTENT) { }
                            priceView = textView("拉的撒大三大四") {
                                visibility = View.GONE
                                textColor = context.getResColor(R.color.colorPrimary_blue)
                                textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                            }.lparams(WRAP_CONTENT, WRAP_CONTENT) { }
                        }.lparams { topMargin = kIntHeight(0.01f) }

                        failView = imageView(R.drawable.icon_chart_msg_warn) { }.lparams {
                            centerVertically()
                            rightOf(contentLayout)
                            horizontalMargin = kIntWidth(0.01f)
                        }
                        progressView = progressBar { }.lparams(kIntWidth(0.05f), kIntWidth(0.05f)) {
                            centerVertically()
                            rightOf(contentLayout)
                            horizontalMargin = kIntWidth(0.01f)
                        }
                    }.lparams(kIntWidth(0.5f), WRAP_CONTENT) {
                        rightOf(faceLayout)
                        horizontalMargin = kIntWidth(0.01f)
                    }
                }.lparams(MATCH_PARENT, WRAP_CONTENT) { horizontalMargin = kIntWidth(0.03f) }
            }
        }
    }

    fun initData(host:TadpoleActivity,content: ChartContent) {
        if(content.type == 999){
            parentView.visibility = View.GONE
            timeView.visibility = View.GONE
            noticeView.visibility = View.VISIBLE
            noticeView.text = content.name
            return
        } else {
            parentView.visibility = View.VISIBLE
            timeView.visibility = View.VISIBLE
            noticeView.visibility = View.GONE
        }
            
        //时间格式化
        val sendDate = content.send_time.toDate("yyyy-MM-dd HH:mm:ss") ?: Date()
        var formatTime = content.send_time
        if (Date() - dayDiff(sendDate) == 0L) {
            formatTime = "今天 ${sendDate.stringFormat("HH:mm:ss")}"
        }
        lastDate?.apply {
            if (sendDate - secondDiff(this) < 30) {
                formatTime = ""
            }
        }
        if (formatTime.isEmpty()) {
            timeView.visibility = View.GONE
        } else {
            timeView.visibility = View.VISIBLE
            timeView.text = formatTime
        }
        //头像名称
        faceView._urlImg(content.avatarImage.serPicPath())
        userNameView.text = content.name
        //内容
        if(content.type == 3){
            contentImgView.visibility = View.VISIBLE
            contentView.visibility = View.VISIBLE
            priceView.visibility = View.VISIBLE
            content.image_path?.apply { contentImgView._urlImg(serPicPath()) }
            contentView.text = content.content
            priceView.text = "￥"+content.price
        } else {
            priceView.visibility = View.GONE
            if (content.isImage) {
                contentImgView.visibility = View.VISIBLE
                contentView.visibility = View.GONE
                content.localImgPath?.apply {
                    val photoUri = Uri.fromFile(this)
                    val bitmap = PhotoChoosePop.decodeUriAsBitmap(host, photoUri)
                    contentImgView.setImageBitmap(bitmap)
                }
                content.image_path?.apply {
                    contentImgView._urlImg(serPicPath())
                    contentImgView.onMyClick { host.startActivity<ImageZoomActivity>(ROUTINE_DATA_BINDLE to this) }
                }
            } else {
                contentImgView.visibility = View.GONE
                contentView.visibility = View.VISIBLE
                contentView.text = content.content
            }
        }
        
        //发送状态设置
        if(content.localStatus == 666){
            progressView.visibility = View.VISIBLE
            failView.visibility = View.GONE
        } else if(content.localStatus == 777){
            progressView.visibility = View.GONE
            failView.visibility = View.VISIBLE
        } else {
            progressView.visibility = View.GONE
            failView.visibility = View.GONE
        }
       
        //调整布局
        if (content.type == 2 || content.type == 3) {
            contentLayout.setBackgroundResource(R.drawable.chat_message_bg)
            faceLayout.layoutParams = (faceLayout.layoutParams as? RelativeLayout.LayoutParams)?.apply {
                addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            }
            chatContenLayout.layoutParams = (chatContenLayout.layoutParams as? RelativeLayout.LayoutParams)?.apply {
                addRule(RelativeLayout.LEFT_OF, 0)
                addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                addRule(RelativeLayout.RIGHT_OF, faceLayout.id)
            }
            contentLayout.layoutParams = (contentLayout.layoutParams as? RelativeLayout.LayoutParams)?.apply {
                addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            }
            failView.layoutParams = (failView.layoutParams as? RelativeLayout.LayoutParams)?.apply {
                addRule(RelativeLayout.LEFT_OF, 0)
                addRule(RelativeLayout.RIGHT_OF, contentLayout.id)
            }
            progressView.layoutParams = (progressView.layoutParams as? RelativeLayout.LayoutParams)?.apply {
                addRule(RelativeLayout.LEFT_OF, 0)
                addRule(RelativeLayout.RIGHT_OF, contentLayout.id)
            }
        } else {
            contentLayout.setBackgroundResource(R.drawable.chat_message_bg2)
            faceLayout.layoutParams = (faceLayout.layoutParams as? RelativeLayout.LayoutParams)?.apply {
                addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
                addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            }
            chatContenLayout.layoutParams = (chatContenLayout.layoutParams as? RelativeLayout.LayoutParams)?.apply {
                addRule(RelativeLayout.RIGHT_OF, 0)
                addRule(RelativeLayout.LEFT_OF, faceLayout.id)
            }
            contentLayout.layoutParams = (contentLayout.layoutParams as? RelativeLayout.LayoutParams)?.apply {
                addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
                addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            }
            failView.layoutParams = (failView.layoutParams as? RelativeLayout.LayoutParams)?.apply {
                addRule(RelativeLayout.RIGHT_OF, 0)
                addRule(RelativeLayout.LEFT_OF, contentLayout.id)
            }
            progressView.layoutParams = (progressView.layoutParams as? RelativeLayout.LayoutParams)?.apply {
                addRule(RelativeLayout.RIGHT_OF, 0)
                addRule(RelativeLayout.LEFT_OF, contentLayout.id)
            }
        }
    }
}