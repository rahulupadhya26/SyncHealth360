package com.app.synchealth.utils

import java.util.*

class DaysUtils(days:Int) {
    private var elapsedMonths:Long? = 0
    private var elapsedDays:Long? = 0

    init {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE,-(days))
        var different = Calendar.getInstance().timeInMillis - calendar.timeInMillis

        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        val mothsInMilli = daysInMilli * 30
        val yearInMilli = mothsInMilli * 12

        different %= yearInMilli

        elapsedMonths = different / mothsInMilli
        different %= mothsInMilli

        elapsedDays = different / daysInMilli
        different %= daysInMilli
    }

    fun getMonths():Int
    {
        return elapsedMonths!!.toInt()
    }

    fun getDays():Int
    {
        return elapsedDays!!.toInt()
    }

}