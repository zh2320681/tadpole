package com.wellcent.tadpole.ui.cell

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.shrek.klib.extension.kIntWidth
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import org.jetbrains.anko.backgroundColor

abstract class BaseCell(context: Context, var title:String) : RelativeLayout(context) {

    val cellTxtColor = Color.parseColor("#666666")
    val blackTxtColor = Color.parseColor("#1a1a1a")
    val hintTxtColor = Color.parseColor("#8e8e8e")

    internal var titleViewId = 0x22
    private val bottomViewId = 0x23
    private val lineViewId = 0x24
    internal var paddingValue: Int = 0

    var infoView = TextView(context)

    var isEnable = false
    var isShowGapLine = false
        set(value) {
            field = value
            var gapView = findViewById(lineViewId)
            if (gapView != null) {
                gapView.visibility = if (isShowGapLine) View.VISIBLE else View.GONE
            } else {
                if (isShowGapLine) {
                    // 显示分割线
                    val gapView = View(context)
                    gapView.id = lineViewId
                    gapView.setBackgroundColor(Color.parseColor("#dedede"))

                    val gapLp = LayoutParams(LayoutParams.MATCH_PARENT, 2)
                    gapLp.addRule(BELOW, bottomView().id)
                    gapLp.leftMargin = paddingValue
                    gapLp.rightMargin = paddingValue
                    gapLp.topMargin = paddingValue / 2
                    addView(gapView, gapLp)
                }
            }
        }


    var leftCellDraw:Int = 0
    var rightcellDraw:Int = 0

    init {
        val res = context.getResources()
        paddingValue = kIntWidth(0.03f)
        backgroundColor = Color.WHITE

        infoView.id = titleViewId
        infoView.setPadding(paddingValue, paddingValue, 0, paddingValue)
        infoView.setTextSize(DimensAdapter.textSpSize(CustomTSDimens.NORMAL))
        infoView.setTextColor(cellTxtColor)
        infoView.setText(title)
        infoView.setGravity(Gravity.CENTER_VERTICAL)

        if (leftCellDraw != 0 || rightcellDraw != 0) {
            var leftDrawable: Drawable? = null
            var rightDrawable: Drawable? = null
            if (leftCellDraw != 0) {
                leftDrawable = res.getDrawable(leftCellDraw)
                leftDrawable!!.setBounds(0, 0, leftDrawable.getMinimumWidth(),
                        leftDrawable.getMinimumHeight())
            }

            if (rightcellDraw != 0) {
                rightDrawable = res.getDrawable(rightcellDraw)
                rightDrawable!!.setBounds(0, 0, rightDrawable!!.getMinimumWidth(),
                        rightDrawable!!.getMinimumHeight())
            }

            infoView.setCompoundDrawables(leftDrawable, null, rightDrawable, null)
        }

        val titleLp = LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT)
        titleLp.addRule(ALIGN_PARENT_LEFT)
        titleLp.addRule(CENTER_VERTICAL)
        addView(infoView, 0, titleLp)

        initChildView()
//        bottomView().id = bottomViewId

        setCellEnable(isEnable)
        isFocusable = isEnable
    }

    /**
     * 初始化子类的布局
     */
    internal abstract fun initChildView()

    /**
     * 得到最后一个view
     * @return
     */
    open fun bottomView(): View {
        return infoView
    }

    /**
     * 是否有效
     * @return
     */
    abstract val isAvild: Boolean

    /**
     * 设置是否可用
     * @param isEnable
     */
    abstract fun setCellEnable(isEnable: Boolean)
}