package com.wellcent.tadpole.util

import com.shrek.klib.extension.stringFormat
import java.util.*

/**
 * @author Shrek
 * @date:  2017-11-20
 */
class AlipayUtils {
    companion object {
        val APP_ID = "2017110809801870"
        val RSA2_PRIVATE = "";
        val RSA_PRIVATE = ""
        /**
         * 构造支付订单参数列表
         */
        fun buildOrderParamMap(rsa2: Boolean): Map<String, String> {
            val keyValues = HashMap<String, String>().apply {
                put("app_id", APP_ID)
//                put("biz_content", "{\"timeout_express\":\"30m\",\"product_code\":\"QUICK_MSECURITY_PAY\",\"total_amount\":\"0.01\",\"subject\":\"1\",\"body\":\"我是测试数据\",\"out_trade_no\":\"" + getOutTradeNo() + "\"}")
                put("charset", "utf-8")
                put("method", "alipay.trade.app.pay")
                put("sign_type", if (rsa2) "RSA2" else "RSA")
                put("timestamp", Date().stringFormat("yyyy-MM-dd HH:mm:ss"))
                put("version", "1.0")
            }
//            val sb = StringBuilder()
//            keyValues.forEach() { key, value ->  
//                val encodeValue = URLEncoder.encode(value, "UTF-8")
//                if(!sb.isEmpty()){ sb.append("&") }
//            }
//            for (i in 0 until keyValues.size - 1) {
//                val key = keys.get(i)
//                val value = map.get(key)
//                sb.append(buildKeyValue(key, value, true))
//                sb.append("&")
//            }
//
//            val tailKey = keys.get(keys.size - 1)
//            val tailValue = map.get(tailKey)
//            sb.append(buildKeyValue(tailKey, tailValue, true))
            return keyValues
        }
    }
    
}