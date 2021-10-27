package com.senguo.bridge

import android.content.Context
import android.net.Uri
import android.util.Log
import org.json.JSONObject
import java.io.File

class Output {
//    public var name: String = ""
//    public var data: JSONObject = JSONObject()

    class Event(val name: String, val data: JSONObject) {

    }

    class Result(val success: Boolean, val data: JSONObject?) {
        var code: String = ""
        var message: String = ""

        constructor(success: Boolean): this(success, null)

        constructor(success: Boolean, data: JSONObject?, code: String, message: String) : this(success, data) {
            this.code = code
            this.message = message
        }

        fun toJson(): JSONObject {
            val json = JSONObject()
            json.put("success", success)
            json.put("data", data)
            json.put("code", code)
            json.put("message", message)
            return json
        }
    }

    class File {
        var name: String = ""
        var size: Long = 0
        var mimeType: String = ""
        var base64: String = ""
        constructor(ctx: Context, uri: Uri) {
            val result = Utils.getFileMetaData(ctx, uri)
            if (result != null) {
                name = result.displayName
                size = result.size
                mimeType = result.mimeType
            }
            base64 = Utils.uriToBase64(ctx, uri)
            Log.i("name", result?.displayName)
            Log.i("size", result?.size.toString())
            Log.i("mimeType", result?.mimeType)
            Log.i("base64", base64)

        }

        constructor(file: java.io.File) {

        }

        fun toJson(): JSONObject {
            val json = JSONObject()
            json.put("\$\$senguoBridgePrivate", true)
            json.put("\$\$type", "file")
            json.put("name", name)
            json.put("size", size)
            json.put("mimeType", mimeType)
            json.put("base64", base64)

            return json
        }
    }
}