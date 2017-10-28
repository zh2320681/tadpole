package com.wellcent.tadpole.presenter

import com.shrek.klib.retrofit.RestExcuter
import com.wellcent.tadpole.bo.Article
import com.wellcent.tadpole.bo.ReqMapping

/**
 * 业务接口
 */
interface AppDao {
    fun articles(): RestExcuter<ReqMapping<Article>>
    fun articleDetail(id:String): RestExcuter<ReqMapping<Article>>
    
    fun feedback(content:String): RestExcuter<ReqMapping<String>>
}