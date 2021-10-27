package com.senguo.bridge

import org.json.JSONObject

class Input {

    class Event(val platform: PlatformAdapter, event: JSONObject) {
        public var target: String = ""
        public var action: String = ""
        public var params: JSONObject? = JSONObject()
        public var callbackName: String? = null

        init {
            target = event.getString("target")
            action = event.getString("action")
            params = event.getJSONObject("params")
            callbackName = event.getString("callbackName")
        }

        fun reply(data: Output.Result) {
            platform.reply(this, data)
        }
    }
}