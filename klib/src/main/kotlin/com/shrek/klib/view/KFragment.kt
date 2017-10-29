package com.shrek.klib.view

import android.app.Activity
import android.support.v4.app.Fragment
import com.shrek.klib.extension.kApplication

open class KFragment : Fragment() {

    val hostAct: KActivity
        get() {
            var act: Activity? = activity
            if (act == null) {
                act = kApplication.actManager.currentActivity()
            }
            return act as KActivity
        }
    
    open fun onShow(){}
    open fun onHide(){}
}