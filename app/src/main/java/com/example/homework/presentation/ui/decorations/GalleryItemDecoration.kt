package com.example.homework.presentation.ui.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GalleryItemDecoration(
    private val spacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        val halfSpacing = spacing / 2
        if (position % 3 == 0) {
            outRect.right = halfSpacing
            outRect.bottom = spacing
        } else if ((position + 1) % 3 == 0) {
            outRect.bottom = spacing
        } else {
            outRect.left = halfSpacing
            outRect.bottom = spacing
        }
    }
}