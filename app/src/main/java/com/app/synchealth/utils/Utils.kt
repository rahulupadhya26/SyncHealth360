package com.app.synchealth.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import com.app.synchealth.MainActivity
import com.app.synchealth.services.SyncHealthService
import okhttp3.internal.and
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.net.InetAddress
import java.net.NetworkInterface
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import kotlin.text.Charsets.UTF_8


object Utils {

    const val CONST_LIKE = "Likes"
    const val CONST_COMMENT = "Comments"
    const val CONST_SHARE = "Share"
    const val CONST_SURVEY = "surveys"
    const val CONST_EVENTS = "events"
    const val CONST_DISCUSSIONS = "discussions"
    const val CONST_EXPLORE = "explore"
    const val CONST_FEED = "feed"
    const val CONST_REPLY_IMAGE = "replyimage"
    const val CONST_CHALLENGES = "challenges"
    const val CONST_VIDEOS = "videos"
    const val CONST_CHALLENGE_REPLIES = "challengereplies"
    const val CONST_CHALLENGE = "challenge"
    const val CONST_SPONSOR = "sponsor"
    const val CONST_EXPERT = "expert"
    const val CONST_NOTIFICATIONS = "notifications"
    const val CONST_READ_NOTIFICATIONS = "readnotification"
    const val CONST_LOADING = "loading"
    const val CONST_PROFILE = "profile"
    const val CONST_ARTICLES = "articles"
    const val CONST_FEED_COMMENTS = "feedcomments"
    const val CONST_VIDEO_COMMENTS = "videocomments"
    const val CONST_DISCUSSION_COMMENTS = "discussionresponse"
    const val CONST_DISCUSSION_SPONSOR = "sponsorResponse"
    const val CONST_DISCUSSION_EXPERT = "expertResponse"
    const val CONST_ARTICLE_COMMENTS = "articlecomments"
    const val CONST_FEED_LIKES = "feedlikes"
    const val CONST_VIDEO_LIKES = "videolikes"
    const val CONST_ARTICLE_LIKES = "articlelikes"

    const val CONST_EVENT_RESPONSE = "eventresponse"

    const val CONST_SURVEY_RESPONSE = "surveyresponse"

    const val CONST_UPDATE_PROFILE = "updateProfile"

    const val CONST_QUESTION_TYPE_TEXT = "Text"
    const val CONST_QUESTION_TYPE_TEXT_BOX = "Textbox"
    const val CONST_QUESTION_TYPE_RADIO = "Radio"
    const val CONST_QUESTION_TYPE_CHECKBOX = "Multiselect"
    const val CONST_QUESTION_TYPE_DROPDOWN = "Dropdown"
    const val CONST_QUESTION_TYPE_DATE = "Date"
    const val CONST_QUESTION_TYPE_SCALE = "Scale"
    const val CONST_QUESTION_TYPE_RATING = "Rating"

    const val CONST_EVENT_GOING = "Going"
    const val CONST_EVENT_INTERESTED = "Interested"
    const val CONST_EVENT_CANTGO = "Cant_Go"

    const val CONST_STATUS_ACTION_CHANGE_STATUS = "modifyStatus"
    const val CONST_STATUS_ACTION_RESET_STATUS = "resetStatus"
    const val CONST_STATUS_ACTION_CHANGE_PASSWORD = "changePassword"

    const val SYNC_HEALTH_LOGIN_TLH03 = "QMTBnH3KBDcE4R+sIFSmmg=="
    const val SYNC_HEALTH_LOGIN_TLH04 = "Vc/3vSLVeUHaV29+zzsfPQ=="
    const val SYNC_HEALTH_LOGIN_TLH05 = "stkU52WDpBdF31YE5FDqQg=="
    const val SYNC_HEALTH_LOGIN_TLH06 = "nwjJl7mFhpQvIaQKhq2aHw=="
    const val SYNC_HEALTH_LOGIN_TLH07 = "hJ+Rh05X1NOsXir+bFfvnA=="
    const val SYNC_HEALTH_LOGIN_TLH08 = "oy6sYo7kR5xDaAWg+0dGkQ=="

    const val EVENT_START_APPOINTMENT = 1
    const val EVENT_CANCEL_APPOINTMENT = 2
    const val EVENT_APPOINTMENT_CHAT = 3
    const val EVENT_APPOINTMENT_EMAIL = 4

    const val QUICK_OPTION_GOALS = 1
    const val QUICK_OPTION_ARTICLES = 2
    const val QUICK_OPTION_VIDEOS = 3
    const val QUICK_OPTION_NEWS = 4
    const val QUICK_OPTION_PODCASTS = 5
    const val QUICK_OPTION_JOURNALS = 6
    const val QUICK_OPTION_DISCUSSIONS = 7

