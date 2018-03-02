package com.wellcent.tadpole.presenter

import android.app.Activity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.shrek.klib.ZActivityManager
import com.shrek.klib.extension.actManager
import com.shrek.klib.presenter.AopInvocation
import com.shrek.klib.retrofit.RestExcuter
import com.wellcent.tadpole.bo.ReqMapping
import com.wellcent.tadpole.presenter.exception.AuthFailureException
import com.wellcent.tadpole.presenter.exception.BussinssException
import com.wellcent.tadpole.presenter.impl.AppDaoImpl
import rx.functions.Action1
import java.net.ConnectException

val ROUTINE_DATA_BINDLE = "ROUTINE_DATA"
val ROUTINE_DATA1_BINDLE = "ROUTINE_DATA1"

//val ServerPath = "http://139.129.57.98:8087"
val ServerPath = "http://admin.cell-bay.com"
fun String.serPicPath():String {
    return "${ServerPath}${this}"
}

fun String.serPdfPath():String {
    return "${ServerPath}${this}"
}

fun String.serArticlePath():String {
    return "${ServerPath}/h5/article?type=out&id=${this}"
}

private val impl: AppDaoImpl by lazy {
    AppDaoImpl(RestDao::class)
}

interface VerifyOperable {
    val verifyOpt: VerifyDao
        get() =  AopInvocation<VerifyDao>(impl).bind(VerifyDao::class.java)
}

interface AppOperable {
    val appOpt: AppDao
        get() = AopInvocation<AppDao>(impl).bind(AppDao::class.java)
}

fun <BO> RestExcuter<BO>.containerASAct(): Activity? {
    var returnVal = container as? Activity
    if (returnVal == null) {
        returnVal = actManager.currentActivity()
    }
    return returnVal
}

fun <BO> RestExcuter<BO>.defaultError(dealThing: ((Throwable) -> Unit)? = null): RestExcuter<BO> {
    return error {
        var contentMsg = "请求服务出现异常!"
        if (it.cause is ConnectException) {
            contentMsg = "连接服务器失败,请检查您的网络!"
        }else if(it is AuthFailureException) {
            SweetAlertDialog(containerASAct(), SweetAlertDialog.ERROR_TYPE)
                    .setContentText("用户的验证信息超时,请重新登录!")
                    .setConfirmText(" 确 定 ")
                    .setConfirmClickListener {
//                        actManager.currentActivity().startActivity<LoginActivity>()
//                        actManager.popAllActivityExceptOne(LoginActivity::class.java)
                    }.show()
            return@error
        }else if(it is BussinssException) {
            contentMsg = it.message.toString()
        }
        val host = containerASAct()
        if( host != null && !host.isFinishing ) {
            SweetAlertDialog(host, SweetAlertDialog.ERROR_TYPE)
                    .setContentText(contentMsg)
                    .setConfirmText(" 确 定 ").show()
        }
        if (dealThing != null) { dealThing(it) }
        it.printStackTrace()
    }
}

fun <BO> RestExcuter<ReqMapping<BO>>.success(dealThing: ((ReqMapping<BO>) -> Unit)? = null): RestExcuter<ReqMapping<BO>> {
    return post {
        successDoing<BO>(containerASAct(), actManager, dealThing,errorDoing, it,it.info)
    }.defaultError {  }
}

fun <BO> RestExcuter<ReqMapping<BO>>.listSuccess(dealThing: ((List<BO>) -> Unit)? = null): RestExcuter<ReqMapping<BO>> {
    return post {
        successDoing<BO>(containerASAct(), actManager, { dealThing?.invoke(it.list) } , errorDoing, it, it.info)
    }.defaultError {  }
}

private fun <BO> successDoing(act: Activity?, actManager: ZActivityManager<Activity>,
                              dealThing: ((ReqMapping<BO>) -> Unit)? = null, errorDoing: Action1<Throwable>? = null,
                              data: ReqMapping<BO>?, msg: String) {
    if (data?.isOk?:false) {
        if (dealThing != null && data != null) {
            dealThing(data!!)
        }
        return
    } else {
        errorDoing?.call(BussinssException(msg))
    }
} 

