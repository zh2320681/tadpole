package com.wellcent.tadpole.ui.custom

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import com.shrek.klib.colligate.MATCH_PARENT
import com.shrek.klib.colligate.WRAP_CONTENT
import com.shrek.klib.extension.*
import com.shrek.klib.ui.kDefaultRestHandler
import com.shrek.klib.ui.selector.SelectorWheelAdapter
import com.shrek.klib.ui.selector.datepick.wheel.WheelView
import com.shrek.klib.ui.showAlertCrouton
import com.shrek.klib.ui.wheelView
import com.shrek.klib.view.adaptation.CustomTSDimens
import com.shrek.klib.view.adaptation.DimensAdapter
import com.wellcent.tadpole.R
import com.wellcent.tadpole.bo.City
import com.wellcent.tadpole.bo.DetectUnit
import com.wellcent.tadpole.bo.Province
import com.wellcent.tadpole.bo.ReqMapping
import com.wellcent.tadpole.presenter.AppOperable
import com.wellcent.tadpole.ui.TadpoleActivity
import org.jetbrains.anko.*

class UnitChoosePop(var hostAct: TadpoleActivity, var process: ((DetectUnit?) -> Unit)? = null) : PopupWindow(hostAct), AppOperable {
    lateinit var provinceChooseView: WheelView
    lateinit var cityChooseView: WheelView
    lateinit var unitChooseView: WheelView

    var provinceAdapter = SelectorWheelAdapter(arrayListOf<Province>()) { it.name }
    var cityAdapter = SelectorWheelAdapter(arrayListOf<City>()) { it.name }
    var unitAdapter = SelectorWheelAdapter(arrayListOf<DetectUnit>()) { it.name }

    init {
        val rootView = kApplication.UI {
            relativeLayout {
                backgroundColor = Color.parseColor("#80000000")
                onMyClick { dismiss() }

                verticalLayout {
                    backgroundColor = Color.WHITE
                    gravity = Gravity.BOTTOM

                    linearLayout {
                        gravity = Gravity.RIGHT
                        textView("确定选择") {
                            textColor = hostAct.getResColor(R.color.colorPrimary)
                            textSize = DimensAdapter.textSpSize(CustomTSDimens.SLIGHTLY_BIG)
                            gravity = Gravity.CENTER
                            verticalPadding = kIntHeight(0.02f)
                            onMyClick {
                                if (unitAdapter.itemsCount > 0) {
                                    process?.invoke(unitAdapter.allData[unitChooseView.currentItem])
                                } else {
                                    hostAct.showAlertCrouton("未选择检测机构!")
                                }
                                dismiss()
                            }
                        }.lparams(WRAP_CONTENT, WRAP_CONTENT) {
                            horizontalMargin = kIntWidth(0.04f)
                        }
                    }
                    linearLayout {
                        provinceChooseView = wheelView {
                            //                            setTextSize(DimensAdapter.textPxSize(CustomTSDimens.MID_SMALL).toInt())
                            addChangingListener { wheel, oldValue, newValue -> getCitys() }
                        }.lparams(MATCH_PARENT, MATCH_PARENT, 1f) { }
                        cityChooseView = wheelView {
                            //                            setTextSize(DimensAdapter.textPxSize(CustomTSDimens.MID_SMALL).toInt())
                            addChangingListener { wheel, oldValue, newValue -> getUnits() }
                        }.lparams(MATCH_PARENT, MATCH_PARENT, 1f) { }
                        unitChooseView = wheelView {
                            //                            setTextSize(DimensAdapter.textPxSize(CustomTSDimens.MID_SMALL).toInt())
                        }.lparams(MATCH_PARENT, MATCH_PARENT, 1f) { }
                    }.lparams(MATCH_PARENT, kIntHeight(0.4f)) {
                        bottomMargin = kIntHeight(0.1f)
                    }
                }.lparams(MATCH_PARENT, WRAP_CONTENT) { alignParentBottom() }
            }
        }.view

        contentView = rootView
        width = kIntWidth(1f)
        height = kIntHeight(1f)
        isFocusable = true
        isOutsideTouchable = true
        setBackgroundDrawable(BitmapDrawable())
        inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
//        setOnDismissListener { backgroundAlpha(1f) }
        getProvinces()
    }

    fun getProvinces() {
        val restHandler = hostAct.kDefaultRestHandler<ReqMapping<Province>>("正在请求省份信息,请稍等...")
        appOpt.provinces(hostAct, restHandler) {
            //            if (it.size > 0) {
            provinceAdapter = SelectorWheelAdapter<Province>(ArrayList(it)) { it.name }
            provinceChooseView.adapter = provinceAdapter
            uiThread(100) { getCitys() }
//            }
        }
    }

    fun getCitys() {
        val restHandler = hostAct.kDefaultRestHandler<ReqMapping<City>>("正在请求城市信息,请稍等...")
        val currProvince = provinceAdapter.allData[provinceChooseView.currentItem]
        appOpt.cities(hostAct, currProvince.province_id, restHandler) {
            //            if (it.size > 0) {
            cityAdapter = SelectorWheelAdapter<City>(ArrayList(it)) { it.name }
            cityChooseView.adapter = cityAdapter
            uiThread(1100) { getUnits() }
//            }
        }
    }

    fun getUnits() {
        val restHandler = hostAct.kDefaultRestHandler<ReqMapping<DetectUnit>>("正在请求机构信息,请稍等...")
        if(cityAdapter.allData.size == 0){ return }
        val currCity = cityAdapter.allData[cityChooseView.currentItem]
        appOpt.units(hostAct, currCity.name, restHandler) {
            //            if (it.size > 0) {
            unitAdapter = SelectorWheelAdapter(ArrayList(it)) { it.name }
            unitChooseView.adapter = unitAdapter
//            }
        }
    }

    fun show(parentView: View) {
        showAtLocation(parentView, Gravity.BOTTOM, 0, 0)
    }
}