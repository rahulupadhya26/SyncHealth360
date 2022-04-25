package com.app.synchealth.controller

interface HandleResult {
    fun onSuccess(action:String)
    fun onFailed()
}