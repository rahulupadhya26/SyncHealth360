
package com.app.synchealth.utils

import java.text.SimpleDateFormat
import java.util.*


class DateUtils(date: String?) {
    var mDate:Date? = null

    init {
        mDate = SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.getDefault()).parse(date!!)
    }

    fun getFormattedDate():String
    {
        return SimpleDateFormat("dd MMM, yyyy",Locale.getDefault()).format(mDate!!.time)
    }

    fun getDay():String
    {
        return SimpleDateFormat("dd",Locale.getDefault()).format(mDate!!.time)
    }

    fun getMonthYear():String
    {
        return SimpleDateFormat("MMM , yyyy",Locale.getDefault()).format(mDate!!.time)
    }

    fun getTime():String
    {
        return SimpleDateFormat("h:mm a",Locale.getDefault()).format(mDate!!.time)
    }
}