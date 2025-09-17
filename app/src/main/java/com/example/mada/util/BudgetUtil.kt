package com.example.mada.util

import java.util.Calendar
import kotlin.random.Random

object BudgetUtil {
    val expenditure = intArrayOf(10000, 12000, 25000, 13000, 35000, 60000, 45000)

    // 마이데이터 자산 연동
    fun getRandomMoney(): IntArray {
        val myDataBudget = IntArray(7)
        var min = 10000
        var max = 40000
        val step = 1000
        val rangeSize = (max - min) / step + 1

        for (i in 0..3) { // 월요일 ~ 목요일
            myDataBudget[i] = min + (Random.nextInt(rangeSize) * step)
        }

        min = 30000
        max = 80000

        for (i in 4..6) {
            myDataBudget[i] = min + (Random.nextInt(rangeSize) * step)
        }

        return myDataBudget
    }
}