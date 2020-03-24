package com.htphtp.tools.time

import kotlin.properties.Delegates

/**
 * Created by htp on 2018/4/11.
 */

class CountDownCalc {


    private var millisecond: Long by Delegates.observable(0L) { _, _, newValue ->
        day = millisecondToDay(newValue)

        var surplusMillis = newValue - dayToMillisecond(day)
        hour = millisecondToHour(surplusMillis)

        surplusMillis -= hourToMillisecond(hour)
        minute = millisecondToMinute(surplusMillis)

        surplusMillis -= minuteToMillisecond(minute)
        second = millisecondToSecond(surplusMillis)
    }

    private var day: Long = 0L
    private var hour: Long = 0L
    private var minute: Long = 0L
    private var second: Long = 0L

    fun calc(millisecond: Long): CountDownCalc {
        this.millisecond = millisecond
        return this
    }

    fun getDay() = day
    fun getHour() = hour
    fun getMinute() = minute
    fun getSecond() = second

    fun getDayFormat() = format(day)
    fun getHourFormat() = format(hour)
    fun getMinuteFormat() = format(minute)
    fun getSecondFormat() = format(second)

    fun toFormat(dayUnit: String = "天", hourUnit: String = "时", minuteUnit: String = "分", secondUnit: String = "秒", isHideSecond: Boolean = false): String {
        val df = getDayFormat() + dayUnit
        val hf = getHourFormat() + hourUnit
        val mf = getMinuteFormat() + minuteUnit
        val sf = if (isHideSecond) "" else getSecondFormat() + secondUnit

        if (day > 0) {
            return df + hf + mf + sf
        }

        if (hour > 0) {
            return hf + mf + sf
        }

        if (minute > 0) {
            return  mf + sf
        }

        if (second > 0) {
            return sf
        }

        return "0"
    }

    companion object {

        @JvmStatic fun millisecondToDay(millisecond: Long) = millisecond / 1000 / 60 / 60 / 24

        @JvmStatic fun millisecondToHour(millisecond: Long) = millisecond / 1000 / 60 / 60

        @JvmStatic fun millisecondToMinute(millisecond: Long) = millisecond / 1000 / 60

        @JvmStatic fun millisecondToSecond(millisecond: Long) = millisecond / 1000


        @JvmStatic fun dayToMillisecond(day: Long) = day * 24 * 60 * 60 * 1000

        @JvmStatic fun hourToMillisecond(hour: Long) = hour * 60 * 60 * 1000

        @JvmStatic fun minuteToMillisecond(minute: Long) = minute * 60 * 1000

        @JvmStatic fun secondToMillisecond(millisecond: Long) = millisecond * 1000

        fun format(time: Long): String {
            val len = time.toString().length

            val reg = "%0" + (if (len < 2) 2 else len) + "d"
            return reg.format(time)
        }


        fun format(vararg timeUnits: Long, separator: String = ":"): String = timeUnits.map {
            val len = it.toString().length

            val reg = "%0" + (if (len < 2) 2 else len) + "d"

            reg.format(it)
        }.toList().joinToString(separator = separator)
    }

}

