package com.wellcent.tadpole.bo

import java.io.Serializable

class ReqMapping<T> {
    var isOk = true
    var isFail = false
    var list = arrayListOf<T>()
    var info = ""
    var imagePath = ""
    var user:T? = null
    var detail:T? = null
}

class User {
    var id = ""
    var name = "" 
    var avatarImage = ""
    var password = ""
    var phone = ""
    var age = 0
    var id_number = ""
    var expected_date = ""
    var pregnant_week = ""
    var jpush_id = ""
    var create_time = ""
    var update_time = ""
}

class Article : Serializable {
    var create_time = ""
    var title_image = ""
    var id = ""
    var vice_title = ""
    var title = ""
    
    var author = ""
    var content = ""
    var update_time =""
    var category_id = ""
}

class Report: Serializable {
    var detect_item = ""        //检测项目名称
    var id_number = ""        //身份证号码
    var create_time = ""        //创建时间
    var detect_result = ""        //检测结果
    var sampling_date = ""        //采样日期
    var detect_value1 = ""        //检测值1(T21)
    var detect_value2 = ""        //检测值2(T18)
    var detect_value3 = ""        //检测值3(T13)
    var detect_introduction = ""        //说明
    var normal_value = ""        //正常值
    var receive_date = ""        //接收日期
    var update_time = ""        //更新日期
    var phone = ""        //手机号码
    var detectItemId = ""        //检测项目id
    var pregnant_week = ""        //孕周
    var detect_item_code = ""        //检测项目代号
    var sn = ""        //报告单号
    var age = 0        //年龄
    var username = ""        //姓名
    var detect_unit = ""        //检测单位名称
    var report_status = 0        //报告状态                0-未检测，1-检测中，2-检测完毕
    var id = ""        //报告id
    var remark:String? = null        //报告备注
    var claimId:String? = null      //保险id
    
    fun statusChineseName():String {
        if(report_status == 0){ return "未检测" }
        if(report_status == 1){ return "检测中" }
        return "检测完毕"
    }
}

class SysMessage: Serializable {
    var id = ""        //id
    var user_id:String? = null        //用户id
    var title = ""        //标题
    var content = ""        //内容
    var type = 0        //类型                  1：文章; 2:保险; 3:医生回复; 4:检测报告; 5:订单
    var relate_id = ""        //关联的id               对应的id，如type=1，则为文章的id;type=2，则为保险的id
    var send_time = ""        //发送时间
}

class Insurance: Serializable {
    var detect_item_id= ""        //检测项目id
    var reportId= ""        //报告id
    var bank_card= ""        //银行卡号
    var create_time= ""        //创建时间
    var cost_breakdown:String? = null        //费用明细原件
    var insurance_policy:String? = null        //保险单
    var id_image_front:String? = null        //身份证正面
    var update_time= ""        //更新时间
    var user_id= ""        //用户id
    var phone:String? = null        //用户手机号
    var cost_list:String? = null        //费用明细原件
    var id_image_back= ""        //身份证反面
    var bank_name= ""        //开户行
    var name= ""        //检测项目名称
    var id= ""        //保险理赔id
    var invoice:String? = null       //发票原件
    var status= ""        //状态                   1-审核中，2-审核通过，3-审核失败
}