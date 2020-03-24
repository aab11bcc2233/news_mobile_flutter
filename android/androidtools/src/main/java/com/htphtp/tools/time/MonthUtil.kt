package com.htphtp.tools.time

import java.util.*

class MonthUtil private constructor() {

    companion object {
        @JvmStatic
        fun getMonths(date: Date? = null): ArrayList<Long> {
            val calendar = Time.getCalendar(date)

            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            calendar.add(Calendar.DATE, -day) // last day of last month

            val months = ArrayList<Long>(days)
            for (i in 1..days) {
                calendar.add(Calendar.DATE, +1)
                months.add(calendar.time.time)
            }


            return months
        }

        fun getMonthInt(date: Date? = null): Int {
            val calendar = Time.getCalendar(date)
            return calendar.get(Calendar.MONTH) + 1
        }

        @JvmStatic
        fun getLastMonth(date: Date? = null): Date {
            val calendar = Time.getCalendar(date)
            calendar.add(Calendar.MONTH, -1)
            return calendar.time
        }

        @JvmStatic
        fun getNextMonth(date: Date? = null): Date {
            val calendar = Time.getCalendar(date)
            calendar.add(Calendar.MONTH, +1)
            return calendar.time
        }
    }
}