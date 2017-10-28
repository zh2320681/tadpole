package com.wellcent.tadpole.bo

class ReqMapping<T> {
    var isOk = true
    var isFail = false
    var list:List<T> = arrayListOf<T>()
    var info = ""
    var imagePath = ""
    var user:T? = null
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