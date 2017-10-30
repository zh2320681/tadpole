package com.wellcent.tadpole.presenter

import com.shrek.klib.retrofit.RestExcuter
import com.wellcent.tadpole.bo.ReqMapping
import com.wellcent.tadpole.bo.User
import java.io.File

/**
 * @author Shrek
 * @date:  2017-10-25
 */
interface VerifyDao {
    fun getStoneUserName():String
    
    var isFirstUse:Boolean
    //查询的用户
    fun user(): User?
    fun userLogin(phone:String,password:String): RestExcuter<ReqMapping<User>>
    fun getCode( phone:String ): RestExcuter<ReqMapping<String>>
    fun register(phone:String,code:String,password:String): RestExcuter<ReqMapping<String>>
    fun getBackPassword(phone:String,code:String,password:String): RestExcuter<ReqMapping<String>>
    fun modifyUserInfo(name:String,idNumber: String,expectedDate:String): RestExcuter<ReqMapping<String>>
    fun changePassword(oldPassword:String,newPassword:String): RestExcuter<ReqMapping<String>>
    fun modifyUserFace(imgFile: File): RestExcuter<ReqMapping<String>>
    
    fun logOut()
}