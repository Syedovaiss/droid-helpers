package com.ovais.odroidhelper.helpers

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object DateTimeHelper {

    const val DATE_Y_M_D_TIME = "yyyy-MM-dd HH:mm:ssZ"
    fun getFormattedDateTime(epoch: Long, format: String): String {
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        return formatter.format(epoch)
    }

    fun uTCToLocal(dateStr: String, inputDatePattern: String, outputDatePattern: String): String {
        val df = SimpleDateFormat(inputDatePattern, Locale.getDefault())
        df.timeZone = TimeZone.getTimeZone("UTC")
        val date = df.parse(dateStr)
        df.timeZone = TimeZone.getDefault()
        return SimpleDateFormat(outputDatePattern, Locale.getDefault()).format(date!!)
    }

    @JvmStatic
    fun addDaysToCurrent(count: Int): String {

        val formattedDate = SimpleDateFormat(DATE_D_M_Y_SLASH_SEPARATED, Locale.getDefault())
        val c = Calendar.getInstance()
        c.add(Calendar.DATE, count)  // number of days to add
        val tomorrow = formattedDate.format(c.time)
        return tomorrow
    }


    fun getCurrentDateWithTime(): String {
        val cal = Calendar.getInstance()
        val format = SimpleDateFormat(DATE_Y_M_D_TIME, Locale.getDefault())
        return format.format(cal.time)
    }

    fun getCurrentDate(): String {
        val cal = Calendar.getInstance()
        val format = SimpleDateFormat(DATE_Y_M_D, Locale.getDefault())
        return format.format(cal.time)
    }


    fun isValidStartDate(startDate: Date?, endDate: Date?, context: Context): Boolean {
        return endDate == null || startDate!!.before(endDate)
    }

    fun isValidEndDate(startDate: Date?, endDate: Date?, context: Context): Boolean {
        return startDate == null || endDate!!.after(startDate)
    }


    private fun isValidStartEndDate(startDate: Date?, endDate: Date?): Boolean {
        return (startDate != null && endDate != null && startDate.before(endDate))
    }


    @JvmStatic
    fun getInBetweenDaysFromTwoDates(start: Long, end: Long): Int {
        var days = 1
        val currentDate = Calendar.getInstance().apply { timeInMillis = start }

        while (!isSameDay(currentDate.timeInMillis, end)) {
            days++
            currentDate.add(Calendar.DAY_OF_YEAR, 1)
        }

        return days
    }

    @JvmStatic
    fun get24HourFormat(hours: Int): String {
        val formattedDate = SimpleDateFormat(TIME_HH_24_HOUR_FORMAT, Locale.getDefault());
        val calender = Calendar.getInstance()
        calender.time = Date()
        calender.set(Calendar.HOUR_OF_DAY, hours)
        return formattedDate.format(calender.time)
    }

    @JvmStatic
    fun get12HoursFrom24Hours(hoursIn24: Int): String {
        val time = getTimeFromHours(hoursIn24)
        return getFormattedDateTime(time, TIME_H_12_HOUR_FORMAT)
    }

    @JvmStatic
    fun toDayBefore(timeInMillSec: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillSec
        calendar.add(Calendar.DATE, -1)
        return calendar.timeInMillis
    }

    @JvmStatic
    fun getFormattedDateTimeBasedOnPhoneLocal(epoch: Long, format: String, locale: String): String {
        val formatter = SimpleDateFormat(format, Locale(locale))
        return formatter.format(epoch)
    }

    @JvmStatic
    fun isToday(epoch: Long): Boolean {
        return isSameDay(epoch, Date().time)
    }

    @JvmStatic
    fun isSameDay(epoch1: Long, epoch2: Long): Boolean {
        val cal1 = Calendar.getInstance()
        cal1.time = Date(epoch1)
        val cal2 = Calendar.getInstance()
        cal2.time = Date(epoch2)

        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    @JvmStatic
    fun isCurrentTimeInTimeSlot(hour1: Int, hour2: Int): Boolean {
        val time1 = getTimeFromHours(hour1)
        val time2 = getTimeFromHours(hour2)
        val currentTime = System.currentTimeMillis()
        return (currentTime >= time1) && (currentTime <= time2)
    }

    @JvmStatic
    fun addDaysInDate(epoch1: Long, days: Int): Long {
        val calender = Calendar.getInstance()
        calender.time = Date(epoch1)
        calender.add(Calendar.DAY_OF_YEAR, days)
        return calender.time.time
    }

    @JvmStatic
    fun addHoursInTime(epoch1: Long, hours: Int): Long {
        val calender = Calendar.getInstance()
        calender.time = Date(epoch1)
        calender.timeZone = TimeZone.getTimeZone(TimeZone.getDefault().id)
        calender.add(Calendar.HOUR_OF_DAY, hours)
        return calender.time.time
    }

    @JvmStatic
    fun addMinutesInTime(epoch1: Long, minutes: Int): Long {
        val calender = Calendar.getInstance()
        calender.time = Date(epoch1)
        calender.timeZone = TimeZone.getTimeZone(TimeZone.getDefault().id)
        calender.add(Calendar.MINUTE, minutes)
        return calender.time.time
    }

    @JvmStatic
    fun getTimeFromDateAndHours(epoch: Long, hours: Int): Long {
        val calender = Calendar.getInstance()
        calender.time = Date(epoch)
        calender.timeZone = TimeZone.getTimeZone(TimeZone.getDefault().id)
        calender.set(Calendar.HOUR_OF_DAY, hours)
        calender.set(Calendar.MINUTE, 0)
        calender.set(Calendar.SECOND, 0)
        return calender.time.time
    }

    @JvmStatic
    fun getTimeFromHours(hours: Int): Long {
        val calender = Calendar.getInstance()
        calender.time = Date()
        calender.timeZone = TimeZone.getTimeZone(TimeZone.getDefault().id)
        calender.set(Calendar.HOUR_OF_DAY, hours)
        return calender.time.time
    }

    @JvmStatic
    fun mergeDateTIme(date: Long, hour: Int): Long {
        val calender = Calendar.getInstance()
        calender.time = Date(date)
        calender.timeZone = TimeZone.getTimeZone(TimeZone.getDefault().id)
        calender.set(Calendar.HOUR_OF_DAY, hour)
        calender.set(Calendar.MINUTE, 0)
        calender.set(Calendar.SECOND, 0)
        return calender.time.time
    }

    @JvmStatic
    fun getDayOfWeekInString(dayOfWeek: Int, format: String): String {
        val calender = Calendar.getInstance()
        calender.set(Calendar.DAY_OF_WEEK, dayOfWeek)
        return getFormattedDateTimeBasedOnPhoneLocal(
            calender.time.time,
            format,
            Locale.getDefault().language
        )
    }


    @JvmStatic
    fun getDate(year: Int, month: Int, dayOfMonth: Int): Date {
        val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = year
        cal[Calendar.MONTH] = month - 1
        cal[Calendar.DAY_OF_MONTH] = dayOfMonth

        return cal.time
    }


    // Available formats
    const val DATE_TIME_ORDER_FORMAT = "dd MMM EEEE, hh:mm a"
    const val DATE_TIME_FORMAT_D_M_D_H = "EEE MMM dd, hh a"
    const val DATE_TIME_Y_M_D_24_HOUR_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val DATE_TIME_D_M_Y_12_HOUR_FORMAT_WITH_MS = "dd-MMM-yyyy hh:mm:ss.S z"
    const val DATE_TIME_Y_M_D_WITHOUT_MS = "yyyy-MM-dd hh:mm:ss z"
    const val DATE_TIME_M_D_Y_FULL_MONTH_NAME_WITH_TIME = "MMMM dd, yyyy, hh:mm a"
    const val DATE_TIME_M_D_Y_FULL_MONTH_NAME = "MMMM dd, yyyy"
    const val DATE_D_M_Y_ABREV = "dd-MMM-yy"
    const val DATE_D_M_Y_DIGIT = "dd-MM-yy"
    const val DATE_D_M_Y_SLASH_SEPARATED = "dd/MM/yyyy"
    const val DATE_D_M_Y_DOT_SEPARATED = "dd.MM.yyyy"
    const val DATE_Y_M_D_SLASH_SEPARATED = "yyyy/MM/dd"
    const val DATE_Y_M_D = "yyyy-MM-dd"
    const val DATE_M_Y = "MMM yyyy"
    const val DATE_TIME_M_D_WEEK = "EEE MMM dd kk:mm:ss z yyyy"
    const val TIME_M_S = "mm:ss"
    const val TIME_H_M_S_12_HOUR_FORMAT = "hh:mm:ss"
    const val TIME_H_M_S_24_HOUR_FORMAT = "HH:mm:ss"
    const val TIME_H_M = "hh:mm"
    const val TIME_H_M_S_MS = "hh:mm:ss.S"
    const val TIME_H_M_24_HOUR_FORMAT = "HH:mm"
    const val TIME_H_M_A_12_HOUR_FORMAT = "hh:mm a"
    const val TIME_H_12_HOUR_FORMAT = "hh a"
    const val DATE_M_ABREV_DAY = "MMM dd"
    const val DATE_M_ABREV = "MMM"
    const val DATE_M_DIGIT = "MM"
    const val DATE_E = "EEEE"
    const val DATE_E2 = "EEE"
    const val DATE_Y = "yyyy"
    const val DATE_D = "dd"
    const val TIME_Z = "z"
    private const val TIME_HH_24_HOUR_FORMAT = "HH"
    const val DATE_D_M_YYY = "dd MMM, yyyy"

}