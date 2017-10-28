package com.wellcent.tadpole.util

import com.shrek.klib.extension.otherThread
import com.shrek.klib.extension.uiThread
import com.shrek.klib.thread.ThreadMode
import java.util.concurrent.atomic.AtomicBoolean

class LooperTask(val timeGap:Long, val threadMode: ThreadMode = ThreadMode.MainThread, val looperHandler:()->Boolean){
    private var isStart = AtomicBoolean(true)

    fun start() {
        isStart.set(true)
        when(threadMode){
            ThreadMode.PostThread, ThreadMode.BackgroundThread -> {
                if( looperHandler()){ otherThread(timeGap){ if(isStart.get()){ start() } } }
            }
            ThreadMode.MainThread -> {
                if( looperHandler()){ uiThread(timeGap){ if(isStart.get()){ start() }  } }
            }
        }
    }
    fun stop() {
        isStart.set(false)
    }
}
