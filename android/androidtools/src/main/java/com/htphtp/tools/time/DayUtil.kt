package com.htphtp.tools.time

import java.util.*

class DayUtil private constructor() {

    companion object {

        @JvmStatic
        fun getLastDay(date: Date? = null): Date {
            val calendar = Time.getCalendar(date)
            calendar.add(Calendar.DATE, -1)
            return calendar.time
        }

        @JvmStatic
        fun getNextDay(date: Date? = null): Date {
            val calendar = Time.getCalendar(date)
            calendar.add(Calendar.DATE, +1)
            return calendar.time
        }
    }
}