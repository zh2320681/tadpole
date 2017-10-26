package com.wellcent.tadpole.ui.Fragment
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.view.KFragment
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

class MainFragment : KFragment() {
    var clearBtn:ImageView? = null
    lateinit var idInputView:EditText
    
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
                    val accoutOptLayout = linearLayout { 
                        kRandomId()
                        textView("登  录"){
                            textColor = Color.WHITE
                            backgroundResource = R.drawable.icon_btn_nor
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                            gravity = Gravity.CENTER
                        }.lparams(kIntWidth(0.33f),kIntHeight(0.09f)) { rightMargin = kIntWidth(0.03f) }

                        textView("注  册"){
                            textColor = Color.WHITE
                            backgroundResource = R.drawable.icon_btn_nor
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                            gravity = Gravity.CENTER
                        }.lparams(kIntWidth(0.33f),kIntHeight(0.09f)) { leftMargin = kIntWidth(0.03f) }
                        
                    }.lparams { centerInParent() }
                    textView("注册帐号可以第一时间获知报告结果"){ textColor = hostAct.getResColor(R.color.cell_title)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                    }.lparams { above(accoutOptLayout) 
                        bottomMargin = kIntHeight(0.02f)
                        centerInParent()
                    }
                }.lparams(MATCH_PARENT, 0,1.5f)
            }
        }.view
    }
    
}