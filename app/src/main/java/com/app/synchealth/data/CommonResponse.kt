package com.app.synchealth.data


import com.app.synchealth.data.*
import com.google.gson.annotations.SerializedName

data class SimpleResponse(
    @SerializedName("data") val userData: UserData? = null,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("title") val title: String
)
