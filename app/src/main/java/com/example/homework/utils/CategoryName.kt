package com.example.homework.utils

import android.content.Context
import com.example.homework.R
import com.example.homework.data.MovieCategory


fun getNameCategory(context: Context, category: String): String {
    return when (category) {
        "STILL" -> context.getString(R.string.category_still)
        "SHOOTING" -> context.getString(R.string.category_shooting)
        "POSTER" -> context.getString(R.string.category_poster)
        "FAN_ART" -> context.getString(R.string.category_fan_art)
        "PROMO" -> context.getString(R.string.category_promo)
        "CONCEPT" -> context.getString(R.string.category_concept)
        "WALLPAPER" -> context.getString(R.string.category_wallpaper)
        "COVER" -> context.getString(R.string.category_cover)
        "SCREENSHOT" -> context.getString(R.string.category_screenshot)
        "ACTOR" -> context.getString(R.string.actor)
        "PRODUCER" -> context.getString(R.string.producer)
        "VOICE_DIRECTOR" -> context.getString(R.string.voice_director)
        "TRANSLATOR" -> context.getString(R.string.translator)
        "WRITER" -> context.getString(R.string.writer)
        "OPERATOR" -> context.getString(R.string.operator)
        "DESIGN" -> context.getString(R.string.design)
        "EDITOR" -> context.getString(R.string.editor)
        "DIRECTOR" -> context.getString(R.string.director)
        "HIMSELF" -> context.getString(R.string.himself)
        "HRONO_TITR_MALE" -> context.getString(R.string.hrono_titr_male)
        "HERSELF" -> context.getString(R.string.herself)
        "HRONO_TITR_FEMALE" -> context.getString(R.string.hrono_titr_female)
        "COMPOSER" -> context.getString(R.string.composer)
        "VOICE_FEMALE" -> context.getString(R.string.voice_female)
        "VOICE_MALE" -> context.getString(R.string.voice_male)
        else -> category
    }
}

fun getDisplayName(context: Context, category: String): String {
    return when (category) {
        "viewed" -> context.getString(R.string.viewed)
        "liked" -> context.getString(R.string.liked)
        "wishlist" -> context.getString(R.string.wishlist)
        "interested" -> context.getString(R.string.interested)
        else -> category
    }
}