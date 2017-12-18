package com.shrek.klib.ui.navigate

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.shrek.klib.colligate.AndroidVersionCheckUtils
import com.shrek.klib.extension.each
import com.shrek.klib.extension.kDisplay
import com.shrek.klib.extension.relativeLP
import com.shrek.klib.ui.R
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import org.jetbrains.anko.*
import java.util.*

/**
 * @author shrek
 * @date:  2016-06-02
 */
class NavigateBar(context: Context) : RelativeLayout(context) {

    private val LEFT_ID = 0x13

    private val RIGHT_ID = 0x99

    private var paddingValue = kDisplay.widthPixels / 21

    private var shadowWidth = DimensAdapter.dip2.toInt()

    private val titleView: TextView by lazy {
        val tv = TextView(context)
        tv.textSize = DimensAdapter.textSpSize(CustomTSDimens.NAV_TITLE)
        tv.textColor = Color.WHITE
        this@NavigateBar.addView(tv,relativeLP { centerInParent() })
        tv
    }

    init {
        id = Random().nextInt(100000)
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        setMeasuredDimension(kDisplay.widthPixels,DimensAdapter.nav_height)
//    }

    fun setTitleBg(drawableId: Int) {
        titleView.setText(null)
        titleView.setBackgroundResource(drawableId)
    }

    fun setNavBg(drawableId: Int) {
       backgroundResource =  drawableId
    }
    
    fun setNavBgColor(colorVal:Int,isElevation: Boolean = true){
        if (!isElevation) {
            backgroundColor = colorVal
            return
        }
        if (AndroidVersionCheckUtils.hasLOLLIPOP()) {
            elevation = DimensAdapter.dip5
            backgroundColor = colorVal
        } else {
            val mDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(0x7000000, 0x50000000.toInt()))
            mDrawable.setShape(GradientDrawable.RECTANGLE)
            val bottomDrawable = GradientDrawable()
            bottomDrawable.setColor(colorVal)
            bottomDrawable.setShape(GradientDrawable.RECTANGLE)

            val topDrawable = GradientDrawable()
            topDrawable.setColor(colorVal)

            val layerDrawable = LayerDrawable(arrayOf<Drawable>(topDrawable,mDrawable,bottomDrawable))
            layerDrawable.setLayerInset(1, 0, 0, 0, shadowWidth)
            layerDrawable.setLayerInset(0, 0, shadowWidth, 0, 0)
            layerDrawable.setLayerInset(2, 0, 0, 0, 0)
            backgroundDrawable = layerDrawable
        }

    }
    
    fun setTitle(title: String) {
        titleView.setVisibility(View.VISIBLE)
        titleView.setText(title)
    }

    fun setTitleColor(color: Int) {
        titleView.setTextColor(color)    
    }
    
    fun setVisible(isVisible: Boolean) {
        setVisibility(if (isVisible) View.VISIBLE else View.GONE)
    }

    /**
     * 添加左边的按钮
     * @param btn
     */
    fun addLeftBtn(btn: View, listener: (View) -> Unit) {
        var findLastLeftId = LEFT_ID

        val rlp = relativeLP(height = matchParent) {
            centerVertically()

            while (true) {
                val view = findViewById(findLastLeftId)
                if (view == null) {
                    if (findLastLeftId == LEFT_ID) {
                        addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                        btn.id = findLastLeftId
                    } else {
                        addRule(RelativeLayout.RIGHT_OF, --findLastLeftId)
                        btn.id = ++findLastLeftId
                    }
                    break
                }
                findLastLeftId++
            }
        }
        btn.setOnClickListener(listener)
        btn.setPadding(if (findLastLeftId != LEFT_ID) paddingValue / 2 else paddingValue, 0, paddingValue / 2, 0)

        addView(btn, rlp)
    }

    fun addLeftDefaultBtn(drawId: Int, listener: (View) -> Unit) {
        val btn = ImageButton(context)
        btn.setBackgroundResource(R.drawable.navigate_btn)
        btn.setImageResource(drawId)
        addLeftBtn(btn, listener)
    }

