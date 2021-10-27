package com.senguo.service

import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.senguo.bridge.*
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import org.json.JSONObject

class Camera(val ctx: AppCompatActivity): ServiceModule {
    override val moduleName: String = "camera"
    private val TAKE_PICTURE: String = "takePicture"
    private val TAKE_VIDEO = "takeVideo"
    private var takePictureRegisterResult: ActivityResultLauncher<Void>? = null
    private var takeVideoRegisterResult: ActivityResultLauncher<Uri>? = null
    private var eventMap: MutableMap<String, Input.Event> = mutableMapOf()
    private var takeVideoTempUri: Uri? = null

    init {
        takePictureRegisterResult = ctx.registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            val event = eventMap.get(TAKE_PICTURE)
            if (it != null) {
                val uri = Utils.bitmapToUri(ctx, it)
                var json = JSONObject()
                val file = Output.File(ctx, uri)
                json.put("file", file.toJson())
                val result = Output.Result(true, json)
                event?.reply(result)
            } else {
                event?.reply(Output.Result(false, null, "CANCEL", "取消拍照"))
            }
            eventMap.remove(TAKE_PICTURE)
        }

        takeVideoRegisterResult = ctx.registerForActivityResult(ActivityResultContracts.TakeVideo()) {
            val event = eventMap.get(TAKE_VIDEO)
//            if (it != null) {
//                Utils.uriToBase64(ctx, takeVideoTempUri!!)
//                val uri = Utils.bitmapToUri(ctx, it)
                var json = JSONObject()
                val file = Output.File(ctx, takeVideoTempUri!!)
                json.put("file", file.toJson())
                val result = Output.Result(true, json)
                event?.reply(result)
//            } else {
//                event?.reply(Output.Result(false, null, "CANCEL", "取消录像"))
//            }
            eventMap.remove(TAKE_VIDEO)
        }
    }

    override fun onRegister(bridge: SenguoBridge) {

    }

    override fun canIUse(service: String): Boolean {
        val serviceList: List<String> = listOf(TAKE_PICTURE, TAKE_VIDEO)
        return serviceList.contains(service)
    }

    override fun getServiceInfo(service: String): JSONObject? {
        if (service == TAKE_PICTURE) {
            return takePictureServiceInfo()
        }

        return null
    }

    override fun onEvent(event: Input.Event) {
        val action = event.action
        when(action) {
            TAKE_PICTURE -> takePicture(event)
            TAKE_VIDEO -> takeVideo(event)
        }
    }

    private fun takePictureServiceInfo(): JSONObject {
        val json = JSONObject()
        json.put("versionName", "0.0.1")
        json.put("versionCode", 1)

        return json
    }

    private fun takePicture(event: Input.Event) {
        if (ctx.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            eventMap.put(TAKE_PICTURE, event)
            takePictureRegisterResult?.launch(null)
        } else {
            val rxPermissions = RxPermissions(ctx)
            AndroidSchedulers.mainThread().scheduleDirect() {
                rxPermissions.request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe() {
                        if (it) {
                            takePicture(event)
                        }
                    }
            }
        }
    }

    private fun takeVideo(event: Input.Event) {
        eventMap.put(TAKE_VIDEO, event)
        takeVideoTempUri = Utils.getUriForFileByName(ctx, ctx.cacheDir.path + "/temp1.mp4")
        takeVideoRegisterResult?.launch(takeVideoTempUri)
    }
}