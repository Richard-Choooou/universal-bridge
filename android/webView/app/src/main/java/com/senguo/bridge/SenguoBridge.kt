package com.senguo.bridge

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class SenguoBridge {
    private val modules: MutableMap<String, ServiceModule> = mutableMapOf()

    fun addTarget(name: String, module: ServiceModule): Boolean {
        if (modules.containsKey(name)) return false
        modules.put(name, module)
        return true
    }

    fun use(module: ServiceModule): SenguoBridge {
        this.addTarget(module.moduleName, module)
        module.onRegister(this)
        return this
    }

    fun setAdapter(adapter: PlatformAdapter): SenguoBridge {
        adapter.bind(this)
        return this
    }

    fun onEvent(event: Input.Event) {
        AndroidSchedulers.mainThread().scheduleDirect() {
            val target = event.target
            val action = event.action

            if (target == "\$\$bridge") {
                if (action == "canIUse") {
                    val moduleName = event.params?.get("module")
                    val service = event.params?.get("service") as String
                    val module = modules[moduleName]
                    val result = module?.canIUse(service)
                    event.reply(Output.Result(result == true, null))
                } else if (action == "getServiceInfo") {
                    val moduleName = event.params?.get("module")
                    val service = event.params?.get("service") as String
                    val module = modules[moduleName]
                    val result = module?.getServiceInfo(service)
                    if (result != null) {
                        event.reply(Output.Result(true, result))
                    } else {
                        event.reply(Output.Result(false, null))
                    }
                }
            } else {
                val module = modules[target]
                if (module != null) {
                    module.onEvent(event)
                } else {
                    event.reply(Output.Result(false, null, "MODULE_NOT_FIND", "找不到 %s 目标模块".format(target)))
                }
            }
        }
    }
}