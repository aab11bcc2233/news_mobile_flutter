package com.htphtp.tools.time

import java.util.*

class Time(date: Date? = null) {

    var date: Date = date ?: Date()
        set(value) {
            field = value
            calendar.time = field
        }

    private val calendar = getCalendar(date)

    val year: Int
        get() = calendar.get(Calendar.YEAR)
    val month: Int
        get() = MonthUtil.getMonthInt(date)
    val day: Int
        get() = calendar.get(Calendar.DATE)
    val hour: Int
        get() = calendar.get(Calendar.HOUR_OF_DAY)
    val minute: Int
        get() = calendar.get(Calendar.MINUTE)

    fun getWeekOfMonth(startDayOfWeek: Int = Calendar.MONDAY): Int {
        calendar.firstDayOfWeek = startDayOfWeek
        return calendar.get(Calendar.WEEK_OF_MONTH)
    }

    fun getWeekOfYear(startDayOfWeek: Int = Calendar.MONDAY): Int {
        calendar.firstDayOfWeek = startDayOfWeek
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }

    val dayOfWeekChinese: Int
        get() = WeekUtil.getDayOfWeekChinese(date)

    fun getDayOfWeekString() = dayOfWeekStr(dayOfWeekChinese)

    fun isSameDate(timestamp: Long) = isSameDate(Date(timestamp))

    fun isSameDate(date: Date): Boolean {
        val time = Time(date)
        return year == time.year
                && month == time.month
                && day == time.day
    }

    companion object {

        @JvmStatic
        private fun dayOfWeekStr(dayOfWeekChinese: Int): String {
            val map = mapOf<Int, String>(
                    1 to "一",
                    2 to "二",
                    3 to "三",
                    4 to "四",
                    5 to "五",
                    6 to "六"
            )
            return "%s".format(
                    if (dayOfWeekChinese == 7) {
                        "日"
                    } else {
                        map[dayOfWeekChinese]
                    }
            )
        }

        @JvmStatic
        fun getCalendar(date: Date? = null): Calendar {
            return Calendar.getInstance().apply {
                if (date != null) {
                    time = date
                }
            }
        }
    }
}