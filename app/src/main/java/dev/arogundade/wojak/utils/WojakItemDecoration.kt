package dev.arogundade.wojak.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class WojakItemDecoration(
    private val color: Int,
    private val transparent: Int = Color.TRANSPARENT,
    private val height: Int,
    private val firstLine: Boolean = false,
    private val lastLine: Boolean = false,
) : ItemDecoration() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = height
    }

    @Deprecated("Deprecated in Java")
    override fun onDrawOver(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount

        /* set paint color */
        paint.color = color

        if (childCount <= 0) return

        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val params = child
                .layoutParams as RecyclerView.LayoutParams
            val top: Int = child.bottom + params.bottomMargin
            val bottom = top + height

            if (i == 0 && firstLine) {
                /* draw first line */
                c.drawRect(
                    left.toFloat(),
                    child.top.toFloat(),
                    right.toFloat(),
                    height.toFloat(),
                    paint
                )
            }

            if (i == childCount - 1) {
                if (lastLine) {
                    paint.color = color
                    c.drawRect(
                        left.toFloat(),
                        top.toFloat(),
                        right.toFloat(),
                        bottom.toFloat(),
                        paint
                    )
                } else {
                    paint.color = transparent
                    c.drawRect(
                        left.toFloat(),
                        top.toFloat(),
                        right.toFloat(),
                        bottom.toFloat() + 100F,
                        paint
                    )
                }
            } else {
                paint.color = color
                c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
            }

        }
    }

    init {
        paint.isAntiAlias = true
    }
}