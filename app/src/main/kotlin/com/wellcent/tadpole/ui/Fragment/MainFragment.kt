package com.wellcent.tadpole.ui.Fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.shrek.klib.colligate.IDCardUtil
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.ui.adapter.HolderBo
import com.shrek.klib.ui.adapter.KAdapter
import com.shrek.klib.ui.kDefaultRestHandler
import com.shrek.klib.view.KFragment
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.bo.Report
import com.wellcent.tadpole.presenter.AppOperable
import com.wellcent.tadpole.presenter.ROUTINE_DATA_BINDLE
import com.wellcent.tadpole.presenter.VerifyOperable
import com.wellcent.tadpole.presenter.listSuccess
import com.wellcent.tadpole.ui.*
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.startActivityForResult

class MainFragment : KFragment(), VerifyOperable, AppOperable {
    var clearBtn: ImageView? = null
    lateinit var idInputView: EditText
    lateinit var accoutOptLayout: LinearLayout
    lateinit var accountInfo: TextView
    lateinit var recyclerView: RecyclerView
    lateinit var emptyLayout: LinearLayout

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI {
            verticalLayout {
                layoutParams = viewGroupLP(MATCH_PARENT, MATCH_PARENT)
                backgroundColor = Color.WHITE
                relativeLayout {
                    backgroundResource = R.drawable.mf_top
                    val stardView = textView("  ") { kRandomId() }.lparams { centerInParent() }
                    linearLayout {
                        gravity = Gravity.CENTER
                        backgroundResource = R.drawable.mf_search_bg
                        imageView(R.drawable.icon_search) { }.lparams()
                        idInputView = editText("") {
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_SMALL)
                            hint = "请输入身份证号"
                            hintTextColor = hostAct.getResColor(R.color.text_light_black)
                            backgroundColor = Color.TRANSPARENT
                            gravity = Gravity.CENTER_VERTICAL
                            singleLine = true
                            inputType = EditorInfo.TYPE_CLASS_TEXT
                            imeOptions = EditorInfo.IME_ACTION_SEARCH
                            addTextChangedListener(object : TextWatcher {
                                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                                override fun afterTextChanged(s: Editable?) {}
                                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                    if (s != null && s!!.length > 0) {
                                        clearBtn?.visibility = View.VISIBLE
                                    } else {
                                        clearBtn?.visibility = View.INVISIBLE
                                    }
                                }
                            })
                            
//                            setOnFocusChangeListener { v, hasFocus -> 
//                                if(hasFocus){
//                                    accountInfo.visibility = View.GONE
//                                    accoutOptLayout.visibility = View.GONE
//                                } else {
//                                    accountInfo.visibility = View.VISIBLE
//                                    accoutOptLayout.visibility = View.VISIBLE
//                                }
//                            }
                            setOnEditorActionListener { v, actionId, event ->
                                if (event!= null && ( event.keyCode == KeyEvent.KEYCODE_ENTER 
                                        || event.keyCode == KeyEvent.KEYCODE_SEARCH) ) {
                                    searchReport()
                                    true
                                }
                                false
                            }
                        }.lparams(kIntWidth(0.5f), WRAP_CONTENT)
                        clearBtn = imageView(R.drawable.btn_close) {
                            visibility = View.INVISIBLE
                            onMyClick { idInputView.setText("") }
                        }.lparams { leftMargin = kIntWidth(0.03f) }
                    }.lparams(MATCH_PARENT, kIntHeight(0.06f)) {
                        below(stardView)
                        horizontalMargin = kIntWidth(0.06f)
                    }

                    imageView(R.drawable.nav_logo) { }.lparams {
                        above(stardView)
                        bottomMargin = kIntHeight(0.01f)
                        centerHorizontally()
                    }
                }.lparams(MATCH_PARENT, 0, 1f)

