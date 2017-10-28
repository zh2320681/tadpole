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