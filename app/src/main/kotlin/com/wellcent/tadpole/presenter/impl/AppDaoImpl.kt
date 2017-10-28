package com.wellcent.tadpole.presenter.impl

import android.support.v7.app.AppCompatActivity
import com.shrek.klib.extension.kApplication
import com.shrek.klib.presenter.BooleanPreDelegate
import com.shrek.klib.presenter.StringPreDelegate
import com.shrek.klib.presenter.ZPresenter
import com.shrek.klib.presenter.ann.Pointcut
import com.shrek.klib.retrofit.RestExcuter
import com.wellcent.tadpole.bo.Article
import com.wellcent.tadpole.bo.ReqMapping
import com.wellcent.tadpole.bo.User
import com.wellcent.tadpole.presenter.AppDao
import com.wellcent.tadpole.presenter.RestDao
import com.wellcent.tadpole.presenter.VerifyDao
import kotlin.reflect.KClass
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

    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun userLogin(phone: String, password: String): RestExcuter<ReqMapping<User>> {
        return RestExcuter.create(restDao?.userLogin(phone,password,"")).wrapPost {
            if (it.isOk) {
                this@AppDaoImpl.userName = userName
                this@AppDaoImpl.password = password
                this@AppDaoImpl.currOptUser = it.user
            }
        }
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun getCode(phone: String): RestExcuter<ReqMapping<String>> {
        return RestExcuter.create(restDao?.getCode(phone))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun register(phone: String, code: String, password: String): RestExcuter<ReqMapping<String>> {
        return RestExcuter.create(restDao?.register(phone,code,password))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun getBackPassword(phone: String, code: String, password: String): RestExcuter<ReqMapping<String>> {
        return RestExcuter.create(restDao?.getBackPassword(phone,code,password,password))
    }

    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun articles(): RestExcuter<ReqMapping<Article>> {
        return RestExcuter.create(restDao?.articles(1))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun articleDetail(id: String): RestExcuter<ReqMapping<Article>> {
        return RestExcuter.create(restDao?.articleDetail(id))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun feedback(content: String): RestExcuter<ReqMapping<String>> {
         //currOptUser!!.phone
        return RestExcuter.create(restDao?.feedback("18012778237",content))
    }
}