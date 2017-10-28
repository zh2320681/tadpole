package com.wellcent.tadpole

import com.shrek.klib.KApp
import com.shrek.klib.ZSetting
import com.shrek.klib.extension.getResColor
import com.shrek.klib.ui.CommonUiSetup

class MyApp : KApp() {
    override protected fun initSetting(builder: ZSetting.Builder) {
        builder.setRestBasrUrl("http://139.129.57.98:8087/")
            .setIsDebugMode(false)
    }

    override fun onAfterCreate() {
        super.onAfterCreate()
        CommonUiSetup.pramaryColor = getResColor(R.color.colorPrimary)
    }
}