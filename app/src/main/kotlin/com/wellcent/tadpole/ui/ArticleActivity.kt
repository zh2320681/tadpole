package com.wellcent.tadpole.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ImageView
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.e
import com.shrek.klib.extension.getResColor
import com.shrek.klib.extension.kIntHeight
import com.shrek.klib.extension.kIntWidth
import com.shrek.klib.ui.kDefaultRestHandler
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.bo.Article
import com.wellcent.tadpole.presenter.AppOperable
import com.wellcent.tadpole.presenter.ROUTINE_DATA_BINDLE
import com.wellcent.tadpole.presenter.ServerPath
import com.wellcent.tadpole.presenter.success
import org.jetbrains.anko.*

class ArticleActivity : TadpoleActivity(),AppOperable{
    lateinit var webView:WebView
    override fun initialize(savedInstanceState: Bundle?) {
        val article = intent.getSerializableExtra(ROUTINE_DATA_BINDLE) as Article
        verticalLayout {
            gravity = Gravity.CENTER_HORIZONTAL
            backgroundColor = getResColor(R.color.window_background)
            navigateBar("正文") {
                setTitleColor(Color.BLACK)
                setNavBg(R.drawable.tabbar_bg)
                addLeftDefaultBtn(R.drawable.icon_back_p) { finish() }
            }.lparams(MATCH_PARENT, DimensAdapter.nav_height) { bottomMargin = kIntHeight(0.015f) }
            scrollView { 
                backgroundColor = Color.WHITE
                verticalLayout { 
                    gravity = Gravity.CENTER_HORIZONTAL
                    textView(article.title) { 
                        textColor = getResColor(R.color.text_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.MID_BIG)
                    }.lparams { topMargin = kIntHeight(0.02f) }
                    textView(article.create_time) {
                        textColor = getResColor(R.color.text_little_black)
                        textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_SMALL)
                    }.lparams { topMargin = kIntHeight(0.02f) }
                    webView = webView { 
                        val settings = getSettings()
                        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN)
                    }.lparams { topMargin = kIntHeight(0.02f) }
                    linearLayout {
                        imageView(R.drawable.icon_weixin) { scaleType=ImageView.ScaleType.CENTER_INSIDE }.lparams(MATCH_PARENT, WRAP_CONTENT,1f){  }
                        imageView(R.drawable.icon_weixin1) { scaleType=ImageView.ScaleType.CENTER_INSIDE }.lparams(MATCH_PARENT, WRAP_CONTENT,1f){  }
                    }.lparams(MATCH_PARENT, WRAP_CONTENT) { verticalMargin = kIntHeight(0.02f) }
                }.lparams(MATCH_PARENT, WRAP_CONTENT){ horizontalMargin = kIntWidth(0.02f) }
            }.lparams(MATCH_PARENT, MATCH_PARENT)
        }
        appOpt.articleDetail(article.id).handler(kDefaultRestHandler("正在请求正文,请稍等... ")).success {
            val newContent = it.detail!!.content.replace("<img src=\"","<img width=\"${kIntWidth(0.96f)}px\" src=\"${ServerPath}",true)
            e(newContent)
            webView.loadData(newContent, "text/html; charset=UTF-8", null)
        }.excute(this)
    }
}