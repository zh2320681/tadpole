package com.gcl.dsm.ui.custom.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import com.shrek.klib.extension.i

abstract class IndicatorView(ctx: Context) : View(ctx){

    var selectIndex = -1
        set(value) {
            if(value >= totalNumber || value < 0){
                i("indicator index out of number")
                return
            }
            field = value
            invalidate()
        }

    var totalNumber = 0

    var gapVal = 0

    lateinit var normalSize:Size

    lateinit var selectSize:Size

    abstract fun drawNormalIndicator(canvas: Canvas , drawRect:Rect)

    abstract fun drawSelectIndicator(canvas: Canvas , drawRect:Rect)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var leftX = (width - (totalNumber*selectSize.width + gapVal*(totalNumber - 1)))/2

        var deffWidth = (selectSize.width - normalSize.width)/2

        var drawIndex = 0
        while (drawIndex < totalNumber) {
            var left = leftX + drawIndex*(selectSize.width + gapVal)
            var right = left + selectSize.width
            var top = 0
            var bottom = 0

            if (drawIndex == selectIndex) {
                top = (height - selectSize.height)/2
                bottom = top + selectSize.height

                drawSelectIndicator(canvas!!,Rect(left,top,right,bottom))
            } else {
                left += deffWidth
                right -= deffWidth
                top = (height - normalSize.height)/2
                bottom = top + normalSize.height

                drawNormalIndicator(canvas!!,Rect(left,top,right,bottom))
            }
            drawIndex++
        }
    }
}

data class Size(val width:Int,val height:Int)