    /**
     * 移除左边所有的btn
     */
    fun removeAllLeftBtn() {
        each { position, view ->
            val viewID = view.id
            if (Math.abs(viewID - LEFT_ID) <= 20) {
                removeViewAt(position)
            }
        }
    }

    /**
     * 通过下标获取左边的view

     * @param position
     * *
     * @return
     */
    fun getLeftViewByPosition(position: Int): View? {
        return findViewById(LEFT_ID + position)
    }


    fun setLeftViewVisibility(position: Int, visibility: Int) {
        val view = findViewById(LEFT_ID + position)
        view.setVisibility(visibility)

        val tempView = getLeftViewByPosition(position + 1) ?: return

        val rlp = tempView.layoutParams as RelativeLayout.LayoutParams
        if (visibility == View.VISIBLE) {
            rlp.addRule(RelativeLayout.RIGHT_OF, LEFT_ID + position)
            rlp.removeRule(RelativeLayout.ALIGN_PARENT_LEFT)
        } else {
            if (position == 0) {
                rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            } else {
                rlp.addRule(RelativeLayout.RIGHT_OF, LEFT_ID + position - 1)
            }
        }
    }

    /**
     * 添加右边的按钮

     * @param btn
     */
    fun addRightBtn(btn: View, listener: (View) -> Unit) {
        var findLastLeftId = RIGHT_ID

        val rlp = relativeLP(height = matchParent) {
            while (true) {
                val view = findViewById(findLastLeftId)
                if (view == null) {
                    if (findLastLeftId == RIGHT_ID) {
                        addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                        btn.id = findLastLeftId
                    } else {
                        addRule(RelativeLayout.LEFT_OF, --findLastLeftId)
                        btn.id = ++findLastLeftId
                    }
                    break
                }
                findLastLeftId++
            }
            centerVertically()
        }

        btn.setOnClickListener(listener)
        btn.setPadding(paddingValue / 2, 0, if (findLastLeftId != RIGHT_ID) paddingValue / 2 else paddingValue, 0)
        addView(btn, rlp)
    }

    fun addRightDefaultBtn(drawId: Int, listener: (View) -> Unit) {
        val btn = ImageButton(context)
        // btn.setBackgroundResource(drawId);
        btn.setBackgroundResource(R.drawable.navigate_btn)
        btn.setImageResource(drawId)
        addRightBtn(btn, listener)
    }

    fun addRightTxt(text: String,textColor:Int,textSize:Float, listener: (View) -> Unit) {
        val tv = TextView(context)
        tv.gravity = Gravity.CENTER
        tv.text = text
        tv.textColor = textColor
        tv.textSize = textSize
//        tv.setBackgroundResource(R.drawable.navigate_btn)
        addRightBtn(tv, listener)
    }

    /**
     * 移除右边所有的btn
     */
    fun removeAllRightBtn() {
        val childNum = childCount
        for (i in childNum - 1 downTo 0) {
            val view = getChildAt(i)
            val viewID = view.id
            if (Math.abs(viewID - RIGHT_ID) <= 20) {
                removeViewAt(i)
            }
        }
    }

    /**
     * 通过下标获取右边的view

     * @param position
     * *
     * @return
     */
    fun getRightViewByPosition(position: Int): View? {
        return findViewById(RIGHT_ID + position)
    }


    fun setRightViewVisibility(position: Int, visibility: Int) {
        val view = findViewById(RIGHT_ID + position)
        view.setVisibility(visibility)

        val tempView = getRightViewByPosition(position + 1) ?: return

        val rlp = tempView.layoutParams as RelativeLayout.LayoutParams
        if (visibility == View.VISIBLE) {
            rlp.addRule(RelativeLayout.LEFT_OF, RIGHT_ID + position)
            rlp.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        } else {
            if (position == 0) {
                rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            } else {
                rlp.addRule(RelativeLayout.LEFT_OF, RIGHT_ID + position - 1)
            }
        }
    }

}