package com.example.homework.utils

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.example.homework.R
import java.text.SimpleDateFormat
import java.util.Locale


fun trimText(text: String, maxLength: Int): String {
    return if (text.length > maxLength) {
        text.substring(0, maxLength) + "..."
    } else {
        text
    }
}

fun formatFilmLength(lengthInMinutes: Int, context: Context): String {
    val hours = lengthInMinutes / 60
    val minutes = lengthInMinutes % 60

    return if (hours > 0) {
        "${hours}${context.getString(R.string.hours_label)} ${minutes}${context.getString(R.string.minutes_label)}"
    } else {
        "$minutes ${context.getString(R.string.minutes_label)}"
    }
}

fun formatRatingAgeLimits(rating: String?): String {
    return rating?.replace("age", "")?.plus("+") ?: ""
}

fun formatReleaseDate(date: String): String {
    return try {
        val sourceFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val targetFormat = SimpleDateFormat("d MMMM yyyy", Locale("ru"))
        val parsedDate = sourceFormat.parse(date)
        parsedDate?.let { targetFormat.format(it) } ?: "Unknown date"
    } catch (e: Exception) {
        "Unknown date"
    }
}

fun getStyledText(context: Context, category: String, count: Int): SpannableString {
    val text = "$category $count"
    val spannable = SpannableString(text)
    val color = ContextCompat.getColor(context, R.color.gray_gag)
    val fontSizeSp = 14

    spannable.setSpan(
        ForegroundColorSpan(color),
        category.length + 1,
        text.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    spannable.setSpan(
        AbsoluteSizeSpan(fontSizeSp, true),
        category.length + 1,
        text.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    return spannable
}