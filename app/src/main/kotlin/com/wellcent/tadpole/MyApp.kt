package com.wellcent.tadpole

import com.shrek.klib.KApp
import com.shrek.klib.ZSetting
import com.shrek.klib.extension.getResColor
import com.shrek.klib.ui.CommonUiSetup
import com.wellcent.tadpole.presenter.ServerPath

class MyApp : KApp() {
    override protected fun initSetting(builder: ZSetting.Builder) {
        builder.setRestBasrUrl("${ServerPath}/")
            .setIsDebugMode(false)
    }

    override fun onAfterCreate() {
        super.onAfterCreate()
        CommonUiSetup.pramaryColor = getResColor(R.color.colorPrimary)
    }
}