    const val CONST_SYMPTOMS_LIST = "Symptoms"
    const val CONST_ALLERGY_LIST = "allergy_issue_list"

    const val SYNC_HEALTH_BASE_URL = "https://demoehr.csardent.com"

    //const val SYNC_HEALTH_BASE_URL = "https://ehr.psyclarity.csardent.com"
    const val SYNC_HEALTH_URL_PART = "/apis/v2/"
    const val SYNC_HEALTH_MASTER_URL = "https://masterehr.csardent.com/tcs/"

    var selectedSymptoms = ""
    var selectedStreet = ""
    var selectedCity = ""
    var selectedState = ""
    var selectedPostalCode = ""
    var selectedCountry = ""
    var selectedPhoneNo = ""
    var selectedCommunicationMode = ""
    var providerId = ""
    var providerType = ""
    var apptProviderType = ""
    var aptScheduleDate = ""
    var aptScheduleTime = ""
    var aptEndTime = "00:30:00"
    var pharmacyVal = ""
    var allergies = ""
    var medication = ""
    var details = ""
    var apptId = ""
    var apptTeleToken = ""
    var apptPcId = ""

    var patientName = ""


    var latitude = 0.0
    var longitude = 0.0
    var tccode = ""
    var authCode = ""
    var firstName = ""
    var lastName = ""
    var gender = ""
    var dob = ""
    var martialStatus = ""
    var ssn = ""
    var occupation = ""
    var motherName = ""
    var cellPhone = ""
    var homePhone = ""
    var email = ""
    var street = ""
    var city = ""
    var state = ""
    var zipcode = ""
    var country = ""
    var ecName = ""
    var ecPhoneNo = ""
    var insuranceName = ""
    var planName = ""
    var subscriber = ""
    var policyNo = ""
    var groupNo = ""
    var insurancePhoto = ""

    const val NAVIGATE_FROM_DASHBOARD = "fromDashboard"
    const val QUICK_BOOK = "quick_book"

    const val SHARE_MOMENT = "share moment"
    const val SHARE_GRATITUDE = "share gratitude"
    const val FEED_LIKE = "feed like"
    const val FEED_COMMENT = "feed comment"
    const val FEED_SHARE = "feed share"
    const val ARTICLE_LIKE = "article like"
    const val ARTICLE_COMMENT = "article comment"
    const val ARTICLE_SHARE = "article share"
    const val VIDEO_LIKE = "video like"
    const val VIDEO_COMMENT = "video comment"
    const val VIDEO_SHARE = "video share"
    const val NAV_EVENT = "navigated to event"
    const val NAV_VIDEO = "navigated to video"
    const val NAV_EXPLORE = "navigated to explore"
    const val NAV_LOGOUT = "navigated to logout"
    const val NAV_CHALLENGE = "navigated to challenge"
    const val NAV_SYNC_HEALTH = "navigated to sync health"
    const val NAV_JOURNALS = "navigated to journals"
    const val NAV_QUOTES = "navigated to quotes"
    const val NAV_SOBRIETY_CLOCK = "navigated to sobriety clock"

    const val NSFW_CONFIDENCE_THRESHOLD = 0.7
    const val LABEL_SFW = "nude"
    const val LABEL_NSFW = "nonnude"

    const val CONST_NO_RECORDS = "TH102"
    const val CONST_SQL_ERROR = "TH1005"
    const val CONST_SUCCESS = "TH200"
    const val CONST_FAIL = "TH201"
    const val CONST_INVALID_TOKEN = "TH207"
    const val CONST_INVALID_REQ = "TH205"
    const val CONST_INVALID_INPUTS = "TH204"
    const val CONST_USR_PASS_INVALID = "TH100"
    const val CONST_PASS_NOT_MATCH = "TH102"
    const val CONST_USER_NOT_FOUND = "TH101"

    const val CONST_1_BID = "Twice a day"
    const val CONST_2_TID = "Three times a day"
    const val CONST_3_QID = "Four times a day"
    const val CONST_4_Q3H = "Every 3 hours"
    const val CONST_5_Q4H = "Every 4 hours"
    const val CONST_6_Q5H = "Every 5 hours"
    const val CONST_7_Q6H = "Every 6 hours"
    const val CONST_8_Q8H = "Every 8 hours"
    const val CONST_9_QD = "Once a day"
    const val CONST_10_AC = "Before meals"
    const val CONST_11_PC = "After meals"
    const val CONST_12_AM = "Morning"
    const val CONST_13_PM = "Evening"
    const val CONST_14_ANTE = "In front of"
    const val CONST_15_H = "H"
    const val CONST_16_HS = "Hours of sleep"
    const val CONST_17_PRN = "When necessary"
    const val CONST_18_STAT = "Shortest turn around time"

