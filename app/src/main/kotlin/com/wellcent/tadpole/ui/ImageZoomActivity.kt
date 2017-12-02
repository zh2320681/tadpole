package com.wellcent.tadpole.ui

import android.graphics.Color
import android.os.Bundle
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.extension._urlImg
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.presenter.ROUTINE_DATA_BINDLE
import com.wellcent.tadpole.presenter.serPicPath
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.below
import org.jetbrains.anko.imageView
import org.jetbrains.anko.relativeLayout

class ImageZoomActivity : TadpoleActivity() {
    override fun initialize(savedInstanceState: Bundle?) {
        relativeLayout {
            backgroundColor = Color.BLACK
            val navView = navigateBar("图片预览") {
                setTitleColor(Color.BLACK)
                setNavBg(R.drawable.tabbar_bg)
                addLeftDefaultBtn(R.drawable.icon_back_p){ finish() }
            }.lparams(MATCH_PARENT, DimensAdapter.nav_height)
            imageView { _urlImg(intent.getStringExtra(ROUTINE_DATA_BINDLE).serPicPath()) }.lparams(MATCH_PARENT, MATCH_PARENT) { below(navView) }
        }
    }
}