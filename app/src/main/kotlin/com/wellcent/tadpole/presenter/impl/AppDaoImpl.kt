package com.wellcent.tadpole.presenter.impl

import android.support.v7.app.AppCompatActivity
import com.shrek.klib.extension.kApplication
import com.shrek.klib.presenter.BooleanPreDelegate
import com.shrek.klib.presenter.StringPreDelegate
import com.shrek.klib.presenter.ZPresenter
import com.wellcent.tadpole.bo.User
import com.wellcent.tadpole.presenter.AppDao
import com.wellcent.tadpole.presenter.RestDao
import com.wellcent.tadpole.presenter.VerifyDao
import kotlin.reflect.KClass

/**
 * @author Shrek
 * @date:  2017-10-25
 */
class AppDaoImpl(restClazz: KClass<RestDao>) : ZPresenter<RestDao>(restClazz.java), VerifyDao, AppDao {
    val operator = kApplication.getSharedPreferences("tadpole", AppCompatActivity.MODE_PRIVATE)
    override var isFirstUse: Boolean by BooleanPreDelegate(operator,true)
    
    var userName:String by StringPreDelegate(operator,"")
    var password:String by StringPreDelegate(operator,"")
    //当前操作的用户
    private var currOptUser:User? = null
    
    override fun initialization() {
        
    }

    override fun user(): User? {  return currOptUser  }
}