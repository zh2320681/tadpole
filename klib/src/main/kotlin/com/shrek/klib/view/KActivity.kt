package com.shrek.klib.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import com.shrek.klib.colligate.*
import com.shrek.klib.exception.ZAppException
import com.shrek.klib.net.ZNetChangeObserver
import com.shrek.klib.net.ZNetWorkUtil
import com.shrek.klib.net.ZNetworkStateReceiver
import com.shrek.klib.retrofit.RestContainer
import com.shrek.klib.retrofit.RestExcuter
import java.util.*
import android.support.v4.content.PermissionChecker
import android.R.attr.targetSdkVersion
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import com.shrek.klib.extension.*


/**
 * @author shrek
 * @date:  2016-05-31
 */
abstract class KActivity : AppCompatActivity(), ZNetChangeObserver, KeyboardFocus, RestContainer {
    
    val activityName: String by lazy {
        this.javaClass.simpleName
    }

    val restExcuters: MutableSet<RestExcuter<*>> by lazy {
        mutableSetOf<RestExcuter<*>>()
    }

    val keyboardFocusViews: MutableSet<View> by lazy {
        mutableSetOf<View>()
    }
    
    private val permissionsProcess = hashMapOf<Int,()->Unit>()
    
    open var refuseCallback:(List<PermissionDescript>)->Unit = {}
    open var neverRefuseCallback:(List<PermissionDescript>)->Unit = {
        toastLongShow("aaaaaaaa")
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preCreate(savedInstanceState)

        ZAppException.getInstance(this, kAppSetting.isCaptureError())
        actManager.pushActivity(this)

        initialize(savedInstanceState)
    }


    /**
     * 创建前做什么
     * @param savedInstanceState
     */
    open fun preCreate(savedInstanceState: Bundle?) {

    }

    /**
     * 一定要写注入的方法 初始化

     * @return layout ID
     */
    protected abstract fun initialize(savedInstanceState: Bundle?)

    /**
     * 是否注册事件总线
     * @return
     */
    open protected fun isRegisterBus(): Boolean {
        return false
    }

    /**
     * 是否监听网络
     * @return
     */
    open protected fun isMonitorNetwork(): Boolean {
        return false
    }

    /**
     * fragment的操作
     */
    fun fragmentOpt(run: (FragmentTransaction) -> Unit) {
        val trans = supportFragmentManager.beginTransaction()
        run(trans)
        trans.commit()
    }

    fun addFragment(parentId: Int, vararg fragments: Fragment) {
        fragmentOpt {
            for (fragment in fragments) {
                it.add(parentId, fragment, fragment.tag)
            }
        }
    }

    /**
     * 通过tag查找fragment

     * @param tag
     * *
     * @return
     */
    fun findFragmentByTag(tag: String): Fragment {
        return supportFragmentManager.findFragmentByTag(tag)
    }

    fun findFragmentById(@IdRes fragmentId: Int): Fragment {
        return supportFragmentManager.findFragmentById(fragmentId)
    }

    override fun onResume() {
        super.onResume()

        if (isMonitorNetwork()) {
            ZNetworkStateReceiver.registerNetworkStateReceiver(this)
            ZNetworkStateReceiver.registerObserver(this)
        }

        if (isRegisterBus()) {
            eventBus.register(this)
        }

    }

    override fun onPause() {
        super.onPause()
        if (isMonitorNetwork()) {
            ZNetworkStateReceiver.unRegisterNetworkStateReceiver(this)
            ZNetworkStateReceiver.removeRegisterObserver(this)
        }

        if (isRegisterBus()) {
            eventBus.unregister(this)
        }

        cancelAllRestExcuter()
    }

    override fun onDestroy() {
        super.onDestroy()
        actManager.popActivity(this)
    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (BaseUtils.isShouldHideInput(v, ev, keyboardFocusViews)) {
                BaseUtils.hideSoftInput(this, v!!.windowToken)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            val value = activityBackDoing()
            if(value){
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * activity退出做什么
     */
    open fun activityBackDoing():Boolean{
        return false
    }
    

    override fun addKeyboardFocus(vararg views: View) {
        views.forEach { keyboardFocusViews.add(it) }
    }

    override fun addRestExcuter(excuter: RestExcuter<*>) {
        restExcuters.add(excuter)
    }

    override fun cancelAllRestExcuter() {
//        if (restExcuters != null) {
            for (excuter in restExcuters) {
                excuter.cancel()
            }
            restExcuters.clear()
//        }
    }

    override fun removeRestExcuter(excuter: RestExcuter<*>) {
        if (restExcuters != null) {
            restExcuters.remove(excuter)
        }
    }
    
    fun requestPermissionsWithCallBack( permissions: Array<String>, passCallback: () -> Unit) {
        if(AndroidVersionCheckUtils.hasM()) {
            val needPermissionList = permissions.filter {
                var result = true
                if (targetSdkVersion() >= Build.VERSION_CODES.M){
                    result = ( checkSelfPermission(it)==PackageManager.PERMISSION_DENIED)
                }else {
                    result = PermissionChecker.checkSelfPermission(this, it) == PermissionChecker.PERMISSION_DENIED
                }
                result
            }
            if (needPermissionList.isEmpty()) {
                passCallback()
                return
            }
            val requestCode = Random().nextInt(10000)
            ActivityCompat.requestPermissions(this, permissions, requestCode)
            permissionsProcess.put(requestCode,passCallback)
        } else {
            passCallback()
        }
    }
    
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsProcess[requestCode]?.apply {
            val refuseCallbackTemp:(List<String>) -> Unit = {
                refuseCallback( permissionDesc(it) )
            }

            val neverRefuseCallbackTemp:(List<String>) -> Unit = {
                neverRefuseCallback( permissionDesc(it) )
            }
            permissionsResult(requestCode,permissions,grantResults,refuseCallbackTemp,neverRefuseCallbackTemp,this)
        }
        
    }
    
    /**
     * 网络连接连接时调用
     */
    open override fun onConnect(type: ZNetWorkUtil.NetType) {

    }

    /**
     * 当前没有网络连接
     */
    open override fun onDisConnect() {
        
    }
    
}