                relativeLayout {
                    accountInfo = textView("注册帐号可以第一时间获知报告结果") {
                        kRandomId()
                        textColor = hostAct.getResColor(R.color.cell_title)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                    }.lparams {
                        bottomMargin = kIntHeight(0.02f)
                        centerInParent()
                    }
                    //登录注册按钮
                    accoutOptLayout = linearLayout {
                        textView("登  录") {
                            textColor = Color.WHITE
                            backgroundResource = R.drawable.icon_btn_nor
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                            gravity = Gravity.CENTER
                            onMyClick { startActivityForResult<AccountActivity>(USER_LGOIN) }
                        }.lparams(kIntWidth(0.33f), kIntHeight(0.09f)) { rightMargin = kIntWidth(0.03f) }

                        textView("注  册") {
                            textColor = Color.WHITE
                            backgroundResource = R.drawable.icon_btn_nor
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                            gravity = Gravity.CENTER
                            onMyClick { startActivityForResult<AccountActivity>(USER_REGISTER, ROUTINE_DATA_BINDLE to AccountProcess.REGISTER.code) }
                        }.lparams(kIntWidth(0.33f), kIntHeight(0.09f)) { leftMargin = kIntWidth(0.03f) }

                    }.lparams { 
                        centerHorizontally()
                        below(accountInfo)
                    }
                    
                    recyclerView = recyclerView {
                        layoutManager = LinearLayoutManager(context)
                        visibility = View.GONE
                    }.lparams(MATCH_PARENT, MATCH_PARENT) { topMargin = kIntHeight(0.02f) }

                    emptyLayout = verticalLayout {
                        gravity = Gravity.CENTER
                        visibility = View.GONE
                        imageView(R.drawable.icon_empty) { }.lparams(WRAP_CONTENT, WRAP_CONTENT) { }
                        textView("您没有报告结果") {
                            textColor = hostAct.getResColor(R.color.text_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.BIGGER)
                        }.lparams(WRAP_CONTENT, WRAP_CONTENT) { topMargin = kIntHeight(0.02f) }
                    }.lparams(MATCH_PARENT, MATCH_PARENT) { topMargin = kIntHeight(0.02f) }
                }.lparams(MATCH_PARENT, 0, 1.4f)
            }
        }.view
    }

    override fun onShow() {
        val user = verifyOpt.user()
        if (user == null && (recyclerView.visibility == View.VISIBLE || emptyLayout.visibility == View.VISIBLE )) {
            recyclerView.visibility = View.GONE
            emptyLayout.visibility = View.GONE
            accoutOptLayout.visibility = View.VISIBLE
            accountInfo.visibility = View.VISIBLE

            (recyclerView.adapter as? KAdapter<Report, OrderHolder>)?.also {
                it.sourceData = arrayListOf<Report>()
                it.notifyDataSetChanged()
            }
            
        } else if (user != null && (recyclerView.visibility != View.VISIBLE || recyclerView.adapter.itemCount == 0 )) {
            recyclerView.visibility = View.VISIBLE
            emptyLayout.visibility = View.VISIBLE
            accoutOptLayout.visibility = View.GONE
            accountInfo.visibility = View.GONE
            
            appOpt.reports().handler(hostAct.kDefaultRestHandler(" 正在获取报告列表,请稍等... ")).listSuccess {
                setAdapter(it)
            }.excute(hostAct)
        }
    }
    
    fun setAdapter(reports:List<Report>){
        if (reports.size > 0) {
            emptyLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            recyclerView.adapter = KAdapter<Report, OrderHolder>(reports) {
                itemConstructor { OrderHolder(kIntHeight(0.18f)) }
                itemClickDoing { bo, i -> startActivity<ReportActivity>(ROUTINE_DATA_BINDLE to bo) }
                bindData { holder, bo, i ->
                    if (i % 2 == 0) {
                        holder.adornView.setImageResource(R.drawable.list_card_stroke_r)
                        holder.titleView.textColor = hostAct.getResColor(R.color.colorPrimary)
                        holder.stateView.textColor = hostAct.getResColor(R.color.colorPrimary)
                        holder.stateView.rightDrawable(R.drawable.icon_circle_pink, kIntWidth(0.01f))
                    } else {
                        holder.adornView.setImageResource(R.drawable.list_card_stroke_b)
                        holder.titleView.textColor = hostAct.getResColor(R.color.colorPrimary_blue)
                        holder.stateView.textColor = hostAct.getResColor(R.color.colorPrimary_blue)
                        holder.stateView.rightDrawable(R.drawable.icon_circle_blue, kIntWidth(0.01f))
                    }
                    holder.timeView.text = "采集日期: ${bo.sampling_date}"
                    holder.hosView.text = bo.detect_unit
                    holder.stateView.text = bo.statusChineseName()
                    holder.titleView.text = bo.detect_item
                }
            }
        } else {
            emptyLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }
    
    fun searchReport(){
        if(verifyOpt.user() == null){
            startActivity<AccountActivity>()
            context.toastLongShow("请您先登录!")
            return
        }
        val idNum = idInputView.text.toString()
        if(idNum.isEmpty()){
            context.toastLongShow("请您输入身份证号!")
            return 
        }
        if(!IDCardUtil.isIdcard(idNum)){
            context.toastLongShow("请您输入正确身份证号!")
            return
        }
        appOpt.searchReport(idNum).handler(hostAct.kDefaultRestHandler(" 正在查找报告,请稍等... ")).listSuccess {
            setAdapter(it)
        }.excute(hostAct)
    }
}

class OrderHolder(cellHeight: Int) : HolderBo(cellHeight) {
    lateinit var timeView: TextView
    lateinit var titleView: TextView
    lateinit var hosView: TextView
    lateinit var stateView: TextView
    lateinit var adornView: ImageView
    override fun rootViewInit(): AnkoContext<Context>.() -> Unit {
        return {
            relativeLayout {
                adornView = imageView(R.drawable.list_card_stroke_r) { scaleType = ImageView.ScaleType.FIT_XY }.lparams(WRAP_CONTENT, MATCH_PARENT) {
                    leftMargin = kIntWidth(0.02f)
                }
                verticalLayout {
                    backgroundResource = R.drawable.list_card
                    timeView = textView("采集日期：2017-08-10") {
                        gravity = Gravity.CENTER_VERTICAL
                        textColor = context.getResColor(R.color.text_little_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                    }.lparams(MATCH_PARENT, 0, 1f)
                    titleView = textView("胎儿21|18|13号染色体标准型") {
                        gravity = Gravity.CENTER_VERTICAL
                        textColor = context.getResColor(R.color.text_little_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.BIG)
                        lines = 2
                    }.lparams(MATCH_PARENT, 0, 3f)
                    relativeLayout {
                        hosView = textView("苏州市里医院") {
                            gravity = Gravity.CENTER_VERTICAL
                            textColor = context.getResColor(R.color.text_little_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                        }.lparams { centerVertically() }
                        stateView = textView("检测中") {
                            gravity = Gravity.CENTER_VERTICAL
                            textColor = context.getResColor(R.color.text_little_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                        }.lparams {
                            centerVertically()
                            alignParentRight()
                        }
                    }.lparams(MATCH_PARENT, 0, 1f)
                }.lparams(MATCH_PARENT, MATCH_PARENT) { leftMargin = kIntWidth(0.02f) }
            }
        }
    }
}