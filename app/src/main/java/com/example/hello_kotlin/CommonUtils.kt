package com.example.hello_kotlin

open class CommonUtils {
    fun isEmpty(str: String): Boolean {
        var ret = false

        if (str == null || str.trim().isEmpty()) {
            ret = true
        }

        return ret
    }
}