    const val CONST_1 = "1"
    const val CONST_2 = "2"
    const val CONST_3 = "3"
    const val CONST_4 = "4"
    const val CONST_5 = "5"
    const val CONST_6 = "6"
    const val CONST_7 = "7"
    const val CONST_8 = "8"
    const val CONST_9 = "9"
    const val CONST_10 = "10"
    const val CONST_11 = "11"
    const val CONST_12 = "12"
    const val CONST_13 = "13"
    const val CONST_14 = "14"
    const val CONST_15 = "15"
    const val CONST_16 = "16"
    const val CONST_17 = "17"
    const val CONST_18 = "18"

    val NSFW_WORDS = listOf(
        "fuck",
        "bitch",
        "ass",
        "bastard",
        "sex",
        "bellend",
        "butt",
        "cunt",
        "shit",
        "dumbass",
        "fucker",
        "witch",
        "bullshit",
        "motherfucker",
        "dick"
    )

    fun viewToImage(view: View): Bitmap? {
        val returnedBitmap =
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }

    fun getDisplayMetrics(activity: MainActivity): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

    fun convertDpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            getDisplayMetrics(context as MainActivity)
        ).toInt()
    }

    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("isMyServiceRunning?", true.toString() + "")
                return true
            }
        }
        Log.i("isMyServiceRunning?", false.toString() + "")
        return false
    }

    //Start background service
    fun startBackGroundService(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, SyncHealthService::class.java))
        } else {
            context.startService(Intent(context, SyncHealthService::class.java))
        }
    }

    fun convert(bitmap: Bitmap): String? {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    /**
     * Convert byte array to hex string
     * @param bytes toConvert
     * @return hexValue
     */
    fun bytesToHex(bytes: ByteArray): String? {
        val sbuf = StringBuilder()
        for (idx in bytes.indices) {
            val intVal: Int = bytes[idx] and 0xff
            if (intVal < 0x10) sbuf.append("0")
            sbuf.append(Integer.toHexString(intVal).toUpperCase())
        }
        return sbuf.toString()
    }

    /**
     * Get utf8 byte array.
     * @param str which to be converted
     * @return  array of NULL if error was found
     */
    fun getUTF8Bytes(str: String): ByteArray? {
        return try {
            str.toByteArray(charset("UTF-8"))
        } catch (ex: Exception) {
            null
        }
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     * @param filename which to be converted to string
     * @return String value of File
     * @throws java.io.IOException if error occurs
     */
    @Throws(IOException::class)
    fun loadFileAsString(filename: String?): String? {
        val BUFLEN = 1024
        val `is` = BufferedInputStream(FileInputStream(filename), BUFLEN)
        return try {
            val baos = ByteArrayOutputStream(BUFLEN)
            val bytes = ByteArray(BUFLEN)
            var isUTF8 = false
            var read: Int
            var count = 0
            while (`is`.read(bytes).also { read = it } != -1) {
                if (count == 0 && bytes[0] == 0xEF.toByte() && bytes[1] == 0xBB.toByte() && bytes[2] == 0xBF.toByte()) {
                    isUTF8 = true
                    baos.write(bytes, 3, read - 3) // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read)
                }
                count += read
            }
            if (isUTF8) String(baos.toByteArray(), Charset.forName("UTF-8")) else String(baos.toByteArray())
        } finally {
            try {
                `is`.close()
            } catch (ignored: Exception) {
            }
        }
    }

    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return  mac address or empty string
     */
    fun getMACAddress(interfaceName: String?): String? {
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                if (interfaceName != null) {
                    if (!intf.name.equals(interfaceName)) continue
                }
                val mac: ByteArray = intf.hardwareAddress ?: return ""
                val buf = StringBuilder()
                for (aMac in mac) buf.append(String.format("%02X:", aMac))
                if (buf.isNotEmpty()) buf.deleteCharAt(buf.length - 1)
                return buf.toString()
            }
        } catch (ignored: Exception) {
        } // for now eat exceptions
        return ""
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4   true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    fun getIPAddress(useIPv4: Boolean): String? {
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr: String = addr.getHostAddress()
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4) return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(
                                    0,
                                    delim
                                ).toUpperCase()
                            }
                        }
                    }
                }
            }
        } catch (ignored: Exception) {
        } // for now eat exceptions
        return ""
    }
}