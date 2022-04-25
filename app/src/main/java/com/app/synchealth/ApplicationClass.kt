package com.app.synchealth

import android.app.Application
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider

class ApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()
        //Install emoji manager
        EmojiManager.install(GoogleEmojiProvider())
    }
}