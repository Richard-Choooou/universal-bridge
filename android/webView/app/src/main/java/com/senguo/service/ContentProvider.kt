package com.senguo.service

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.senguo.bridge.Input
import com.senguo.bridge.Output
import com.senguo.bridge.SenguoBridge
import com.senguo.bridge.ServiceModule
import org.json.JSONObject

class ContentProvider(ctx: AppCompatActivity): ServiceModule {
    override val moduleName: String = "contentProvider"
    private val CHOOSE_FILE: String = "chooseFile"
    private var getContentRegisterResult: ActivityResultLauncher<String>? = null
    private var eventMap: MutableMap<String, Input.Event> = mutableMapOf()

    init {
        getContentRegisterResult = ctx.registerForActivityResult(ActivityResultContracts.GetContent()) {
            val event = eventMap.get(CHOOSE_FILE)
            if (it != null) {
                var json = JSONObject()
                val file = Output.File(ctx, it)
                json.put("file", file.toJson())
                val result = Output.Result(true, json)
                event?.reply(result)
            } else {
                event?.reply(Output.Result(false, null, "CANCEL", "未选择文件"))
            }
            eventMap.remove(CHOOSE_FILE)
        }
    }

    override fun onRegister(bridge: SenguoBridge) {

    }

    override fun canIUse(service: String): Boolean {
        val serviceList: List<String> = listOf(CHOOSE_FILE)
        return serviceList.contains(service)
    }

    override fun getServiceInfo(service: String): JSONObject? {
        if (service == CHOOSE_FILE) {
            return chooseFileServiceInfo()
        }

        return null
    }

    override fun onEvent(event: Input.Event) {
        val action = event.action
        when(action) {
            CHOOSE_FILE -> chooseFile(event)
        }
    }

    private fun chooseFileServiceInfo(): JSONObject {
        val json = JSONObject()
        json.put("versionName", "0.0.1")
        json.put("versionCode", 1)

        return json
    }

    private fun chooseFile(event: Input.Event) {
        eventMap.put(CHOOSE_FILE, event)
        val type = event.params?.get("type") as String

        if (type == null) {
            event.reply(Output.Result(false, null, "MISS_PARAMS", "需要传递一个 type"))
        } else {
            getContentRegisterResult?.launch(type)
        }
    }
}