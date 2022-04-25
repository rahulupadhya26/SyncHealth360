package com.app.wecare.interceptor

import android.util.Log
import com.app.wecare.crypto.CryptoStrategy
import com.app.wecare.crypto.CryptoUtil.requestBodyToString
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException


class EncryptionInterceptor(private val mEncryptionStrategy: CryptoStrategy?) :
    Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val rawBody = request.body
        var encryptedBody: String? = ""
        val mediaType = "text/plain; charset=utf-8".toMediaTypeOrNull()
        if (mEncryptionStrategy != null) {
            try {
                val rawBodyStr: String = requestBodyToString(rawBody!!)
                encryptedBody = mEncryptionStrategy.encrypt(rawBodyStr)
                Log.i("Raw body=> %s", rawBodyStr)
                Log.i("Encrypted BODY=> %s", encryptedBody!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            throw IllegalArgumentException("No encryption strategy!")
        }
        val body = encryptedBody!!.toRequestBody(mediaType)
        //        request = request.newBuilder().header("User-Agent", "Your-App-Name");
        request = request.newBuilder().header("Content-Type", body.contentType().toString())
            .header("Content-Length", body.contentLength().toString())
            .method(request.method, body).build()
        return chain.proceed(request)
    }
}