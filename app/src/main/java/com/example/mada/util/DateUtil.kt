package com.example.mada.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.roundToInt

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

    fun getWeekInfoList(startDate: String, n: Int): List<String> {
        val result = mutableListOf<String>()

        // 입력된 문자열을 Date로 변환
        val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
        val date = sdf.parse(startDate) ?: return emptyList()

        val calendar = Calendar.getInstance(Locale.KOREA).apply {
            time = date
            firstDayOfWeek = Calendar.MONDAY
        }

        // n주 뒤까지 반복
        for (i in 0..n) {
            // i주 뒤 날짜 구하기
            val tempCal = calendar.clone() as Calendar
            tempCal.add(Calendar.WEEK_OF_YEAR, i)

            val month = tempCal.get(Calendar.MONTH) + 1
            val weekOfMonth = tempCal.get(Calendar.WEEK_OF_MONTH)

            result.add("${month}월 ${weekOfMonth}주차")
        }

        return result
    }

    fun getSavingPlan(amount: Int, months: Int): Pair<String,String> {
        val calendar = Calendar.getInstance()
        val startDate = calendar.time

        // 종료일 = 현재 날짜 + n개월
        calendar.add(Calendar.MONTH, months)
        val endDate = calendar.time

        // 주차 수 계산
        val diffMillis = endDate.time - startDate.time
        val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
        val weeks = (diffDays / 7.0).roundToInt().coerceAtLeast(1) // 최소 1주 보장

        // 주 단위 금액 계산 (반올림)
        val weeklyAmount = (amount.toDouble() / weeks).roundToInt()

        // 금액 천 단위 포맷
        val formattedAmount = NumberFormat.getNumberInstance(Locale.KOREA).format(weeklyAmount)

        // 종료일 포맷
        val formatter = SimpleDateFormat("yyyy.M.d", Locale.KOREA)
        val targetDate = formatter.format(endDate)

        return Pair("${targetDate}까지\n주 ${formattedAmount}원\n저축이 필요해요.","주 ${formattedAmount}원")
    }

    /**
     * 오늘 날짜와 month개월 뒤 날짜를 Pair로 반환
     * 예: Pair("2025.09.27", "2025.10.27")
     */
    fun getTodayAndFutureDate(months: Int): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val today = calendar.time

        // n개월 뒤
        calendar.add(Calendar.MONTH, months)
        val futureDate = calendar.time

        // 날짜 포맷
        val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
        val todayStr = formatter.format(today)
        val futureStr = formatter.format(futureDate)

        return Pair(todayStr, futureStr)
    }
}