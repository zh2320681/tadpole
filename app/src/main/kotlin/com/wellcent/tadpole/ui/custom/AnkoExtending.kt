package com.wellcent.tadpole.ui.custom

import android.content.Context
import android.support.v7.widget.CardView
import android.view.View
import android.view.ViewManager
import android.widget.FrameLayout
import com.gcl.dsm.ui.custom.indicator.RectangleIndicatorView
import com.github.barteksc.pdfviewer.PDFView
import com.liaoinstan.springview.widget.SpringView
import com.shrek.klib.ui.nonTheme
import com.wellcent.tadpole.R
import org.jetbrains.anko.custom.ankoView

open class _CardView(ctx: Context): CardView(ctx) {
    fun <T: View> T.lparams(
            c: android.content.Context?,
            attrs: android.util.AttributeSet?,
            init: FrameLayout.LayoutParams.() -> Unit = {}
    ): T {
        val layoutParams = FrameLayout.LayoutParams(c!!, attrs!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    fun <T: View> T.lparams(
            width: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            height: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            init: FrameLayout.LayoutParams.() -> Unit = {}
    ): T {
        val layoutParams = FrameLayout.LayoutParams(width, height)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }
}

inline fun ViewManager.cardView() = cardView {}
inline fun ViewManager.cardView(init: _CardView.() -> Unit) = ankoView({ _CardView(it) },0, init)
/**
 * 下拉控件
 */
inline fun ViewManager.springView() = springView {}

inline fun ViewManager.springView(addHeader: SpringView.DragHander? = null,
                                  addFooter: SpringView.DragHander? = null,
                                  init: SpringView.() -> Unit): SpringView {
    return ankoView({
        SpringView(it, it.resources.getXml(R.layout.dialog_date_pick_layout))
    },nonTheme) {
        init.invoke(this)
        val method = com.shrek.klib.colligate.ReflectUtils.getMethodByName(SpringView::class.java, "onFinishInflate")
        method.isAccessible = true
        method.invoke(this)

        header = addHeader
        footer = addFooter
    }
}


/**
 * 圆角图片
 */
inline fun ViewManager.circleImageView() = circleImageView {}
inline fun ViewManager.circleImageView(init: CircleImageView.() -> Unit) = ankoView({ CircleImageView(it) },nonTheme, init)

/**
 * 指示器
 */
inline fun ViewManager.rectangleIndicatorView(color:Int) = rectangleIndicatorView(color) {}
inline fun ViewManager.rectangleIndicatorView(color:Int,init: RectangleIndicatorView.() -> Unit) = ankoView({ RectangleIndicatorView(it,color) },nonTheme, init)

inline fun ViewManager.pdfView(init: PDFView.() -> Unit) = ankoView({ PDFView(it,null) },nonTheme, init)