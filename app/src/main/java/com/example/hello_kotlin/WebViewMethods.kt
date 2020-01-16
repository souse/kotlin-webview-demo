package com.example.hello_kotlin

import com.alibaba.fastjson.JSONObject

class WebViewMethods: WebViewMethodsInterface {
    override fun getToken(param: JSONObject): JSONObject {
        println("调用了 getToken ...")

        param.put("userToken", "12345678i9")

        return param
    }
}