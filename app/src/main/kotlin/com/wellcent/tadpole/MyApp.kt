package com.wellcent.tadpole

import com.shrek.klib.KApp
import com.shrek.klib.ZSetting

class MyApp : KApp() {
    override protected fun initSetting(builder: ZSetting.Builder) {
        builder.setRestBasrUrl("http://61.155.214.101:82/gclenergyrest/rest/mobileApi/")
            .setIsDebugMode(false)
    }
     
}