package com.shrek.klib.extension

import android.app.Activity
import android.util.DisplayMetrics
import android.view.LayoutInflater
import com.shrek.klib.KApp
import com.shrek.klib.ZActivityManager
import com.shrek.klib.ZSetting
import com.shrek.klib.colligate.StringUtils
import com.shrek.klib.event.ZEventBus
import com.shrek.klib.file.FileOperator
import com.shrek.klib.net.ZNetChangeObserver
import com.shrek.klib.retrofit.LogInterceptor
import com.shrek.klib.thread.HandlerEnforcer
import com.shrek.klib.thread.ZThreadEnforcer
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.fastjson.FastJsonConverterFactory
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * 注入
 * @author shrek
 * @date:  2016-05-30
 */
val Any.kLayoutInflater: LayoutInflater by lazy {
    LayoutInflater.from(KApp.app)
}

val Any.kDisplay: DisplayMetrics by lazy {
    KApp.app.provideDisplay
}


@Application
val Any.kApplication: KApp
    get() = KApp.app

val Any.picasso: Picasso by lazy {
    Picasso.with(KApp.app)
}

@Application
val Any.actManager: ZActivityManager<Activity>
    get() = KApp.app.actManager

@Application
val Activity.kObserver: ZNetChangeObserver
    get() = KApp.app.observer

val Any.eventBus: ZEventBus
    get() = ZEventBus()

val Any.kEnforer: ZThreadEnforcer
    get() = HandlerEnforcer.newInstance()

@Application
val Any.kAppSetting: ZSetting
    get() = KApp.app.defSetting


fun Any.getOkHttpBuilder(): OkHttpClient.Builder {
    return OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).connectTimeout(30, TimeUnit.SECONDS)
}

//fun gson(): GsonBuilder {
//    val setting = KApp.app.appSetting
//    return GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().setDateFormat(setting.getGsonTimeFormat())
//}

//fun Any.jsonBuilder(): GsonBuilder {
//    return gson()
//}

/**
 * 相对屏幕的宽度
 */
fun Any.kWidth(rate:Float):Float {
    return kDisplay.widthPixels*rate
}

fun Any.kIntWidth(rate:Float):Int {
    return kWidth(rate).toInt()
}

/**
 * 屏幕的高度
 */
fun Any.kHeight(rate:Float):Float {
    return kDisplay.heightPixels*rate
}

fun Any.kIntHeight(rate:Float):Int {
    return kHeight(rate).toInt()
}

@Application
fun Any.retrofit(): Retrofit {
    val builder = getOkHttpBuilder()

    val setting = KApp.app.kAppSetting

    if (setting.isDebugMode()) {
        builder.addInterceptor(LogInterceptor())
    }

    val interceptors = setting.getCustomInterceptors()
    if (interceptors != null) {
        for (interceptor in interceptors!!) {
            builder.addInterceptor(interceptor)
        }
    }

    if (!StringUtils.isEmpty(setting.getRestCachePath())) {
        val fileOperator = FileOperator(KApp.app, setting.getRestCachePath())
        val cache = okhttp3.Cache(fileOperator.getOptFile(), 20 * 1024 * 1024.toLong())
        builder.cache(cache)
    }

    val retrofit = Retrofit.Builder().baseUrl(setting.getRestBaseUrl())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(FastJsonConverterFactory.create())
            .validateEagerly(true).client(builder.build()).build()

    return retrofit
}