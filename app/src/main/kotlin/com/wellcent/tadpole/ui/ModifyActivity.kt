package com.wellcent.tadpole.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.ui.selector.datepick.DatePickDialog
import com.shrek.klib.ui.selector.datepick.DatePickMode
import com.shrek.klib.ui.showAlertCrouton
import com.shrek.klib.view.KActivity
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.bo.User
import com.wellcent.tadpole.presenter.ROUTINE_DATA_BINDLE
import org.jetbrains.anko.*
import java.util.*

class ModifyActivity : KActivity() {
    var inputVies = arrayListOf<Pair<TextView,TextView>>()
    lateinit var contentLayout:LinearLayout
    val currType by lazy{ if (intent.getIntExtra(ROUTINE_DATA_BINDLE, ModifyType.USER_DATA.code) == ModifyType.USER_DATA.code) { ModifyType.USER_DATA} else {
        ModifyType.PASSWORD } }
    override fun initialize(savedInstanceState: Bundle?) {
        verticalLayout {
            gravity = Gravity.CENTER_HORIZONTAL
            backgroundColor = getResColor(R.color.window_background)
            navigateBar(currType.title) {
                setTitleColor(Color.BLACK)
                setNavBg(R.drawable.tabbar_bg)
                addLeftDefaultBtn(R.drawable.icon_back_p) { finish() }
            }.lparams(MATCH_PARENT, DimensAdapter.nav_height) { bottomMargin = kIntHeight(0.015f) }
            contentLayout = verticalLayout {
                currType.titles.forEachIndexed { index, s -> inputVies.add( addContentCell( currType== ModifyType.USER_DATA && index == 2).invoke(this) ) }
            }.lparams(MATCH_PARENT, WRAP_CONTENT) 
            currType.initInfo(null,inputVies)
            textView("完 成") {
                textColor = Color.WHITE
                backgroundResource = R.drawable.primary_btn
                textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                gravity = Gravity.CENTER
                onMyClick { comfirm() }
            }.lparams(kIntWidth(0.9f),kIntHeight(0.1f))
        }
    }

    fun addContentCell(isDate:Boolean = false): _LinearLayout.()-> Pair<TextView,TextView> {
        return {
            var titleView: TextView? = null
            var inputView: TextView? = null
            relativeLayout {
                backgroundColor = Color.WHITE
//                verticalPadding = kIntHeight(0.015f)
                horizontalPadding = kIntWidth(0.08f)
                titleView = textView() {
                    kRandomId()
                    textColor = getResColor(R.color.text_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                }.lparams(WRAP_CONTENT, WRAP_CONTENT) { centerVertically() }

                if(isDate){
                    inputView = textView {
                        textColor = getResColor(R.color.text_little_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                        gravity = Gravity.RIGHT
                    }.lparams(MATCH_PARENT, WRAP_CONTENT) { centerVertically()
                        rightOf(titleView!!)
                        leftMargin = kIntWidth(0.02f)
                    }
                    onMyClick {
                        val currDate = Date()
                        val chooseDate = (inputView!!.text.toString().toDate("yyyy-MM-dd"))?:currDate
                        val dialog = DatePickDialog(this@ModifyActivity, chooseDate , currDate[0] - 20, currDate[0], DatePickMode.DAY_CHOOSE)
                        dialog.setTitle("请选择时间")
                        dialog.show()
                        dialog.setOnChooseDateListener {  inputView!!.setText(it.stringFormat("yyyy-MM-dd")) }
                    }
                } else {
                    inputView = editText() {
                        gravity = Gravity.RIGHT
                        textColor = getResColor(R.color.text_little_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                        backgroundColor = Color.TRANSPARENT
                    }.lparams(MATCH_PARENT, WRAP_CONTENT) { centerVertically()
                        rightOf(titleView!!)
                        leftMargin = kIntWidth(0.02f)
                    }
                }
            }.lparams(MATCH_PARENT, kIntHeight(0.07f)) { }
            textView { backgroundColor = getResColor(R.color.gap_line) }.lparams(MATCH_PARENT, DimensAdapter.dip1.toInt()) { horizontalMargin = kIntWidth(0.08f) }
            titleView!! to inputView!!
        }
    }
    
    fun comfirm(){
        val errorInfo = currType.judgeAvild(inputVies)
        if(errorInfo != null){
            showAlertCrouton(errorInfo!!,contentLayout)
        } else {
            
        }
    }
}

enum class ModifyType(val title: String, val code: Int,val titles:Array<String>,val special:(List<Pair<TextView,TextView>>)->String?) { 
    USER_DATA("个人信息", 777,arrayOf("姓名","身份证","预测期"),{
        if(!com.shrek.klib.colligate.IDCardUtil.isIdcard(it[1].second.text.toString())){ "请输入有效的身份证" } else { null }
    }), 
    PASSWORD("修改密码", 666,arrayOf("原密码","新密码","重复新密码"),{
        if(!it[2].second.text.toString().equals(it[1].second.text.toString())){ "两次密码输入不一致,请重新输入!" } else { null }
    }); 
    
    fun initInfo(user: User? = null,controls:List<Pair<TextView,TextView>>){
        val values = arrayOf(user?.name,user?.id_number,user?.expected_date)
        titles.forEachIndexed { index, s ->  controls[index].first.text = s
            if(this == USER_DATA){ controls[index].second.setText(values[index])  }
        }
    }
    
    fun judgeAvild(controls:List<Pair<TextView,TextView>>):String? {
        var error:String? = null
        controls.forEachIndexed { index, s ->  if(s.second.text.isEmpty()){ if(error == null){ error = "请您输入${titles[index] }"} } }
        //特殊判断
        return error?:special(controls)
    }
}