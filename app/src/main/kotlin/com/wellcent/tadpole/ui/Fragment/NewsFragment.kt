package com.wellcent.tadpole.ui.Fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.liaoinstan.springview.container.DefaultHeader
import com.liaoinstan.springview.widget.SpringView
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.extension.*
import com.shrek.klib.ui.adapter.HolderBo
import com.shrek.klib.ui.adapter.KAdapter
import com.shrek.klib.ui.navigateBar
import com.shrek.klib.view.KFragment
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.bo.Article
import com.wellcent.tadpole.presenter.AppOperable
import com.wellcent.tadpole.presenter.ROUTINE_DATA_BINDLE
import com.wellcent.tadpole.presenter.listSuccess
import com.wellcent.tadpole.presenter.serPicPath
import com.wellcent.tadpole.ui.ArticleActivity
import com.wellcent.tadpole.ui.custom.springView
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.startActivity

class NewsFragment : KFragment(),AppOperable {
    lateinit var springView:SpringView
    lateinit var recyclerView: RecyclerView
    
    var adapter:KAdapter<Article, NewsHolder>? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI{
            verticalLayout {
                navigateBar("孕育讲堂") {
                    setTitleColor(Color.BLACK)
                    setNavBg(R.drawable.tabbar_bg)
                }.lparams(MATCH_PARENT, DimensAdapter.nav_height)
                springView = springView(DefaultHeader(context), DefaultHeader(context)){
                    setGive(SpringView.Give.TOP)
                    recyclerView = recyclerView {
                        backgroundColor = resColor(R.color.window_background)
                        layoutManager = LinearLayoutManager(context)
                    }.lparams(MATCH_PARENT, MATCH_PARENT)

                    setListener(object : SpringView.OnFreshListener {
                        override fun onRefresh() {
                            requestNews()
                        }
                        override fun onLoadmore() {}
                    })
                }.lparams(MATCH_PARENT, MATCH_PARENT){  }
            }
        }.view
    }

    override fun onShow() {
        super.onShow()
        if(adapter == null) { requestNews() }
    }
    
    fun requestNews(){
        springView.callFresh()
        appOpt.articles().listSuccess {
            adapter = KAdapter<Article, NewsHolder>(it) {
                itemConstructor { NewsHolder(kIntHeight(0.31f)) }
                itemClickDoing { bo, i -> startActivity<ArticleActivity>( ROUTINE_DATA_BINDLE to bo ) }
                bindData { holder, bo, i ->
                    holder.timeView.text = bo.create_time
                    holder.desView.text = bo.vice_title
                    holder.titleView.text = bo.title
                    holder.imgView._urlImg(bo.title_image.serPicPath())
                    springView.onFinishFreshAndLoad()
                }
            }
            recyclerView.adapter = adapter
        }.error{ springView.onFinishFreshAndLoad() }.excute(hostAct)
    }
}

class NewsHolder(cellHeight:Int): HolderBo(cellHeight) {
    lateinit var timeView:TextView
    lateinit var desView:TextView
    lateinit var titleView:TextView
    lateinit var imgView:ImageView
    override fun rootViewInit(): AnkoContext<Context>.() -> Unit {
        return  {
            relativeLayout {
                timeView = textView("2017-10-14 14:51:50"){
                    kRandomId()
                    textColor = context.getResColor(R.color.cell_title)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.MID_SMALL)
                }.lparams { centerHorizontally()    
                    alignParentBottom()
                    bottomMargin = kIntHeight(0.01f)
                }
                desView = textView("简洁简洁简洁简洁简洁简洁"){
                    kRandomId()
                    textColor = context.getResColor(R.color.cell_title)
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_SMALL)
                }.lparams { centerHorizontally()
                    above(timeView)
                    bottomMargin = kIntHeight(0.01f)
                }
                titleView = textView("标题标题"){
                    kRandomId()
                    textColor = Color.BLACK
                    textSize = DimensAdapter.textSpSize(CustomTSDimens.NORMAL)
                }.lparams { centerHorizontally()
                    above(desView)
                    bottomMargin = kIntHeight(0.01f)
                }
                imgView = imageView(R.drawable.mf_top) { scaleType = ImageView.ScaleType.FIT_XY }.lparams(MATCH_PARENT, MATCH_PARENT){
                    above(titleView)
                    bottomMargin = kIntHeight(0.01f)
                }
            }
        }
    }
    
}