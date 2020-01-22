package com.courses.common

import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 *  decoration makes top, bottom, start, end has the same padding space
 *  @param space space
 */
class CourseItemDecoration(val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val childPos = parent?.getChildAdapterPosition(view)

        var padding = space
        parent.context?.resources?.let {
            val metrics = it.displayMetrics
            padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, space.toFloat(), metrics).toInt()
        }

        outRect.let{
            it.left = padding
            it.right = padding
            if (childPos == 0) it.top = padding else it.top = 0
            it.bottom = padding
        }

    }
}