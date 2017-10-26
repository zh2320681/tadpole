package com.wellcent.tadpole.presenter

import com.wellcent.tadpole.bo.User

/**
 * @author Shrek
 * @date:  2017-10-25
 */
interface VerifyDao {

    var isFirstUse:Boolean

    //查询的用户
    fun user(): User?
}