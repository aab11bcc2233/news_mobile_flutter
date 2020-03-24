package com.htphtp.tools.time

import java.util.*

/**
 * @author htp
 */
class TimeDiffUtil private constructor() {
    companion object {
        private const val minute = (60 * 1000).toLong() // 1分钟
        private const val hour = 60 * minute            // 1小时
        private const val day = 24 * hour               // 1天
        private const val month = 31 * day              // 月
        private const val year = 12 * month             // 年

        fun getTimeDiffByCurrentTime(
                date: Date,
                yearUnit: String = "年前",
                monthUnit: String = "个月前",
                dayUnit: String = "天前",
                hourUnit: String = "个小时前",
                minuteUnit: String = "分钟前",
                justNowUnit: String = "刚刚"
        ): String {
            val diff = Date().time - date.time
            var r: Long = 0
            if (diff > year) {
                r = diff / year
                return r.toString() + yearUnit
            }
            if (diff > month) {
                r = diff / month
                return r.toString() + monthUnit
            }
            if (diff > day) {
                r = diff / day
                return r.toString() + dayUnit
            }
            if (diff > hour) {
                r = diff / hour
                return r.toString() + hourUnit
            }
            if (diff > minute) {
                r = diff / minute
                return r.toString() + minuteUnit
            }

            return justNowUnit
        }


        fun getUsedTime(useTime: Long): String {
            if (useTime / year > 0) {
                return "${useTime / year}年${useTime % year / month}月${useTime % year % month / day}日${useTime % year % month % day / hour}小时${useTime % year % month % day % hour / minute}分钟${useTime % year % month % day % hour % minute / 1000}秒"
            } else if (useTime / month > 0) {
                return "${useTime % year / month}月${useTime % year % month / day}日${useTime % year % month % day / hour}小时${useTime % year % month % day % hour / minute}分钟${useTime % year % month % day % hour % minute / 1000}秒"
            } else if (useTime / day > 0) {
                return "${useTime % year % month / day}日${useTime % year % month % day / hour}小时${useTime % year % month % day % hour / minute}分钟${useTime % year % month % day % hour % minute / 1000}秒"
            } else if (useTime / hour > 0) {
                return "${useTime % year % month % day / hour}小时${useTime % year % month % day % hour / minute}分钟${useTime % year % month % day % hour % minute / 1000}秒"
            } else if (useTime / minute > 0) {
                return "${useTime % year % month % day % hour / minute}分钟${useTime % year % month % day % hour % minute / 1000}秒"
            } else if (useTime / 1000 > 0) {
                return "${useTime % year % month % day % hour % minute / 1000}秒"
            }
            return "${useTime}毫秒"
        }

        fun getUsedTimeHours(useTime: Long): String {
            if (useTime / hour > 0) {
                return "${useTime % year % month % day / hour}小时${useTime % year % month % day % hour / minute}分"
            } else if (useTime / minute > 0) {
                return "${useTime % year % month % day % hour / minute}分钟"
            }
            return "${useTime/1000}秒"
        }

    }
}