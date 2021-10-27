package com.senguo.bridge

import org.json.JSONObject

interface PlatformAdapter {
    fun bind(bridge: SenguoBridge)

    fun reply(event: Input.Event, data: Output.Result)

    fun sendEvent(event: Output.Event)
}