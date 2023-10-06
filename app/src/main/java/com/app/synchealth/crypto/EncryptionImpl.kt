package com.app.synchealth.crypto

class EncryptionImpl : CryptoStrategy {
    @Throws(Exception::class)
    override fun encrypt(body: String?): String? {
        return CryptoUtil.encrypt(body!!)
    }

    override fun decrypt(data: String?): String? {
        return null
    }
}