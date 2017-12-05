package com.wellcent.tadpole.presenter.impl

import android.support.v7.app.AppCompatActivity
import com.shrek.klib.extension.kApplication
import com.shrek.klib.presenter.BooleanPreDelegate
import com.shrek.klib.presenter.StringPreDelegate
import com.shrek.klib.presenter.ZPresenter
import com.shrek.klib.presenter.ann.Pointcut
import com.shrek.klib.retrofit.RestExcuter
import com.shrek.klib.retrofit.handler.RestHandler
import com.wellcent.tadpole.bo.*
import com.wellcent.tadpole.presenter.AppDao
import com.wellcent.tadpole.presenter.RestDao
import com.wellcent.tadpole.presenter.VerifyDao
import com.wellcent.tadpole.presenter.listSuccess
import com.wellcent.tadpole.ui.TadpoleActivity
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import kotlin.reflect.KClass
class AppDaoImpl(restClazz: KClass<RestDao>) : ZPresenter<RestDao>(restClazz.java), VerifyDao, AppDao {
    val operator = kApplication.getSharedPreferences("tadpole", AppCompatActivity.MODE_PRIVATE)
    override var isFirstUse: Boolean by BooleanPreDelegate(operator,true)
    
    var userName:String by StringPreDelegate(operator,"")
    var password:String by StringPreDelegate(operator,"")
    //当前操作的用户
    private var currOptUser:User? = null
    private var reportsTemp:List<Report>? = null
    //缓存
    private var provincesCache:List<Province> = arrayListOf<Province>()
    private var citiesCache = hashMapOf<String,List<City>>()
    private var unitsCache = hashMapOf<String,List<DetectUnit>>()
    private var goodsCache:List<Goods> = arrayListOf<Goods>()
    
    override fun initialization() {
    }

