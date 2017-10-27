package com.wellcent.tadpole.ui

import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import com.shrek.klib.colligate.IDCardUtil
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.getResColor
import com.shrek.klib.extension.kIntHeight
import com.shrek.klib.extension.kIntWidth
import com.shrek.klib.extension.onMyClick
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.view.KActivity
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.bo.User
import com.wellcent.tadpole.presenter.ROUTINE_DATA_BINDLE
import org.jetbrains.anko.*

class ModifyActivity : KActivity() {
    var inputVies = arrayListOf<Pair<TextView,EditText>>()
    override fun initialize(savedInstanceState: Bundle?) {
        val currType = if (intent.getIntExtra(ROUTINE_DATA_BINDLE, ModifyType.USER_DATA.code) == ModifyType.USER_DATA.code) { ModifyType.USER_DATA} else {
            ModifyType.PASSWORD }
        verticalLayout {
            backgroundColor = getResColor(R.color.window_background)
            navigateBar("设置") {
                setTitleColor(Color.BLACK)
                setNavBg(R.drawable.tabbar_bg)
                addLeftDefaultBtn(R.drawable.icon_back_p) { finish() }
            }.lparams(MATCH_PARENT, DimensAdapter.nav_height) { bottomMargin = kIntHeight(0.015f) }
            currType.titles.forEachIndexed { index, s -> inputVies.add( addContentCell( currType== ModifyType.USER_DATA && index == 2).invoke(this) ) }
            currType.initInfo(null,inputVies)
        }
    }

    fun addContentCell(isDate:Boolean = false): _LinearLayout.()-> Pair<TextView,EditText> {
        return {
            var titleView: TextView? = null
            var inputView: EditText? = null
            relativeLayout {
                if(isDate){ onMyClick {  } }
                backgroundColor = Color.WHITE
                verticalPadding = kIntHeight(0.02f)
                horizontalPadding = kIntWidth(0.08f)
                titleView = textView() {
                    textColor = getResColor(R.color.text_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                }.lparams(WRAP_CONTENT, WRAP_CONTENT) { centerVertically() }

                inputView = editText() {
                    textColor = getResColor(R.color.text_black)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                    if(isDate){ isEnabled = false  }
                }.lparams(WRAP_CONTENT, WRAP_CONTENT) { centerVertically()
                    alignParentRight()
                }
            }.lparams(MATCH_PARENT, WRAP_CONTENT) { }
            textView { backgroundColor = getResColor(R.color.gap_line) }.lparams(MATCH_PARENT, DimensAdapter.dip1.toInt()) { horizontalMargin = kIntWidth(0.08f) }
            titleView!! to inputView!!
        }
    }
}

enum class ModifyType(val title: String, val code: Int,val titles:Array<String>,val special:(List<Pair<TextView,EditText>>)->String?) { 
    USER_DATA("个人信息", 777,arrayOf("姓名","身份证","预测期"),{
        if(IDCardUtil.isIdcard(it[1].second.text.toString())){ "请输入有效的身份证" }
        null
    }), 
    PASSWORD("修改密码", 666,arrayOf("原密码","新密码","重复新密码"),{
        if(!it[2].second.text.toString().equals(it[1].second.text.toString())){ "两次密码输入不一致,请重新输入!" }
        null
    }); 
    
    fun initInfo(user: User? = null,controls:List<Pair<TextView,EditText>>){
        val values = arrayOf(user?.name,user?.id_number,user?.expected_date)
        titles.forEachIndexed { index, s ->  controls[index].first.text = s
            if(this == USER_DATA){ controls[index].second.setText(values[index])  }
        }
    }
    
    fun judgeAvild(controls:List<Pair<TextView,EditText>>):String? {
        var error:String? = null
        controls.forEachIndexed { index, s ->  if(s.second.text.isEmpty()){ if(error == null){ error = "请您输入${titles[index] }"} } }
        //特殊判断
        return special(controls)
    }
}