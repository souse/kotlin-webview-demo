package com.example.hello_kotlin

import android.webkit.JavascriptInterface
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject

class WebAppInterface(mContext: handMessageListener) {
    private var listener: handMessageListener? = null

    init {
        this.listener = mContext
    }

    @JavascriptInterface
    fun postMessage(json: String) {
        try {
            var jsonObject: JSONObject = JSON.parseObject(json)
            var methodName: String  = jsonObject.getString("name")
            var callbackId: String = jsonObject.getString("callbackId")
            var param: JSONObject = jsonObject.getJSONObject("param")

            if (!CommonUtils().isEmpty(methodName)) {
                when (methodName) {
                    "getToken" -> listener!!.handleMessage(methodName, callbackId, param)
                    else -> println(json)
                }
            } else {
                listener!!.callBack("", callbackId, param)
            }
        } catch (e: Exception) {
            listener!!.callBack("", "", JSONObject())
            // 这里应该还需要收集错误日志
        }
    }

    /**
     * 统一处理JS方法
     */
    interface handMessageListener {
        /**
         * 调用方法
         */
        fun handleMessage(method: String, callBackId: String, param: JSONObject)

        /**
         * 响应JS回调
         */
        fun callBack(method: String, callBackId: String, param: JSONObject)
    }
}