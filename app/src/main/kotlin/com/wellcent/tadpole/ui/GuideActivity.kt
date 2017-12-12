package com.wellcent.tadpole.ui

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.extension.kIntHeight
import com.shrek.klib.extension.kIntWidth
import com.shrek.klib.extension.kRandomId
import com.shrek.klib.extension.uiThread
import com.shrek.klib.ui.adapter.KFragmentPagerAdapter
import com.shrek.klib.view.KFragment
import com.wellcent.tadpole.R
import com.wellcent.tadpole.presenter.VerifyOperable
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.viewPager
import java.lang.ref.WeakReference

class GuideActivity : TadpoleActivity(),VerifyOperable{
    var tempIndex = 0
    override fun initialize(savedInstanceState: Bundle?) {
        relativeLayout { viewPager { kRandomId()
            val kAdapter = KFragmentPagerAdapter<ImgFragment>(this@GuideActivity, WeakReference(this),arrayOf(ImgFragment(R.drawable.guide1),ImgFragment(R.drawable.guide2),
                    ImgFragment(R.drawable.guide3)) ){positon,oldFragment,newFragment ->
                oldFragment.reset()
                newFragment.anim()
            }.apply {
                onPageScrolled = { p1,p2,p3 ->
                    if(p1 == 2 && p2 == 0f && p3 == 0){ tempIndex++ } else { tempIndex = 0 }
                    if(tempIndex == 5){ 
                        verifyOpt.isFirstUse = false
                        startActivity<MainActivity>()
                        finish()
                    }
                }
            }
            adapter = kAdapter
            uiThread(1000){ kAdapter.getItem(0).anim() }
        }.lparams(MATCH_PARENT, MATCH_PARENT) }
    }
}

class ImgFragment() : KFragment() {
    var imageRes:Int = 0
    var alphViews = arrayListOf<View>()
    lateinit var bottomView1:View
    lateinit var bottomView2:View
    constructor(@DrawableRes imageRes:Int) : this(){ this.imageRes = imageRes }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI { 
            relativeLayout {
                imageView(imageRes){ scaleType = ImageView.ScaleType.FIT_XY }
                verticalLayout {
                    repeat(8){
                        alphViews.add(view { backgroundColor = Color.WHITE }.lparams(MATCH_PARENT, MATCH_PARENT,1f))
                    }
                }.lparams(kIntWidth(0.53f),kIntHeight(0.49f)){ centerInParent() }

                bottomView1 = view{ backgroundColor = Color.parseColor("#FEFEFE")  }.lparams(kIntWidth(0.4f), kIntHeight(0.06f)){
                    alignParentBottom()
                    centerHorizontally()
                    bottomMargin = kIntHeight(0.12f)
                }

                bottomView2 = view{ backgroundColor = Color.parseColor("#FEFEFE")
                    kRandomId()
                }.lparams(kIntWidth(0.6f), kIntHeight(0.06f)){
                    alignParentBottom()
                    centerHorizontally()
                    bottomMargin = kIntHeight(0.06f)
                }
            }
        }.view
    }
    
    fun anim(){
        var gapTime = 0L
        alphViews.forEach {
            uiThread(gapTime){
                ObjectAnimator.ofFloat(it, "alpha", 1f, 0f).apply {
                    setDuration(100)
                    start()
                }
            }
            gapTime+=100
        }
        ObjectAnimator.ofFloat(bottomView1, "alpha", 1f, 0f).apply {
            setDuration(300)
            start()
        }
        ObjectAnimator.ofFloat(bottomView2, "alpha", 1f, 0f).apply {
            setDuration(500)
            start()
        }
    }
    
    fun reset(){
        alphViews.forEach { it.alpha = 1f }
        bottomView1.alpha = 1f
        bottomView2.alpha = 1f
    }
}