package com.wellcent.tadpole.bo

import java.io.File
import java.io.Serializable

class ReqMapping<T> {
    var isOk = true
    var isFail = false
    var list = arrayListOf<T>()
    var info = ""
    var imagePath:String? = null
    var avatarImage = ""
    var period = ""
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

    fun converInsurance():Insurance{
        return Insurance().apply {
            this.reportId = this@Report.id
            this.detect_item_id = this@Report.detectItemId
            this.phone = this@Report.phone
            this.detectItemName = this@Report.detect_item
            this.claimId = this@Report.claimId
        }
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
    var id_image_back:String? = null         //身份证反面
    var bank_name= ""        //开户行
    var detectItemName= ""        //检测项目名称
    var introduction:String? = null
    var id= ""        //保险理赔id
    var invoice:String? = null       //发票原件
    var status= 0        //1-审核中，2-审核通过，3-审核失败
    var report_status = 0 //状态                   1-审核中，2-审核通过，3-审核失败
    var claimId:String? = null      //保险id
    var claimImageList = arrayListOf<ClaimImage>()
    
}

class ClaimImage: Serializable {
    var detect_item_id = ""
    var update_time = ""
    var create_time = ""
    var user_id = ""
    var report_id = ""
    var image_path = ""
    var id = ""
    var type:String? = null
    //本地的图片
    var localImgPath:String?= null
}

class ChartContent: Serializable {
    var isImage = false  // 0：否    1：是
    var detect_item_id:String? = null
    var send_time = ""
    var user_id = ""
    var image_path:String? = null
    var price:String? = null
    var avatarImage = ""
    var name = ""
    var hasRead = 0  //0：否    1：是
    var id = ""
    var type = 0   //1：用户消息    2：后台回复消息  3 订单  999 通知
    var content:String? = null
    var localImgPath: File? = null
    var localStatus = 0 //本地状态  666 为发送中  777为发送失败
}


class Order : Serializable {
    var create_time = ""    //创建时间
//    var update_time = ""    //更新时间
    var pay_time = ""
    var name = ""    //检测项目名称
//    var remark = ""    //检测项目说明
    var price = ""    //价格
    var id = ""    //项目id
//    var normal_value = ""    //正常值
//    var test_value = ""    //测试值
//    var result = ""    //检测结果
    var status:Int = 0 // 状态                   0：停用     1：正常
    var imagePath = ""    //图片路径
    var detectUnitName = ""
    var detectItemName = ""
    var code = ""
    var isUsed:Boolean = false
}

class Province : Serializable {
    var province_id = ""
    var name = ""
    var id = 0
}

class City : Serializable {
    var province_id = ""
    var name = ""
    var id = 0
    var city_id = ""
}

class DetectUnit : Serializable {
    var code = ""
    var province = ""
    var create_time = ""
    var city = ""
    var name = ""
    var id = ""
    var sn = ""
    var status = 0
}

class Goods : Serializable {
    var update_time = ""
    var code = ""
    var create_time = ""
    var price = ""
    var image_path = ""
    var name = ""
    var remark = ""
    var id = ""
    var status = 0
}