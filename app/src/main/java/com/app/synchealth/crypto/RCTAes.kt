package com.app.synchealth.crypto

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import okhttp3.internal.and
import org.spongycastle.crypto.digests.SHA512Digest
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator
import org.spongycastle.crypto.params.KeyParameter
import org.spongycastle.util.encoders.Hex
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64;


class RCTAes(reactContext: ReactApplicationContext?) : ReactContextBaseJavaModule(reactContext) {
    private var key = "SYNChealth360App2020_*&^%TYUIy2k"
    override fun getName(): String {
        return "RCTAes"
    }

    @ReactMethod
    fun encryptString(data: String?): String {
        return encrypt(data, sha256(key), null).toString()
    }

    @ReactMethod
    fun decryptString(data: String?): String {
        return decrypt(data, sha256(key), null).toString()
    }

    @ReactMethod
    fun pbkdf2(pwd: String, salt: String, cost: Int, length: Int, promise: Promise) {
        try {
            val strs = pbkdf2(pwd, salt, cost, length)
            promise.resolve(strs)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    @ReactMethod
    fun hmac256(data: String, pwd: String, promise: Promise) {
        try {
            val strs = hmacX(data, pwd, HMAC_SHA_256)
            promise.resolve(strs)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    @ReactMethod
    fun hmac512(data: String, pwd: String, promise: Promise) {
        try {
            val strs = hmacX(data, pwd, HMAC_SHA_512)
            promise.resolve(strs)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    @ReactMethod
    fun sha256(data: String): String {
        return shaX(data, "SHA-256")
    }

    @ReactMethod
    fun sha1(data: String, promise: Promise) {
        try {
            val result = shaX(data, "SHA-1")
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    @ReactMethod
    fun sha512(data: String, promise: Promise) {
        try {
            val result = shaX(data, "SHA-512")
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    @ReactMethod
    fun randomUuid(promise: Promise) {
        try {
            val result: String = UUID.randomUUID().toString()
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    @ReactMethod
    fun randomKey(length: Int?, promise: Promise) {
        try {
            val key = ByteArray(length!!)
            val rand = SecureRandom()
            rand.nextBytes(key)
            val keyHex = bytesToHex(key)
            promise.resolve(keyHex)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    @Throws(Exception::class)
    private fun shaX(data: String, algorithm: String): String {
        val md: MessageDigest = MessageDigest.getInstance(algorithm)
        md.update(data.toByteArray())
        val digest: ByteArray = md.digest()
        return bytesToHex(digest)
    }

    companion object {
        private const val CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding"
        const val HMAC_SHA_256 = "HmacSHA256"
        const val HMAC_SHA_512 = "HmacSHA512"
        private const val KEY_ALGORITHM = "AES"
        fun bytesToHex(bytes: ByteArray): String {
            val hexArray = "0123456789abcdef".toCharArray()
            val hexChars = CharArray(bytes.size * 2)
            for (j in bytes.indices) {
                val v: Int = bytes[j] and 0xFF
                hexChars[j * 2] = hexArray[v ushr 4]
                hexChars[j * 2 + 1] = hexArray[v and 0x0F]
            }
            return String(hexChars)
        }

        @Throws(
            NoSuchAlgorithmException::class,
            InvalidKeySpecException::class,
            UnsupportedEncodingException::class
        )
        private fun pbkdf2(pwd: String, salt: String, cost: Int, length: Int): String {
            val gen = PKCS5S2ParametersGenerator(SHA512Digest())
            gen.init(pwd.toByteArray(charset("UTF_8")), salt.toByteArray(charset("UTF_8")), cost)
            val key = (gen.generateDerivedParameters(length) as KeyParameter).key
            return bytesToHex(key)
        }

        @Throws(
            NoSuchAlgorithmException::class,
            InvalidKeyException::class,
            UnsupportedEncodingException::class
        )
        private fun hmacX(text: String, key: String, algorithm: String): String {
            val contentData = text.toByteArray(charset("UTF_8"))
            val akHexData: ByteArray = Hex.decode(key)
            val sha_HMAC: Mac = Mac.getInstance(algorithm)
            val secret_key: SecretKey = SecretKeySpec(akHexData, algorithm)
            sha_HMAC.init(secret_key)
            return bytesToHex(sha_HMAC.doFinal(contentData))
        }

        val emptyIvSpec: IvParameterSpec = IvParameterSpec(
            byteArrayOf(
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00
            )
        )

        @Throws(Exception::class)
        private fun encrypt(text: String?, hexKey: String, hexIv: String?): String? {
            if (text == null || text.length == 0) {
                return null
            }
            val key: ByteArray = Hex.decode(hexKey)
            val secretKey: SecretKey = SecretKeySpec(key, KEY_ALGORITHM)
            val cipher: Cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            cipher.init(
                Cipher.ENCRYPT_MODE,
                secretKey,
                if (hexIv == null) emptyIvSpec else IvParameterSpec(Hex.decode(hexIv))
            )
            val encrypted: ByteArray = cipher.doFinal(text.toByteArray(charset("UTF-8")))
            return Base64.encodeToString(encrypted, Base64.NO_WRAP)
        }

        @Throws(Exception::class)
        private fun decrypt(ciphertext: String?, hexKey: String, hexIv: String?): String? {
            if (ciphertext == null || ciphertext.length == 0) {
                return null
            }
            val key: ByteArray = Hex.decode(hexKey)
            val secretKey: SecretKey = SecretKeySpec(key, KEY_ALGORITHM)
            val cipher: Cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            cipher.init(
                Cipher.DECRYPT_MODE,
                secretKey,
                if (hexIv == null) emptyIvSpec else IvParameterSpec(Hex.decode(hexIv))
            )
            val decrypted: ByteArray = cipher.doFinal(Base64.decode(ciphertext, Base64.NO_WRAP))
            return String(decrypted, charset("UTF-8"))
        }
    }
}