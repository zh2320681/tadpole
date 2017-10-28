package com.wellcent.tadpole.presenter

import com.wellcent.tadpole.bo.ReqMapping
import com.wellcent.tadpole.bo.User
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

}