package com.example.webview

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
//import android.webkit.WebView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.senguo.bridge.*
import com.senguo.service.Camera
import com.senguo.service.ContentProvider
import android.webkit.WebView as ChromeWebView
//import com.tencent.smtt.sdk.WebView as TencentWebView
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initWebView()
    }

    fun initWebView() {
        var webview = findViewById<ChromeWebView>(R.id.webView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            com.tencent.smtt.sdk.WebView.setWebContentsDebuggingEnabled(true);
        }

        SenguoBridge()
            .use(ContentProvider(this))
            .use(Camera(this))
            .setAdapter(WebAdapter(this, webview)) // web 平台，已实现
//            .setAdapter(FlutterAdapter(this)) // flutter 平台，待实现
//            .setAdapter(ReactNativeAdapter(this)) // react native 平台，待实现

        webview.settings.javaScriptEnabled = true
        webview.loadUrl("file:///android_asset/html/index.html")
    }
}