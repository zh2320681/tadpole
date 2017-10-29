package com.wellcent.tadpole.presenter

import com.shrek.klib.retrofit.RestExcuter
import com.wellcent.tadpole.bo.Article
import com.wellcent.tadpole.bo.Report
import com.wellcent.tadpole.bo.ReqMapping
import com.wellcent.tadpole.bo.SysMessage

/**
 * 业务接口
 */
interface AppDao {
    fun articles(): RestExcuter<ReqMapping<Article>>
    fun articleDetail(id:String): RestExcuter<ReqMapping<Article>>
    
    fun feedback(content:String): RestExcuter<ReqMapping<String>>
    
    fun reports(): RestExcuter<ReqMapping<Report>>
    fun reportsCache():List<Report>?
    fun reportDetail(report: Report): RestExcuter<ReqMapping<Report>>
    fun messages():RestExcuter<ReqMapping<SysMessage>>
}