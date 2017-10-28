package com.shrek.klib.extension

import android.graphics.Color
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.jakewharton.rxbinding.view.RxView
import com.shrek.klib.bo.KImgBo
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.view.adaptation.DimensAdapter
import com.squareup.picasso.Picasso
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 和Ui相关的扩展
 * @author shrek
 * @date:  2016-05-25
 */

val View.screenX: Int
    get() {
        val array = intArrayOf(0, 0)
        getLocationOnScreen(array)
        return array[0]
    }

val View.screenY: Int
    get() {
        val array = intArrayOf(0, 0)
        getLocationOnScreen(array)
        return array[1]
    }

val View.centerX: Int
    get() {
        return screenX + width / 2
    }

val View.centerY: Int
    get() {
        return screenY + height / 2
    }

val View.rectF: RectF
    get() {
        return RectF(screenX.toFloat(), screenY.toFloat(), screenX + width.toFloat(), screenY + height.toFloat())
    }

val View.rect: Rect
    get() {
        return Rect(screenX, screenY, screenX + width, screenY + height)
    }

fun View.resColor(@ColorRes colorVal: Int): Int {
    return getResources().getColor(colorVal)
}

fun View.resColorDrawable(@ColorRes colorVal: Int):Drawable {
    return ColorDrawable(resColor(colorVal))
}

fun View.kRandomId(): Unit {
    id = Random().nextInt(100000)
}

fun View.onMyClick(onClick: (View) -> Unit): View {
    RxView.clicks(this)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                onClick?.invoke(this)
            }
    return this
}

fun View.setSelectorRes(normalDrawable: Drawable,selectDrawable: Drawable,disableDrawable: Drawable? = null){
    val drawable = StateListDrawable()
    //Non focused states
    drawable.addState(intArrayOf(-android.R.attr.state_focused, -android.R.attr.state_selected, -android.R.attr.state_pressed),
            normalDrawable)
    drawable.addState(intArrayOf(-android.R.attr.state_focused, android.R.attr.state_selected, -android.R.attr.state_pressed),
            selectDrawable)
    //Focused states
    drawable.addState(intArrayOf(android.R.attr.state_focused, -android.R.attr.state_selected, -android.R.attr.state_pressed),
            selectDrawable)
    drawable.addState(intArrayOf(android.R.attr.state_focused, android.R.attr.state_selected, -android.R.attr.state_pressed),
            selectDrawable)
    //Pressed
    drawable.addState(intArrayOf(android.R.attr.state_selected, android.R.attr.state_pressed),
            selectDrawable)
    drawable.addState(intArrayOf(android.R.attr.state_pressed),
            selectDrawable)
    //disable
    if (disableDrawable != null) {
        drawable.addState(intArrayOf(-android.R.attr.state_enabled),disableDrawable)
    }
    setBackgroundDrawable(drawable)
}

fun View.setSelectorRes(@DrawableRes normalRes: Int, @DrawableRes selectRes: Int, @DrawableRes disableRes: Int = -1) {
    val normalDrawable = resources.getDrawable(normalRes, null)
    val selectDrawable = resources.getDrawable(selectRes, null)
    var disableDrawable:Drawable? = null
    if (disableRes != -1) disableDrawable = resources.getDrawable(disableRes,null)
    
    setSelectorRes(normalDrawable,selectDrawable,disableDrawable)
}

/**
 * 这里的颜色是颜色值  不是资源id
 */
fun View.setSelectorColor(normalColor: Int, selectColor: Int,disableColor: Int = -1) {
    val normalDrawable = ColorDrawable(normalColor)
    val selectDrawable = ColorDrawable(selectColor)
    var disableDrawable:Drawable? = null
    if (disableColor != -1) disableDrawable = ColorDrawable(disableColor)

    setSelectorRes(normalDrawable,selectDrawable,disableDrawable)
}


fun ViewGroup.each(eachDoing: (Int, View) -> Unit) {
    val num = getChildCount()
    for (position in 0..num) {
        val view = getChildAt(position)
        eachDoing(position, view)
    }
}

/**
 * 水平方向的 分割线
 */
fun LinearLayout.horizontalSeparation(colorVal: Int = Color.WHITE
                                      , width: Int = DimensAdapter.dip10.toInt()
                                      , verticalVal: Int = DimensAdapter.dip10.toInt()):View {
    var view = TextView(context)
    view.setBackgroundColor(colorVal)

    addView(view, linearLP(width, MATCH_PARENT) {
        topMargin = verticalVal
        bottomMargin = verticalVal
    })
    return view
}

/**
 * 水平方向的 分割线
 */
fun LinearLayout.verticalSeparation(colorVal: Int = Color.WHITE
                                      , height: Int = DimensAdapter.dip10.toInt()
                                      , horizontalVal: Int = DimensAdapter.dip10.toInt()) {
    var view = TextView(context)
    view.setBackgroundColor(colorVal)

    addView(view, linearLP(MATCH_PARENT, height) {
        leftMargin = horizontalVal
        rightMargin = horizontalVal
    })
}

infix fun ImageView._urlImg(url: String) {
    _urlImg(KImgBo(url))
}

infix fun ImageView._urlImg(bo: KImgBo) {
    if (bo.imgUrl.isEmpty()) {
        if (bo.errorImg != -1) setImageResource(bo.errorImg)
        return;
    }

    val creator = Picasso.with(context).load(bo.imgUrl);

    if (bo.placeHolder != -1) {
        creator.placeholder(bo.placeHolder);
    }

    if (bo.errorImg != -1) {
        creator.error(bo.errorImg);
    }

    if (bo.sizeHeight * bo.sizeWidth != 0) {
        creator.resize(bo.sizeWidth, bo.sizeHeight);
    }

    if (bo.isCenterCrop) {
        creator.centerCrop();
    }

    if (bo.transformation != null) {
        creator.transform(bo.transformation!!);
    }

    creator.into(this);
}

fun TextView.leftDrawable(resId: Int, paddingVal: Int = 0) {
    val drawable = resources.getDrawable(resId)
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    setCompoundDrawables(drawable, null, null, null)

    compoundDrawablePadding = paddingVal
}

fun TextView.rightDrawable(resId: Int, paddingVal: Int = 0) {
    val drawable = resources.getDrawable(resId)
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    setCompoundDrawables(null, null, drawable, null)
    compoundDrawablePadding = paddingVal
}

fun TextView.topDrawable(resId: Int, paddingVal: Int = 0) {
    val drawable = resources.getDrawable(resId)
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    setCompoundDrawables(null, drawable, null, null)

    compoundDrawablePadding = paddingVal
}

/**
 * 布局参数
 */
fun Any.linearLP(width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                 height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                 init: LinearLayout.LayoutParams.() -> Unit = {})
        : LinearLayout.LayoutParams {
    val llp = LinearLayout.LayoutParams(width, height)
    llp.init()
    return llp
}


fun Any.relativeLP(width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                   height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                   init: RelativeLayout.LayoutParams.() -> Unit = {})
        : RelativeLayout.LayoutParams {
    val rlp = RelativeLayout.LayoutParams(width, height)
    rlp.init()
    return rlp
}

fun Any.frameLP(width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                init: FrameLayout.LayoutParams.() -> Unit = {})
        : FrameLayout.LayoutParams {
    val flp = FrameLayout.LayoutParams(width, height)
    flp.init()
    return flp
}

fun Any.viewGroupLP(width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                init: ViewGroup.LayoutParams.() -> Unit = {})
        : ViewGroup.LayoutParams {
    val flp = ViewGroup.LayoutParams(width, height)
    flp.init()
    return flp
}