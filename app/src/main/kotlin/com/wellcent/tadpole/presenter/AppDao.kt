package com.wellcent.tadpole.presenter

import com.shrek.klib.retrofit.RestExcuter
import com.shrek.klib.retrofit.handler.RestHandler
import com.wellcent.tadpole.bo.*
import com.wellcent.tadpole.ui.TadpoleActivity
import java.io.File

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
    fun searchReport(idNumber:String): RestExcuter<ReqMapping<Report>>
    fun messages():RestExcuter<ReqMapping<SysMessage>>

    fun insurances(): RestExcuter<ReqMapping<Insurance>>
    fun insuranceDetail(id:String): RestExcuter<ReqMapping<Insurance>>
    fun saveInsurance(detectItemId:String, reportId:String, bankCard:String,bankName:String): RestExcuter<ReqMapping<String>>
    fun insuranceSaveImgs(detectItemId:String, reportId: String,paths: List<String>,files:List<String>): RestExcuter<ReqMapping<String>>

    fun sysChartMessages(): RestExcuter<ReqMapping<ChartContent>>
    fun sysChartUnReadMessages(): RestExcuter<ReqMapping<ChartContent>>
    fun sysSendMessage(content:String?, imgFile: File?): RestExcuter<ReqMapping<String>>

    fun doctorsChartMessages(): RestExcuter<ReqMapping<ChartContent>>
    fun doctorsChartUnReadMessages(): RestExcuter<ReqMapping<ChartContent>>
    fun doctorsSendMessage(content:String?, imgFile: File?): RestExcuter<ReqMapping<String>>
    
    fun orders(): RestExcuter<ReqMapping<Order>>
    fun provinces(host:TadpoleActivity,restHandler: RestHandler<ReqMapping<Province>>, process:(List<Province>)->Unit)
    fun cities(host:TadpoleActivity,provinceId:String,restHandler: RestHandler<ReqMapping<City>>, process:(List<City>)->Unit)
    fun units(host:TadpoleActivity,cityName:String,restHandler: RestHandler<ReqMapping<DetectUnit>>, process:(List<DetectUnit>)->Unit)
    
    fun goods(host:TadpoleActivity,goodsId:String,restHandler: RestHandler<ReqMapping<Goods>>,process:(Goods?)->Unit)
}