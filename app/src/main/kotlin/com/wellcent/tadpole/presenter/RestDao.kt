package com.wellcent.tadpole.presenter

import com.wellcent.tadpole.bo.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
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
    @Multipart
    @POST("/webUser/changeAvatar")
    fun modifyUserFace(@Part("phone") phone:RequestBody, @Part file:MultipartBody.Part): Observable<ReqMapping<String>>
    
    @GET("/webArticle/getArticles")
    fun articles(@Query("page") page:Int):Observable<ReqMapping<Article>>

    @GET("/webArticle/getArticleById")
    fun articleDetail(@Query("id") id:String):Observable<ReqMapping<Article>>
    
    @GET("/webFeedback/saveFeedback")
    fun feedback(@Query("phone") phone:String,@Query("content") content:String): Observable<ReqMapping<String>>

    @GET("/webReport/getReports")
    @Headers("Cache-Control: max-age=60*60*1000")
    fun reports(@Query("phone") phone:String): Observable<ReqMapping<Report>>

    @GET("/webReport/getDetail")
    fun reportDetail(@Query("id") id:String):Observable<ReqMapping<Report>>
    
    @GET("/webReport/searchReport")
    fun searchReport(@Query("phone") phone:String,@Query("idNumber") idNumber:String): Observable<ReqMapping<Report>>
    
    @GET("/webMessage/getMessages")
    fun messages(@Query("phone") phone:String): Observable<ReqMapping<SysMessage>>

    @GET("/webClaim/getClaims")
    @Headers("Cache-Control: max-age=60*60*1000")
    fun insurances(@Query("phone") phone:String): Observable<ReqMapping<Insurance>>

    @Multipart
    @POST("/webClaim/uploadImage")
    fun claimSaveImgs(@Part("phone") phone:RequestBody, @Part("detectItemId") detectItemId:RequestBody
                      , @Part("reportId") reportId:RequestBody, @Part paths:List<MultipartBody.Part>,
                      @Part files:List<MultipartBody.Part>): Observable<ReqMapping<String>>
    
    @GET("/webClaim/saveClaim")
    fun saveClaim(@Query("phone") phone:String,@Query("detectItemId") detectItemId:String ,@Query("reportId") reportId:String,
                  @Query("bankCard") bankCard:String,@Query("bankName") bankName:String): Observable<ReqMapping<String>>

    @GET("/webCustomerService/getAllMsg")
    fun sysChartMessages(@Query("phone") phone: String): Observable<ReqMapping<ChartContent>>
    
    @GET("/webCustomerService/getUnreadMsg")
    fun sysChartUnReadMessages(@Query("phone") phone: String): Observable<ReqMapping<ChartContent>>

    @Multipart
    @POST("/webCustomerService/sendOneMsg")
    fun sysSendMessage(@Part("phone") phone:RequestBody, @Part("content") content:RequestBody?, @Part file:MultipartBody.Part?): Observable<ReqMapping<String>>
   
    @GET("/webCustomerService/getAllMsg")
    fun doctorsChartMessages(@Query("phone") phone: String): Observable<ReqMapping<ChartContent>>

    @GET("/webCustomerService/getUnreadMsg")
    fun doctorsChartUnReadMessages(@Query("phone") phone: String): Observable<ReqMapping<ChartContent>>

    @Multipart
    @POST("/webCustomerService/sendOneMsg")
    fun doctorsSendMessage(@Part("phone") phone:RequestBody, @Part("content") content:RequestBody?, @Part file:MultipartBody.Part?): Observable<ReqMapping<String>>

    @GET("/webOrder/getOrders")
    fun orders(@Query("phone") phone: String,@Query("page") page:Int = 1): Observable<ReqMapping<Order>>
}