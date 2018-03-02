package com.wellcent.tadpole.ui

import android.Manifest
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnDrawListener
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.shrek.klib.colligate.BaseUtils
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.download.DialogDLHandler
import com.shrek.klib.colligate.download.bo.DLTask
import com.shrek.klib.ui.kDefaultRestHandler
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.presenter.ROUTINE_DATA_BINDLE
import com.wellcent.tadpole.presenter.serPdfPath
import com.wellcent.tadpole.ui.custom.pdfView
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.below
import org.jetbrains.anko.relativeLayout
import java.io.File

class PDFActivity : TadpoleActivity(),OnLoadCompleteListener,OnErrorListener,OnDrawListener {
    val kHandler = kDefaultRestHandler<String>(" 正在加载PDF,请稍等... ")
    lateinit var pdfView:PDFView
    override fun initialize(savedInstanceState: Bundle?) {
        relativeLayout {
            backgroundColor = Color.BLACK
            val navView = navigateBar("PDF预览") {
                setTitleColor(Color.BLACK)
                setNavBg(R.drawable.tabbar_bg)
                addLeftDefaultBtn(R.drawable.icon_back_p){ finish() }
            }.lparams(MATCH_PARENT, DimensAdapter.nav_height)
            pdfView = pdfView {  }.lparams(MATCH_PARENT, MATCH_PARENT) { below(navView) }
           
//            kHandler.pre()
        }

        val pdfPath = intent.getStringExtra(ROUTINE_DATA_BINDLE).serPdfPath()
        downLoad(pdfPath)
    }

    override fun loadComplete(nbPages: Int) {
        kHandler.post("")
    }

    override fun onError(t: Throwable?) {
        kHandler.error(t)
    }

    override fun onLayerDrawn(canvas: Canvas?, pageWidth: Float, pageHeight: Float, displayedPage: Int) {
    }


    fun downLoad(path: String) {
        val task = DLTask(Uri.encode(path, "-![.:/,%?&=]") ).also { it.isAutoOpen = false }
        requestPermissionsWithCallBack(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            BaseUtils.downloadFile(this,task , object : DialogDLHandler(this) {
                override fun isDLFileExist(task: DLTask?): Boolean {
                    return false
                }

                override fun postDownLoadingOnUIThread(task: DLTask?) {
                    super.postDownLoadingOnUIThread(task)
                    val file = File(task!!.savePath, task!!.fileName)
                    pdfView.fromFile(file)
//                    .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
                    .enableSwipe(true) // allows to block changing pages using swipe
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    // allows to draw something on the current page, usually visible in the middle of the screen
//                    .onDraw(onDrawListener)
                    .onDrawAll(this@PDFActivity)
                    .onLoad(this@PDFActivity) 
//                    .onPageChange(onPageChangeListener)
//                    .onPageScroll(onPageScrollListener)
                    .onError(this@PDFActivity)
//                    .onPageError(onPageErrorListener)
//                    .onRender(onRenderListener) 
                    .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                    .password(null)
                    .scrollHandle(null)
                    .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                    // spacing between pages in dp. To define spacing color, set view background
                    .spacing(0)
                    .load()
                }
            })
        }
    }
}