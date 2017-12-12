package com.wellcent.tadpole.ui

import android.os.Bundle
import android.widget.ImageView
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.extension.uiThread
import com.shrek.klib.view.KActivity
import com.wellcent.tadpole.R
import com.wellcent.tadpole.presenter.VerifyOperable
import org.jetbrains.anko.imageView
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.startActivity


class LaunchActivity : KActivity(),VerifyOperable{
    override fun initialize(savedInstanceState: Bundle?) {
        relativeLayout { imageView(R.drawable.launch){ scaleType = ImageView.ScaleType.FIT_XY }.lparams(MATCH_PARENT, MATCH_PARENT) }
        uiThread(2000){ if(verifyOpt.isFirstUse){ startActivity<GuideActivity>()
            finish() } else { startActivity<MainActivity>() }
            finish()}
    }
}