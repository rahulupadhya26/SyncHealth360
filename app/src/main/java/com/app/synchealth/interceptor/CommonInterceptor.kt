package com.app.synchealth.interceptor

import com.app.synchealth.crypto.CryptoUtil
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class CommonInterceptor :Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val rawBody = request.body
        val rawBodyStr: String = CryptoUtil.requestBodyToString(rawBody!!)
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = rawBodyStr!!.toRequestBody(mediaType)
        //        request = request.newBuilder().header("User-Agent", "Your-App-Name");
        request = request.newBuilder().header("Content-Type", body.contentType().toString())
            .header("Content-Length", body.contentLength().toString())
            .method(request.method, body).build()

        val response = chain.proceed(request)
        if (response.isSuccessful) {
            val newResponse = response.newBuilder()
            var contentType = response.header("Content-Type")
            //if (TextUtils.isEmpty(contentType) || contentType.equals("text/html; charset=UTF-8")) contentType = "application/json"
            val responseStr = response.body!!.string()
            newResponse.body(
                responseStr
                    .toResponseBody("application/json".toMediaTypeOrNull())
            )
            return newResponse.build()
        }
        return response
    }
}