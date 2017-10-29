package com.wellcent.tadpole.presenter

import com.wellcent.tadpole.bo.*
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Observable

/**
 * 网络请求类
 */
interface RestDao {
    //用户登录
    @POST("/webUser/login")
    fun userLogin(@Query("phone") phone: String, @Query("password") password: String, @Query("jPushId") jPushId: String): Observable<ReqMapping<User>>

    @GET("/webUser/getCode")
    fun getCode(@Query("phone") phone: String): Observable<ReqMapping<String>>
    
    @GET("/webUser/register")
    fun register(@Query("phone") phone:String,@Query("code") code:String,@Query("password") password: String): Observable<ReqMapping<String>>
    
    @GET("/webUser/getPassword")
    fun getBackPassword(@Query("phone") phone:String,@Query("code") code:String,@Query("newPassword") newPassword: String,
                        @Query("dupNewPassword")dupNewPassword:String): Observable<ReqMapping<String>>
    
    @GET("/webUser/changePassword")
    fun changePassword(@Query("phone") phone:String,@Query("password") password:String,@Query("newPassword") newPassword: String,
                        @Query("dupNewPassword")dupNewPassword:String): Observable<ReqMapping<String>>
    
    @GET("/webUser/setInfo")
    fun modifyUserInfo(@Query("phone") phone:String,@Query("name") name:String,@Query("idNumber") idNumber: String,
                       @Query("expectedDate")expectedDate:String): Observable<ReqMapping<String>>
    
    @GET("/webArticle/getArticles")
    fun articles(@Query("page") page:Int):Observable<ReqMapping<Article>>

    @GET("/webArticle/getArticleById")
    fun articleDetail(@Query("id") id:String):Observable<ReqMapping<Article>>
    
    @GET("/webFeedback/saveFeedback")
    fun feedback(@Query("phone") phone:String,@Query("content") content:String): Observable<ReqMapping<String>>

    @GET("/webReport/getReports")
    fun reports(@Query("phone") phone:String): Observable<ReqMapping<Report>>

    @GET("/webMessage/getMessages")
    fun messages(@Query("phone") phone:String): Observable<ReqMapping<SysMessage>>
    
}