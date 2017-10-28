package com.wellcent.tadpole.presenter

import com.shrek.klib.retrofit.RestExcuter
import com.wellcent.tadpole.bo.ReqMapping
import com.wellcent.tadpole.bo.User

/**
 * @author Shrek
 * @date:  2017-10-25
 */
interface VerifyDao {
    var isFirstUse:Boolean
    //查询的用户
    fun user(): User?
    fun userLogin(phone:String,password:String): RestExcuter<ReqMapping<User>>
    fun getCode( phone:String ): RestExcuter<ReqMapping<String>>
}