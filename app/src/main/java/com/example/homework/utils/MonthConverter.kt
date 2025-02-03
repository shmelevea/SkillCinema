package com.example.homework.utils

object MonthConverter {
    private val monthToString = mapOf(
        1 to "JANUARY",
        2 to "FEBRUARY",
        3 to "MARCH",
        4 to "APRIL",
        5 to "MAY",
        6 to "JUNE",
        7 to "JULY",
        8 to "AUGUST",
        9 to "SEPTEMBER",
        10 to "OCTOBER",
        11 to "NOVEMBER",
        12 to "DECEMBER"
    )

    fun monthToString(month: Int): String {
        return monthToString[month] ?: throw IllegalArgumentException("Invalid month: $month")
    }
}