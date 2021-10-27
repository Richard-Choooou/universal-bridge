package com.senguo.bridge

import org.json.JSONObject

interface ServiceModule {
    val moduleName: String
    fun onRegister(bridge: SenguoBridge)

    fun canIUse(service: String): Boolean

    fun getServiceInfo(service: String): JSONObject?

    fun onEvent(event: Input.Event)
}