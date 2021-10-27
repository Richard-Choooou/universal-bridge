package com.senguo.bridge

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class WebAdapter(var ctx: Context, var webview: WebView): PlatformAdapter {
    private var bridge: SenguoBridge? = null
    init {
        webview.addJavascriptInterface(JsInterface(), "senguoBridge")
    }

    inner class JsInterface() {
        @JavascriptInterface
        fun sendEvent(event: String) {
            val jsonData = JSONObject(event)
            val event = Input.Event(this@WebAdapter, jsonData)
            Log.i("event", jsonData.toString())
            bridge?.onEvent(event)
        }
    }

    override fun bind(bridge: SenguoBridge) {
        this.bridge = bridge
    }

    override fun reply(event: Input.Event, data: Output.Result) {
        val json = JSONObject()
        json.put("callbackName", event.callbackName)
        json.put("callbackData", data.toJson())

        val event = Output.Event("invokeCallback", json)
        sendEvent(event)
    }

    override fun sendEvent(event: Output.Event) {
        webview.evaluateJavascript("javascript:window.senguoJsBridgeWebSide._dispatchEvent('%s', %s)".format(event.name, event.data.toString())) {
            Log.i("callbackResult", it)
        }
    }
}