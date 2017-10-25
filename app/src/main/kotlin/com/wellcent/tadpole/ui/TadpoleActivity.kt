package com.wellcent.tadpole.ui

import cn.pedant.SweetAlert.SweetAlertDialog
import com.shrek.klib.colligate.PermissionDescript
import com.shrek.klib.colligate.toAppSetting
import com.shrek.klib.view.KActivity
import java.lang.StringBuilder

/**
 * @author Shrek
 * @date:  2017-10-25
 */
abstract class TadpoleActivity : KActivity() {

    override var refuseCallback: (List<PermissionDescript>) -> Unit = {
        var returnString = ""
        if(it.size != 0) {
            var sb = StringBuilder(it[0].abbDes)
            it.forEachIndexed { index, permissionDescript ->
                if (index > 0) { sb.append(",${permissionDescript.abbDes}") }
            }
            returnString = sb.toString()
        }
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("您拒绝 [${returnString}] 的申请，会导致后续操作失败，请重新启动程序并同意权限的申请!")
                .setConfirmText("确定")
                .setConfirmClickListener {
                    it.dismiss()
                }.showCancelButton(false).show()
    }

    override var neverRefuseCallback: (List<PermissionDescript>) -> Unit = {
        var returnString = ""
        if(it.size != 0) {
            var sb = StringBuilder(it[0].abbDes)
            it.forEachIndexed { index, permissionDescript ->
                if (index > 0) {
                    sb.append(",${permissionDescript.abbDes}")
                }
            }
            returnString = sb.toString()
        }
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("您已永久拒绝[${returnString}]的申请，需要您去系统设置里手动设置这些权限设置，才能进行后续操作!!")
                .setConfirmText("去设置")
                .setConfirmClickListener {
                    toAppSetting()
                }.setCancelText("算了吧").show()
    }
}