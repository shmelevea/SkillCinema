package com.example.homework.presentation.ui.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MovieListItemDecoration(
    private val spacingHorizontal: Int,
    private val bottomSpacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        outRect.left = spacingHorizontal / 2
        outRect.right = spacingHorizontal / 2

        outRect.bottom = bottomSpacing
    }
}