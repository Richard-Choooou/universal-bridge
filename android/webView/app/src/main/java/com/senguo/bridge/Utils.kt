package com.senguo.bridge

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import androidx.core.content.FileProvider
import com.example.webview.BuildConfig
import java.io.ByteArrayOutputStream
import java.io.File


object Utils {
    class FileMetaData {
        public var displayName: String = ""
        public var size: Long = -1
        public var mimeType: String = ""
        public var path: String = ""
    }

    fun getFileMetaData(context: Context, uri: Uri): FileMetaData? {
        val fileMetaData = FileMetaData()
        return if ("file".equals(uri.scheme, ignoreCase = true)) {
            val file = File(uri.path)
            fileMetaData.displayName = file.name
            fileMetaData.size = file.length()
            fileMetaData.path = file.path
            fileMetaData
        } else {
            val contentResolver: ContentResolver = context.contentResolver
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            fileMetaData.mimeType = contentResolver.getType(uri)!!
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
                    fileMetaData.displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    if (!cursor.isNull(sizeIndex)) fileMetaData.size = cursor.getLong(sizeIndex) else fileMetaData.size = -1
                    try {

                        fileMetaData.path = cursor.getString(cursor.getColumnIndexOrThrow("_data"))
                    } catch (e: Exception) {
                        // DO NOTHING, _data does not exist
                    }
                    return fileMetaData
                }
            } catch (e: Exception) {
                Log.e("Exception", e.toString())
            } finally {
                cursor?.close()
            }
            null
        }
    }

    fun bitmapToUri(ctx: Context, bitmap: Bitmap): Uri {
        return Uri.parse(MediaStore.Images.Media.insertImage(ctx.getContentResolver(), bitmap, null,null))
    }

//    fun uriToBase64(ctx: Context, uri: Uri): String {
//        val imageInputStream = ctx.contentResolver.openInputStream(uri)
//        val bitmap = BitmapFactory.decodeStream(imageInputStream)
//        val baos = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
//        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
//    }

    fun getUriForFileByName(ctx: Context, fileName: String?): Uri? {
        val f = File(fileName)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(ctx, BuildConfig.APPLICATION_ID, f)
        } else {
            Uri.fromFile(f)
        }
    }

    fun uriToBase64(ctx: Context, uri: Uri): String {
        val imageInputStream = ctx.contentResolver.openInputStream(uri)
        if (imageInputStream == null) return ""
        val baos = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var count = imageInputStream.read(buffer)
        while(count >= 0){
            baos.write(buffer, 0, count);//读取输入流并写入输出字节流中
            count = imageInputStream.read(buffer)
        }
        imageInputStream?.close();//关闭文件输入流
//        val bitmap = BitmapFactory.decodeStream(imageInputStream)
//        val baos = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
//        val b = baos.toByteArray()
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
    }
}