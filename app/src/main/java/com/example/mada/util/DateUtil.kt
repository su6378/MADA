package com.example.mada.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtil {
    fun getToday(): Int {
        val today = if (Calendar.getInstance()
                .get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
        ) 7 else Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1

        return today - 1
    }

    fun getDateInfo(): List<String> {
        val dateInfo = arrayListOf<String>()
        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("d", Locale.KOREA)
        val today = calendar.get(Calendar.DAY_OF_WEEK)

        if (today == Calendar.SUNDAY) calendar.add(Calendar.DAY_OF_WEEK, -1) // 오늘이 일요일인 경우 시작을 지난주 월요일로 세팅

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val month = calendar.get(Calendar.MONTH) + 1
        dateInfo.add("${month}월")

        for (i in 0 until 7) {
            dateInfo.add(dayFormat.format(calendar.time))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dateInfo
    }
}