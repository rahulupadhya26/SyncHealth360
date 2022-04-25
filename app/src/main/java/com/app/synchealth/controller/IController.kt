package com.app.synchealth.controller

import android.graphics.Bitmap
import android.view.View
import android.widget.TextView
import com.app.synchealth.data.UserData

interface IController {
    fun getHeader(): View
    fun getBackButton(): View
    fun swipeSliderEnable(flag: Boolean)
    fun getSubTitle(): TextView
    fun showProgress()
    fun hideProgress()
    fun captureImage()
    fun uploadPicture()
    fun uploadVideo()
    fun clearTempFormData()
    fun getPhotoPath(): String
    fun getUserInfo(): UserData
    fun getBitmapList(): ArrayList<String>
    fun setBottomMenu(id: Int)
    fun getTokenSavedStatus(): Boolean
    fun syncHealthGetConfigResponse(): String
    fun hideKeyboard(view: View)
    fun syncHealthGetPatientId(): String
    fun syncHealthGetToken(): String
    fun profileInfo(): String
    fun isLoggedIn(): Boolean
    fun clearCache()
    fun sendEventLog(key: String, value: String)
    fun detectImage(bitmap: Bitmap, confidenceThreshold: Float): Boolean
}