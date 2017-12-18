package com.gcl.dsm.ui.custom.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.shrek.klib.extension.kDisplay
import com.shrek.klib.extension.kIntHeight
import com.shrek.klib.extension.kIntWidth

class RectangleIndicatorView(ctx : Context , val normalColor: Int) : IndicatorView(ctx) {

    var paint = Paint()

    init {
        normalSize = Size(kIntWidth(0.005f) , kIntHeight(0.005f))
        selectSize = Size(kIntWidth(0.02f) , kIntHeight(0.005f))

        gapVal = kIntWidth(0.005f)

        paint.isAntiAlias = true
    }

    override fun drawNormalIndicator(canvas: Canvas, drawRect: Rect) {
        paint.color = normalColor
        val radius = normalSize.width.toFloat()
        canvas.drawCircle(drawRect.left.toFloat()+radius,drawRect.top.toFloat()+radius,radius,paint)
    }

    override fun drawSelectIndicator(canvas: Canvas, drawRect: Rect) {
        paint.color = normalColor
        canvas.drawRect(Rect(drawRect.left + selectSize.height/3,drawRect.top,drawRect.right-selectSize.height/3,
                drawRect.bottom),paint)
        val radius = selectSize.height.toFloat()/2
        canvas.drawCircle(drawRect.left.toFloat()+ radius,drawRect.top.toFloat() + radius,radius,paint)
        canvas.drawCircle(drawRect.right.toFloat() - radius,drawRect.top.toFloat() + radius,radius,paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(kDisplay.widthPixels,selectSize.height*3)
    }

}