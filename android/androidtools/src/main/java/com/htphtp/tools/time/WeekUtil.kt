package com.htphtp.tools.time

import java.util.*

enum class DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    fun getValue() = ordinal + 1
}

class WeekUtil private constructor() {
    companion object {
        @JvmStatic
        fun getWeeks(date: Date? = null, startDayOfWeek: Int = Calendar.MONDAY): List<Long> {
            val calendar = getFirstDayOfWeek(date, startDayOfWeek)

            val weeks = ArrayList<Long>(DAYS_A_WEEK)
            for (i in 1 .. DAYS_A_WEEK) {
                if (i > 1) {
                    calendar.add(Calendar.DATE, +1)
                }
                weeks.add(calendar.time.time)
            }

            return weeks.toList()
        }

        @JvmStatic
        fun getFirstDayOfWeek(date: Date?, startDayOfWeek: Int = Calendar.MONDAY): Calendar {
            val calendar = Time.getCalendar(date)
            calendar.set(Calendar.DAY_OF_WEEK, startDayOfWeek)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            return calendar
        }

        // 下面获取上周，下周的方法，是从 周一开始算；
        // 由于 getWeeks() 方法添加了 一周的起始日期，下面的方法不再适用
//        @JvmStatic
//        fun getLastWeeks(weeks: List<Long>): List<Long> {
//            return getLastOrNextWeeks(weeks, true)
//        }
//
//        @JvmStatic
//        fun getNextWeeks(weeks: List<Long>): List<Long> {
//            return getLastOrNextWeeks(weeks, false)
//        }
//
//        @JvmStatic
//        private fun getLastOrNextWeeks(weeks: List<Long>, isLastWeek: Boolean): List<Long> {
//            if (weeks.size != DAYS_A_WEEK) {
//                throw IllegalArgumentException("seven days a week")
//            }
//
//            val isNotMonday = getDayOfWeekChinese(Date(weeks.first())) != MONDAY
//            if (isNotMonday) {
//                throw IllegalArgumentException("\"weeks\" first value must be Monday")
//            }
//
//            val isNotSunday = getDayOfWeekChinese(Date(weeks.last())) != SUNDAY
//            if (isNotSunday) {
//                throw IllegalArgumentException("\"weeks\" last value must be Sunday")
//            }
//
//            val oneDayMillisecond = 24L * 60L * 60L * 1000L
//
//            val date = if (isLastWeek) {
//                Date(weeks.first() - oneDayMillisecond)
//            } else {
//                Date(weeks.last() + oneDayMillisecond)
//            }
//
//            return getWeeks(date)
//
//        }

        @JvmStatic
        fun getDayOfWeekChinese(date: Date? = null): Int {
            val calendar = Calendar.getInstance()
            if (date != null) {
                calendar.time = date
            }
            val week = calendar.get(Calendar.DAY_OF_WEEK)
            return toChineseDayOfWeek(week)
        }

        @JvmStatic
        fun toChineseDayOfWeek(dayOfWeekByUS: Int): Int {

            if (dayOfWeekByUS !in Calendar.SUNDAY..Calendar.SATURDAY) {
                throw IllegalArgumentException("\"dayOfWeekByUS\" must be 1 to 7")
            }

            val w = dayOfWeekByUS - 1
            val isSunday = w == 0

            return if (isSunday) DayOfWeek.SUNDAY.getValue() else w
        }

        const val DAYS_A_WEEK = 7

//        const val MONDAY = 1
//        const val TUESDAY = 2
//        const val WEDNESDAY = 3
//        const val THURSDAY = 4
//        const val FRIDAY = 5
//        const val SATURDAY = 6
//        const val SUNDAY = 7

    }
}