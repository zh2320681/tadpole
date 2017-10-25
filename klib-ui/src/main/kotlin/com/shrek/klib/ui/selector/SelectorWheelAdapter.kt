package com.shrek.klib.ui.selector

import com.shrek.klib.ui.selector.datepick.wheel.WheelAdapter
import java.util.*

/**
 * @author Shrek
 * @date:  2017-03-17
 */
class SelectorWheelAdapter<T>(val allData: ArrayList<T>,val converter:(T)->String):WheelAdapter {

    var maxLength = 0
    
    init {
        allData.forEach { 
            val temp = converter(it)
            maxLength = Math.max(maxLength,temp.length)
        }
    }

    override fun getItem(index: Int): String {
        return converter.invoke(allData[index])
    }

    override fun getItemsCount(): Int {
        return allData.size
    }

    override fun getMaximumLength(): Int {
        return maxLength+10
    }
    
}