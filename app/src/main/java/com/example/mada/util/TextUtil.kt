package com.example.mada.util

import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale

object TextUtil {
    // 확장 함수: 여러 구간(IntRange)에 색상 적용
    fun TextView.setColoredRanges(
        fullText: CharSequence,
        ranges: List<IntRange>,
        @androidx.annotation.ColorRes colorRes: Int
    ) {
        val color = androidx.core.content.ContextCompat.getColor(context, colorRes)
        val span = android.text.SpannableString(fullText)
        ranges.forEach { r ->
            val safeStart = r.first.coerceIn(0, fullText.length)
            val safeEnd   = (r.last + 1).coerceIn(0, fullText.length)
            if (safeStart < safeEnd) {
                span.setSpan(
                    android.text.style.ForegroundColorSpan(color),
                    safeStart,
                    safeEnd,
                    android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        text = span
    }

    // 확장 함수: 주어진 키워드 목록을 모두 찾아 색 적용 (대/소문자 무시 옵션)
    fun TextView.setColoredSubstrings(
        fullText: CharSequence,
        keywords: List<String>,
        @androidx.annotation.ColorRes colorRes: Int,
        ignoreCase: Boolean = true
    ) {
        val color = androidx.core.content.ContextCompat.getColor(context, colorRes)
        val spannable = android.text.SpannableString(fullText)

        keywords.filter { it.isNotEmpty() }.forEach { key ->
            var start = fullText.indexOf(key, startIndex = 0, ignoreCase = ignoreCase)
            while (start >= 0) {
                val end = start + key.length
                spannable.setSpan(
                    android.text.style.ForegroundColorSpan(color),
                    start,
                    end,
                    android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                start = fullText.indexOf(key, startIndex = end, ignoreCase = ignoreCase)
            }
        }
        text = spannable
    }

    fun Int.toWon(): String  = String.format(Locale.KOREA, "%,d원", this)

    fun getEditTextValueAsInt(editText: TextInputEditText): Int {
        val text = editText.text?.toString() ?: ""
        // "원" 제거 후 공백도 trim
        val cleaned = text.replace("원", "").replace(",","").trim()
        // 빈 문자열이면 0 반환, 아니면 Int 변환
        return if (cleaned.isEmpty()) 0 else cleaned.toInt()
    }
}