package com.example.homework.presentation.ui.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacingHorizontal: Int,
    private val spacingVertical: Int,
    private val orientation: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val totalItemCount = state.itemCount

        if (orientation == GridLayoutManager.VERTICAL) {
            if (position % spanCount == 0) {
                outRect.right = 0
            } else {
                outRect.right = spacingHorizontal
            }

            if (position >= totalItemCount - spanCount) {
                outRect.bottom = 0
            } else {
                outRect.bottom = spacingVertical
            }
        } else if (orientation == GridLayoutManager.HORIZONTAL) {
            if (position % spanCount == 0) {
                outRect.bottom = 0
            } else {
                outRect.bottom = spacingVertical
            }

            if (position >= totalItemCount - spanCount) {
                outRect.right = 0
            } else {
                outRect.right = spacingHorizontal
            }
        }
    }
}