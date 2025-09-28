package com.example.mada.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.ceil

class DecorationUtil(
    private val spanCount: Int,
    private val spacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // 아이템 위치 (0부터 시작)
        val column = position % spanCount                  // 열 index (0,1,2,...)
        val itemCount = state.itemCount
        val rowCount = ceil(itemCount / spanCount.toDouble()).toInt()
        val currentRow = position / spanCount + 1 // 1부터 시작

        // 왼쪽 여백
        if (column == 0) {
            outRect.left = 0
        } else {
            outRect.left = spacing
        }

        // 위쪽 여백
        if (position < spanCount) {
            outRect.top = 0
        } else {
            outRect.top = spacing
        }

        // 마지막 줄 → bottom 여백 추가
        if (currentRow == rowCount) {
            outRect.bottom = spacing
        } else {
            outRect.bottom = 0
        }
    }
}