    override fun user(): User? {  return currOptUser  }
    override fun logOut() { currOptUser = null }
    override fun getStoneUserName():String { return userName }
    
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun userLogin(phone: String, password: String): RestExcuter<ReqMapping<User>> {
        return RestExcuter.create(restDao?.userLogin(phone,password,"")).wrapPost {
            if (it.isOk) {
                this@AppDaoImpl.userName = phone
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
    override fun modifyUserInfo(name: String, idNumber: String, expectedDate: String): RestExcuter<ReqMapping<String>> {
        return RestExcuter.create(restDao?.modifyUserInfo(currOptUser!!.phone,name,idNumber,expectedDate))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun changePassword(oldPassword: String, newPassword: String): RestExcuter<ReqMapping<String>> {
        return RestExcuter.create(restDao?.changePassword(currOptUser!!.phone,oldPassword,newPassword,newPassword))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun modifyUserFace(imgFile: File): RestExcuter<ReqMapping<String>> {
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imgFile)
        val body = MultipartBody.Part.createFormData("image", imgFile.getName(), requestFile)
        val phoneBody = RequestBody.create(MediaType.parse("multipart/form-data"),currOptUser!!.phone)
        return RestExcuter.create(restDao?.modifyUserFace(phoneBody,body)).wrapPost { currOptUser?.avatarImage = it.avatarImage }
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun getDoctorPeriod(): RestExcuter<ReqMapping<String>> {
        return RestExcuter.create(restDao?.getDoctorPeriod())
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
        return RestExcuter.create(restDao?.feedback(currOptUser!!.phone,content))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun reports(): RestExcuter<ReqMapping<Report>> {
        return RestExcuter.create(restDao?.reports(currOptUser!!.phone)).wrapPost {  reportsTemp = it.list }
    }

    override fun reportsCache(): List<Report>? { return reportsTemp }
    
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun reportDetail(report: Report): RestExcuter<ReqMapping<Report>> {
        return RestExcuter.create(restDao?.reportDetail(report.id))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun searchReport(idNumber: String): RestExcuter<ReqMapping<Report>> {
        return RestExcuter.create(restDao?.searchReport(currOptUser!!.phone,idNumber))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun insurances(): RestExcuter<ReqMapping<Insurance>> {
        return RestExcuter.create(restDao?.insurances(currOptUser!!.phone))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun insuranceDetail(id: String): RestExcuter<ReqMapping<Insurance>> {
        return RestExcuter.create(restDao?.insuranceDetail(currOptUser!!.phone,id))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun saveInsurance(detectItemId: String, reportId: String, bankCard: String, bankName: String): RestExcuter<ReqMapping<String>> {
        return RestExcuter.create(restDao?.saveClaim(currOptUser!!.phone,detectItemId,reportId,bankCard,bankName))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun insuranceSaveImgs(detectItemId: String, reportId: String, paths: List<String>, files: List<String>)
            : RestExcuter<ReqMapping<String>>{
        val mediaType = MediaType.parse("multipart/form-data")
        val phoneBody = RequestBody.create(mediaType,currOptUser!!.phone)
        val reportIdBody = RequestBody.create(mediaType,reportId)
        val detectItemIdBody = RequestBody.create(mediaType,detectItemId)
        val pathsBody = arrayListOf<MultipartBody.Part>()
        paths.forEach { pathsBody.add(MultipartBody.Part.createFormData("url",it) ) }
        var filesBody = arrayListOf<MultipartBody.Part>()
        files.forEach {
            val part = MultipartBody.Part.createFormData("image", it, RequestBody.create(mediaType,File(it)))
            filesBody.add(part) 
        }
        return RestExcuter.create(restDao?.claimSaveImgs(phoneBody,detectItemIdBody,reportIdBody,pathsBody,filesBody))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun messages(): RestExcuter<ReqMapping<SysMessage>> {
        return RestExcuter.create(restDao?.messages(currOptUser!!.phone))
    }

    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun sysChartMessages(): RestExcuter<ReqMapping<ChartContent>>{
        return RestExcuter.create(restDao?.sysChartMessages(currOptUser!!.phone))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun sysChartUnReadMessages(): RestExcuter<ReqMapping<ChartContent>>{
        return RestExcuter.create(restDao?.sysChartUnReadMessages(currOptUser!!.phone))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun sysSendMessage(content:String?, imgFile: File?): RestExcuter<ReqMapping<String>>{
        return chartSendMessage(content,imgFile,false)
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun doctorsChartMessages(): RestExcuter<ReqMapping<ChartContent>>{
        return RestExcuter.create(restDao?.doctorsChartMessages(currOptUser!!.phone))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun doctorsChartUnReadMessages(): RestExcuter<ReqMapping<ChartContent>>{
        return RestExcuter.create(restDao?.doctorsChartUnReadMessages(currOptUser!!.phone))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun doctorsSendMessage(content:String?, imgFile: File?): RestExcuter<ReqMapping<String>>{
        return chartSendMessage(content,imgFile,true)
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun orders(): RestExcuter<ReqMapping<Order>> {
        return RestExcuter.create(restDao?.orders(currOptUser!!.phone))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun provinces(host: TadpoleActivity, restHandler: RestHandler<ReqMapping<Province>>, process:(List<Province>)->Unit) {
        if( provincesCache.size > 0 ){ process(provincesCache) } else {
            RestExcuter.create(restDao?.provinces()).handler(restHandler).listSuccess {
                provincesCache = it
                process(it)
            }.excute(host)
        }
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun cities(host:TadpoleActivity,provinceId:String,restHandler: RestHandler<ReqMapping<City>>, process:(List<City>)->Unit) {
        val cityTemp = citiesCache[provinceId]
        if( cityTemp?.size?:0 > 0 ){ process(cityTemp!!) } else {
            RestExcuter.create(restDao?.cities(provinceId)).handler(restHandler).listSuccess {
                citiesCache.put(provinceId,it)
                process(it)
            }.excute(host)
        }
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun units(host:TadpoleActivity,cityName:String,restHandler: RestHandler<ReqMapping<DetectUnit>>, process:(List<DetectUnit>)->Unit){
        val unitTemp = unitsCache[cityName]
        if( unitTemp?.size?:0 > 0 ){ process(unitTemp!!) } else {
            RestExcuter.create(restDao?.units(cityName)).handler(restHandler).listSuccess {
                unitsCache.put(cityName,it)
                process(it)
            }.excute(host)
        }
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun goods(host: TadpoleActivity, goodsId: String,restHandler: RestHandler<ReqMapping<Goods>>, process: (Goods?) -> Unit) {
        if( goodsCache.size > 0 ){ process(findGoods(goodsId)) } else {
            RestExcuter.create(restDao?.goods()).handler(restHandler).listSuccess {
                goodsCache = it
                process(findGoods(goodsId))
            }.excute(host)
        }
    }

    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun aliPayOrder(detectItemId:String,detectUnitId:String): RestExcuter<ReqMapping<String>> {
        return RestExcuter.create(restDao?.aliPayOrder(detectItemId,currOptUser!!.phone,detectUnitId))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun wxPayOrder(detectItemId: String, detectUnitId: String): RestExcuter<ReqMapping<WXPayParas>> {
        return RestExcuter.create(restDao?.wxPayOrder(detectItemId,currOptUser!!.phone,detectUnitId))
    }
    @Pointcut(before = arrayOf("beforeLog"), after = arrayOf("afterLog"))
    override fun getByTradeNo(tradeNo:String): RestExcuter<ReqMapping<Order>>{
        return RestExcuter.create(restDao?.getByTradeNo(tradeNo))
    }
    private fun findGoods(goodsId: String) : Goods?{
        var returnGoods:Goods? = null
        goodsCache.forEach { if(it.id.equals(goodsId)){ returnGoods = it } }
        return  returnGoods
    }
    
    private fun chartSendMessage(content:String?, imgFile: File?,isDoctors: Boolean): RestExcuter<ReqMapping<String>>{
        val mediaType = MediaType.parse("multipart/form-data")
        val phoneBody = RequestBody.create(mediaType,currOptUser!!.phone)
        var contentBody:RequestBody? = null
        content?.apply { contentBody = RequestBody.create(mediaType,this) }
        var imgBody:MultipartBody.Part? = null
        imgFile?.apply {
            val requestFile = RequestBody.create(mediaType, this)
            imgBody = MultipartBody.Part.createFormData("image", imgFile.getName(), requestFile)
        }
        if( isDoctors ){  return RestExcuter.create(restDao?.doctorsSendMessage(phoneBody,contentBody,imgBody)) }
        return RestExcuter.create(restDao?.sysSendMessage(phoneBody,contentBody,imgBody))
    }
}