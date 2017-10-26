package com.wellcent.tadpole.ui.custom

import android.view.ViewManager
import com.liaoinstan.springview.widget.SpringView
import com.shrek.klib.ui.nonTheme
import com.wellcent.tadpole.R
import org.jetbrains.anko.custom.ankoView
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