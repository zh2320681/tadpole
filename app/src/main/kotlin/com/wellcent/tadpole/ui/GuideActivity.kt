package com.wellcent.tadpole.ui

import android.os.Bundle
import android.support.annotation.DrawableRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.extension.kRandomId
import com.shrek.klib.ui.adapter.KFragmentPagerAdapter
import com.shrek.klib.view.KFragment
import com.wellcent.tadpole.R
import com.wellcent.tadpole.presenter.VerifyOperable
import org.jetbrains.anko.imageView
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.viewPager
import java.lang.ref.WeakReference

class GuideActivity : TadpoleActivity(),VerifyOperable{
    var tempIndex = 0
    override fun initialize(savedInstanceState: Bundle?) {
        relativeLayout { viewPager { kRandomId()
            adapter = KFragmentPagerAdapter<ImgFragment>(this@GuideActivity, WeakReference(this),arrayOf(ImgFragment(R.drawable.guide1),ImgFragment(R.drawable.guide2),
                    ImgFragment(R.drawable.guide3)) ).apply {
                onPageScrolled = { p1,p2,p3 ->
                    if(p1 == 2 && p2 == 0f && p3 == 0){ tempIndex++ } else { tempIndex = 0 }
                    if(tempIndex == 5){ 
                        verifyOpt.isFirstUse = false
                        startActivity<MainActivity>()
                        finish()
                    }
                }
            }
        }.lparams(MATCH_PARENT, MATCH_PARENT) }
    }
}

class ImgFragment() : KFragment() {
    var imageRes:Int = 0
    constructor(@DrawableRes imageRes:Int) : this(){ this.imageRes = imageRes }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI { imageView(imageRes){ scaleType = ImageView.ScaleType.FIT_XY } }.view
    }
}