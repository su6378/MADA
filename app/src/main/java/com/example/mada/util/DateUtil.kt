package com.example.mada.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
import kotlin.math.ceil

private const val TAG = "DX"
object DateUtil {
    fun getToday(): Int {
        val today = if (Calendar.getInstance()
                .get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
        ) 7 else Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1

        return today - 1
    }

    fun getDateInfo(weekOffset: Int = 0): List<String> {
        val dateInfo = arrayListOf<String>()
        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("d", Locale.KOREA)

        // 주 시작을 월요일로 맞추기
        calendar.firstDayOfWeek = Calendar.MONDAY
        val today = calendar.get(Calendar.DAY_OF_WEEK)

        if (today == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, -1) // 일요일이면 지난주 월요일로 세팅
        }

        // 이번 주 월요일로 이동
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        // weekOffset 적용 (지난주/다음주 이동)
        calendar.add(Calendar.WEEK_OF_YEAR, weekOffset)

        // 월요일 기준 월/주차 계산
        val mondayMonth = calendar.get(Calendar.MONTH) + 1
        val weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)

        // 첫 번째: 월 (월요일 달 기준)
        dateInfo.add("${mondayMonth}월")

        // 월요일부터 일요일까지 날짜 추가
        for (i in 0 until 7) {
            dateInfo.add(dayFormat.format(calendar.time))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // 마지막: 주차 (월요일 달 기준)
        dateInfo.add("${mondayMonth}월 ${weekOfMonth}주차")

        return dateInfo
    }

    fun getWeeksInMonth(year: Int, month: Int): Pair<List<String>, Int> {
        val sdf = SimpleDateFormat("M월 d일", Locale.KOREA)
        val weekList = mutableListOf<String>()

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)

        // 월요일 기준 주 시작
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.minimalDaysInFirstWeek = 1

        // 첫 주의 월요일로 이동 (필요하면 이전 달 포함)
        val firstWeekMonday = cal.clone() as Calendar
        firstWeekMonday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        if (firstWeekMonday.after(cal)) {
            // 1일이 월요일이면 안 움직이고, 아니면 이전 달로 이동
            firstWeekMonday.add(Calendar.DATE, -7)
        }

        val currentWeekStart = firstWeekMonday.clone() as Calendar
        val today = Calendar.getInstance().time
        var currentIndex = -1
        var index = 0

        while (true) {
            val weekStart = currentWeekStart.clone() as Calendar
            val weekEnd = currentWeekStart.clone() as Calendar
            weekEnd.add(Calendar.DATE, 6) // 일요일까지

            val weekStr = "${sdf.format(weekStart.time)} ~ ${sdf.format(weekEnd.time)}"
            weekList.add(weekStr)

            // 오늘 날짜가 포함된 주면 인덱스 저장
            if (today >= weekStart.time && today <= weekEnd.time) {
                currentIndex = index
            }

            index++

            // 다음 주로 이동
            currentWeekStart.add(Calendar.DATE, 7)

            // 종료 조건: 현재 주 월요일이 다음 달이면 종료
            if (currentWeekStart.get(Calendar.MONTH) > month - 1 && currentWeekStart.get(Calendar.YEAR) >= year) {
                break
            }
        }

        return Pair(weekList, currentIndex)
    }

    fun getWeekRange(weekOffset: Int = 0): List<String> {
        val sdf = SimpleDateFormat("M월 d일", Locale.KOREA)

        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY

        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            cal.add(Calendar.DATE, -1)
        }

        // 오늘이 속한 주의 월요일로 이동
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val result = mutableListOf<String>()

        for (i in 0..weekOffset) {
            // 주 시작(월요일)
            val weekStart = cal.clone() as Calendar

            // 주 끝(일요일)
            val weekEnd = cal.clone() as Calendar
            weekEnd.add(Calendar.DATE, 6)

            result.add("${sdf.format(weekStart.time)} ~ ${sdf.format(weekEnd.time)}")

            // 다음 주로 이동
            cal.add(Calendar.WEEK_OF_YEAR, 1)
        }

        return result
    }

    fun getSavingAmountPerMonth(goalAmount: Int, targetMillis: Long): Int {
        val today = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = targetMillis }

        // 개월 수 계산
        val months = (target.get(Calendar.YEAR) - today.get(Calendar.YEAR)) * 12 +
                (target.get(Calendar.MONTH) - today.get(Calendar.MONTH)) + 1 // 현재 달 포함

        return ceil(goalAmount.toDouble() / months).toInt()
    }

    fun getCurrentWeekInfo(): String {
        val calendar = Calendar.getInstance(Locale.KOREA)

        // 주 시작 요일을 월요일로 설정
        calendar.firstDayOfWeek = Calendar.MONDAY

        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH는 0부터 시작
        val weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)

        return "${month}월 ${weekOfMonth}주차"
    }
}