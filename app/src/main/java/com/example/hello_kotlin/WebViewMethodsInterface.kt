package com.example.hello_kotlin

import com.alibaba.fastjson.JSONObject

interface WebViewMethodsInterface {
    /**
     * @description 获取token
     * @return Object对象
     */
    fun getToken(param: JSONObject): JSONObject
}