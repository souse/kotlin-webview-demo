package com.example.hello_kotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.widget.ProgressBar
import com.alibaba.fastjson.JSONObject
import com.example.hello_kotlin.WebAppInterface.handMessageListener
import java.lang.reflect.Method

open class PicaWebViewActivity : Activity(), handMessageListener{
    lateinit var webView:  WebView
    lateinit var processBar: ProgressBar
    lateinit var JsInterface: WebAppInterface

    lateinit var webViewMethods: WebViewMethods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pica_web_view)

        webViewMethods = WebViewMethods()
        webView = findViewById(R.id.webView)
        processBar = findViewById(R.id.progressBar)

        var bundle = intent.extras
        var url = bundle.getString("url")

        Log.println(1, "bundle==========url", url)

        webView.loadUrl(url)


        // 1. 初始化交互方法
        setWebViewSetting()
    }

    /**
     * 初始化 webview 的一些设置
     */
    @SuppressLint("JavascriptInterface")
    private fun setWebViewSetting() {
        val settings = webView.settings

        JsInterface = WebAppInterface(this)
        webView.addJavascriptInterface(JsInterface, "__rocAndroid")
        webView.webViewClient = BasicWebViewClient()
        webView.webChromeClient = BasicWebChromeClient()

        settings.javaScriptEnabled = true

        // 定位(location)
        settings.setGeolocationEnabled(true);

        // 存储(storage)
        settings.domStorageEnabled = true
        // 启用Web SQL Database API，这个设置会影响同一进程内的所有WebView，默认值 false
        // 此API已不推荐使用，参考：https://www.w3.org/TR/webdatabase/
        // settings.databaseEnabled = true

        // 启用Application Caches API，必需设置有效的缓存路径才能生效，默认值 false
        // 此API已废弃，参考：https://developer.mozilla.org/zh-CN/docs/Web/HTML/Using_the_application_cache
        settings.setAppCacheEnabled(true)
        settings.setAppCachePath(cacheDir.absolutePath)

        // 是否当webview调用requestFocus时为页面的某个元素设置焦点，默认值 true
        settings.setNeedInitialFocus(true)

        // 是否支持viewport属性，默认值 false
        // 页面通过`<meta name="viewport" ... />`自适应手机屏幕
        settings.useWideViewPort = true

        // 是否使用overview mode加载页面，默认值 false
        // 当页面宽度大于WebView宽度时，缩小使页面宽度等于WebView宽度
        settings.loadWithOverviewMode = true

        // 布局算法
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL

        // 是否支持多窗口，默认值false
        settings.setSupportMultipleWindows(false)

        // 是否可用Javascript(window.open)打开窗口，默认值 false
        settings.javaScriptCanOpenWindowsAutomatically = true

        // 是否可访问Content Provider的资源，默认值 true
        settings.allowContentAccess = true
        // 是否可访问本地文件，默认值 true
        settings.allowFileAccess = true

        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        // settings.allowFileAccessFromFileURLs = false
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        // settings.allowFileAccessFromFileURLs  = false

        settings.blockNetworkImage = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 可做成配置化
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }


    private inner class BasicWebViewClient: WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            processBar.progress = 0
        }
    }

    private inner class BasicWebChromeClient: WebChromeClient()

    override fun handleMessage(methodName: String, callBackId: String, param: JSONObject) {
        runOnUiThread {
            var method: Method = webViewMethods.javaClass.getDeclaredMethod(
                methodName,
                JSONObject::class.java
            )

            var result: JSONObject = method.invoke(webViewMethods, param) as JSONObject

            if  (result != null) {
                callBack(methodName, callBackId, result)
            }
        }
    }

    override fun callBack(method: String, callBackId: String, param: JSONObject) {
        if (CommonUtils().isEmpty(callBackId)) return

        runOnUiThread {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript(
                    "rocNative.__nativeCall(\"$method\", \"$callBackId\", $param)",
                    null
                )
            } else {
                webView.loadUrl("javascript:rocNative.__nativeCall(\"$method\", \"$callBackId\", $param)")
            }
        }
    }

    /**
    public void handleMessage(String name, String callBack, JSONObject param) {
        Observable.create((ObservableOnSubscribe<Integer>) e -> {
            e.onNext(1);
            e.onComplete();
        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
            Method mothed = null;
            try {
                mothed = this.getClass().getDeclaredMethod(name, String.class, JSONObject.class);
                mothed.invoke(this, callBack, param);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }
    */
}
