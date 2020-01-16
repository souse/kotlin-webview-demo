package com.example.hello_kotlin

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView

class ChcActivity: PicaWebViewActivity() {
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var titleView: TextView = findViewById(R.id.midText)

        titleView.text = "这是H5Title"
        titleView.visibility =  1
    }
}