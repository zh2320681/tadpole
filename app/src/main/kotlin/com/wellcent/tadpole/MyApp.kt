package com.wellcent.tadpole

import android.content.Context
import android.support.multidex.MultiDex
import com.shrek.klib.KApp
import com.shrek.klib.ZSetting
import com.shrek.klib.extension.getResColor
import com.shrek.klib.ui.CommonUiSetup
import com.umeng.socialize.PlatformConfig
import com.umeng.socialize.UMShareAPI
import com.wellcent.tadpole.presenter.ServerPath

class MyApp : KApp() {
    override protected fun initSetting(builder: ZSetting.Builder) {
        builder.setRestBasrUrl("${ServerPath}/")
            .setIsDebugMode(true)
    }

    override fun onAfterCreate() {
        super.onAfterCreate()
        PlatformConfig.setWeixin("wx967daebe835fbeac", "5bb696d9ccd75a38c8a0bfe0675559b3");
        UMShareAPI.get(this)
        CommonUiSetup.pramaryColor = getResColor(R.color.colorPrimary)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}

