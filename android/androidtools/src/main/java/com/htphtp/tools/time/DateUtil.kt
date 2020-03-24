package com.htphtp.tools.time

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by htp on 2018/4/12.
 */
class DateUtil {

    companion object {

        @JvmStatic
        fun getPatternByYearMonthDay(split: String) = "yyyy${split}MM${split}dd"

        @JvmStatic
        fun parseDate(pattern: String, source: String): Calendar {
            val date = if (source.isEmpty()) {
                Date()
            } else {
                SimpleDateFormat(pattern)
                        .parse(source)
            }


            return Calendar.getInstance().apply {
                time = date
            }
        }

        @JvmStatic
        fun dateFormat(pattern: String, date: String): String {
            val d = date.toLongOrNull()

            return if (d != null) {
                dateFormat(pattern, d)
            } else {
                dateFormat(pattern)
            }
        }

        @JvmStatic
        fun dateFormat(pattern: String, date: Long): String {
            if (date == 0L) {
                return ""
            }

            val d = if (date.toString().length == 10) {
                date * 1000L
            } else {
                date
            }

            return dateFormat(pattern, Date(d))
        }

        @JvmStatic
        fun dateFormat(pattern: String = "yyyy-MM-dd", date: Date = Date()): String {
            return SimpleDateFormat(pattern).format(date)
        }


        /**
         * @return yyyy-MM-d日
         */
        @JvmStatic
        fun getFormatDateYarAndMonth(timeInMillis: Long?): String {
            return if (timeInMillis == null || timeInMillis == 0L) "" else SimpleDateFormat("yyyy-MM-dd").format(Date(timeInMillis))
        }

        /*
     * 将时间转换为时间戳
     */
        @Throws(ParseException::class)
        @JvmStatic
        fun dateToStamp(pattern: String, s: String): Long {
            val simpleDateFormat = SimpleDateFormat(pattern)
            val date = simpleDateFormat.parse(s)
            return date.time
        }

        //计算从开始时间到结束时间中间有几天时间
        fun dateDifference(startDate:String,endDate:String):Long{
            if(dateToStamp("yyyy-MM-dd",endDate) - dateToStamp("yyyy-MM-dd",startDate) < 0){
                return 0
            }else{
                return (dateToStamp("yyyy-MM-dd",endDate) - dateToStamp("yyyy-MM-dd",startDate))/1000/60/60/24 + 1
            }
        }

    }


}