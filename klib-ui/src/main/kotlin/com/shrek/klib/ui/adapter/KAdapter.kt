package com.shrek.klib.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.extension.kApplication
import com.shrek.klib.extension.onMyClick
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.UI

/**
 * @author Shrek
 * @date:  2017-03-15
 */
class KAdapter<SOURCE_BO, HOLDER : HolderBo>(var sourceData: Collection<SOURCE_BO>, doing: KAdapter<SOURCE_BO,HOLDER>.() -> Unit)
    : RecyclerView.Adapter<KHolder<HOLDER>>() {
    private var bindData: ((HOLDER, SOURCE_BO, Int) -> Unit)? = null

    internal lateinit var itemConstructor: () -> HOLDER

    private var itemClickDoing: ((SOURCE_BO, Int) -> Unit)? = { s, i -> }
    
    init {  doing.invoke(this)  }

    //绑定数据
    fun bindData(bindDataDoing :(HOLDER, SOURCE_BO, Int) -> Unit) {
        this.bindData = bindDataDoing
    }
    
    fun itemConstructor(itemBoConstructor : () -> HOLDER) {
        itemConstructor = itemBoConstructor
    }

    fun itemClickDoing(doing: (SOURCE_BO, Int) -> Unit) {
        itemClickDoing = doing
    }

    override fun getItemCount(): Int {
        return sourceData.size
    }

    fun getItem(position: Int): SOURCE_BO {
        return sourceData.elementAt(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): KHolder<HOLDER> {
        val bo = itemConstructor()
        return KHolder<HOLDER>(bo)
    }

    override final fun onBindViewHolder(holder: KHolder<HOLDER>, position: Int) {
        val sourceBo = getItem(position)
        bindData?.invoke(holder.bo, sourceBo,position)
        if(holder.cellHeight() > 0) {
            holder.itemView.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT,holder.cellHeight())
        }
        holder.itemView.onMyClick {
            itemClickDoing?.invoke(sourceBo, position)
        }
    }
}

class KHolder<BO : HolderBo>(var bo: BO) : RecyclerView.ViewHolder(bo.rootView) {
    fun cellHeight():Int {
        return bo.cellHeight
    }
}

//params一些参数
abstract class HolderBo(var cellHeight:Int, val params:Map<String,Any>? = null) {
    lateinit var rootView: View
    
    init {
        rootView = kApplication.UI {
            rootViewInit().invoke(this)
        }.view
    }
    
    abstract fun rootViewInit(): AnkoContext<Context>.() -> Unit
}