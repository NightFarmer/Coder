package com.nightfarmer.coder.ex

import android.util.Log
import okhttp3.ResponseBody
import java.io.*
import java.net.CacheResponse

/**
 * Created by zhangfan on 16-8-26.
 */

fun log(msg: Any?) {
    Log.i("mylog", "$msg")
}

inline fun <reified T> T.debug(log: Any) {
    Log.i("mylog", "##################################")
}

fun File.writeResponseBody(response: ResponseBody, callBack: (Long) -> Unit): Boolean {
    return writeStream(response.byteStream(), callBack)
}

fun File.writeStream(inputStream: InputStream, callBack: (Long) -> Unit): Boolean {
    var outputStream: OutputStream? = null
    try {
        val fileReader = ByteArray(4096)
        var fileSizeWrote: Long = 0
        outputStream = FileOutputStream(this)
        while (true) {
            val read = inputStream.read(fileReader)
            if (read == -1) {
                break
            }
            outputStream.write(fileReader, 0, read)
            fileSizeWrote += read.toLong()
            callBack.invoke(fileSizeWrote)
        }
        outputStream.flush()
        return true
    } catch (e: IOException) {
        throw IOException("file write failed...")
    } finally {
        inputStream.close()
        if (outputStream != null) {
            outputStream.close()
        }
    }
}