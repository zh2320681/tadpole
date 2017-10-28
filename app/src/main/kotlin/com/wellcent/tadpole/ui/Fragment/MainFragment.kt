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
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.ui.adapter.HolderBo
import com.shrek.klib.ui.adapter.KAdapter
import com.shrek.klib.view.KFragment
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.presenter.ROUTINE_DATA_BINDLE
import com.wellcent.tadpole.ui.AccountActivity
import com.wellcent.tadpole.ui.AccountProcess
import com.wellcent.tadpole.ui.ReportActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI

class MainFragment : KFragment() {
    var clearBtn:ImageView? = null
    lateinit var idInputView:EditText
    lateinit var accoutOptLayout:LinearLayout
    lateinit var accountInfo:TextView
    lateinit var recyclerView: RecyclerView
    
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI { 
            verticalLayout { 
                layoutParams = viewGroupLP(MATCH_PARENT, MATCH_PARENT)
                backgroundColor = Color.WHITE
                relativeLayout {
                    backgroundResource = R.drawable.mf_top
                    val stardView = textView("  "){ kRandomId() }.lparams{ centerInParent() }
                    linearLayout {
                        gravity = Gravity.CENTER
                        backgroundResource = R.drawable.mf_search_bg 
                        imageView(R.drawable.icon_search) {  }.lparams()
                        idInputView = editText{
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_SMALL)
                            hint = "请输入身份证号"
                            hintTextColor = hostAct.getResColor(R.color.text_light_black)
                            backgroundColor = Color.TRANSPARENT
                            singleLine = true
                            inputType = EditorInfo.TYPE_CLASS_TEXT
                            imeOptions = EditorInfo.IME_ACTION_SEARCH
                            addTextChangedListener(object : TextWatcher {
                                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                                override fun afterTextChanged(s: Editable?) { }
                                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                    if(s!= null && s!!.length > 0){ clearBtn?.visibility = View.VISIBLE } else {clearBtn?.visibility = View.INVISIBLE}
                                }
                            })
                            setOnEditorActionListener { v, actionId, event ->
                                if (actionId == KeyEvent.KEYCODE_ENTER) {
                                    // do some your things  
                                    true
                                }
                                false
                            }
                        }.lparams(kIntWidth(0.5f), WRAP_CONTENT)
                        clearBtn = imageView(R.drawable.btn_close){
                            visibility = View.INVISIBLE
                            onMyClick { idInputView.setText("") }
                        }.lparams { leftMargin = kIntWidth(0.03f) }
                    }.lparams(MATCH_PARENT, kIntHeight(0.06f)){ 
                        below(stardView)
                        horizontalMargin = kIntWidth(0.06f)
                    }
                    
                    imageView(R.drawable.nav_logo) {  }.lparams { above(stardView) 
                        bottomMargin = kIntHeight(0.01f)
                        centerHorizontally()
                    }
                }.lparams(MATCH_PARENT, 0,1f)

                relativeLayout {
                    //登录注册按钮
                    accoutOptLayout = linearLayout { 
                        kRandomId()
                        textView("登  录"){
                            textColor = Color.WHITE
                            backgroundResource = R.drawable.icon_btn_nor
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                            gravity = Gravity.CENTER
                            onMyClick { startActivity<AccountActivity>() }
                        }.lparams(kIntWidth(0.33f),kIntHeight(0.09f)) { rightMargin = kIntWidth(0.03f) }

                        textView("注  册"){
                            textColor = Color.WHITE
                            backgroundResource = R.drawable.icon_btn_nor
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                            gravity = Gravity.CENTER
                            onMyClick { startActivity<AccountActivity>( ROUTINE_DATA_BINDLE to AccountProcess.REGISTER.code ) }
                        }.lparams(kIntWidth(0.33f),kIntHeight(0.09f)) { leftMargin = kIntWidth(0.03f) }
                        
                    }.lparams { centerInParent() }
                    accountInfo = textView("注册帐号可以第一时间获知报告结果"){ textColor = hostAct.getResColor(R.color.cell_title)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                    }.lparams { above(accoutOptLayout) 
                        bottomMargin = kIntHeight(0.02f)
                        centerInParent()
                    }
                    recyclerView = recyclerView {
                        layoutManager = LinearLayoutManager(context)
                        adapter = KAdapter<String, OrderHolder>(arrayListOf("1111111", "2222222")) {
                            itemConstructor { OrderHolder(kIntHeight(0.18f)) }
                            itemClickDoing { s, i -> startActivity<ReportActivity>() }
                            bindData { holder, s, i ->
                                if(i%2 == 0){
                                    holder.adornView.setImageResource(R.drawable.list_card_stroke_r)
                                    holder.titleView.textColor = hostAct.getResColor(R.color.colorPrimary)
                                    holder.stateView.textColor = hostAct.getResColor(R.color.colorPrimary)
                                    holder.stateView.rightDrawable(R.drawable.icon_circle_pink,kIntWidth(0.01f))
                                } else {
                                    holder.adornView.setImageResource(R.drawable.list_card_stroke_b)
                                    holder.titleView.textColor = hostAct.getResColor(R.color.colorPrimary_blue)
                                    holder.stateView.textColor = hostAct.getResColor(R.color.colorPrimary_blue)
                                    holder.stateView.rightDrawable(R.drawable.icon_circle_blue,kIntWidth(0.01f))
                                }
                            }
                        }
                    }.lparams(MATCH_PARENT, MATCH_PARENT){ topMargin = kIntHeight(0.02f)}
                }.lparams(MATCH_PARENT, 0,1.5f)
            }
        }.view
    }
    
}

class OrderHolder(cellHeight:Int): HolderBo(cellHeight) {
    lateinit var timeView:TextView
    lateinit var titleView:TextView
    lateinit var hosView:TextView
    lateinit var stateView:TextView
    lateinit var adornView:ImageView
    override fun rootViewInit(): AnkoContext<Context>.() -> Unit {
        return {
            relativeLayout {
                adornView = imageView(R.drawable.list_card_stroke_r) { scaleType = ImageView.ScaleType.FIT_XY }.lparams(WRAP_CONTENT, MATCH_PARENT){
                    leftMargin = kIntWidth(0.02f)
                }
                verticalLayout { 
                    backgroundResource = R.drawable.list_card
                    timeView = textView("采集日期：2017-08-10"){
                        gravity = Gravity.CENTER_VERTICAL
                        textColor = context.getResColor(R.color.text_little_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                    }.lparams(MATCH_PARENT,0,1f)
                    titleView = textView("胎儿21|18|13号染色体标准型"){
                        gravity = Gravity.CENTER_VERTICAL
                        textColor = context.getResColor(R.color.text_little_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.BIG)
                        lines = 2
                    }.lparams(MATCH_PARENT,0,3f)
                    relativeLayout {
                        hosView = textView("苏州市里医院"){
                            gravity = Gravity.CENTER_VERTICAL
                            textColor = context.getResColor(R.color.text_little_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                        }.lparams{ centerVertically() }
                        stateView = textView("检测中"){
                            gravity = Gravity.CENTER_VERTICAL
                            textColor = context.getResColor(R.color.text_little_black)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SMALL)
                        }.lparams{ centerVertically() 
                            alignParentRight()}
                    }.lparams(MATCH_PARENT,0,1f)
                }.lparams(MATCH_PARENT, MATCH_PARENT){ leftMargin = kIntWidth(0.02f) }
            }
        }
    }
}