package ca.unb.mobiledev.unb_mate

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class LineView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val lines = mutableListOf<Line>()
    private val paint = Paint().apply {
        color = 0xFF2196F3.toInt() // Blue
        strokeWidth = 5f
        isAntiAlias = true
    }

    data class Line(val startX: Float, val startY: Float, val endX: Float, val endY: Float)

    fun setLines(newLines: List<Line>) {
        lines.clear()
        lines.addAll(newLines)
        invalidate() // redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (line in lines) {
            canvas.drawLine(line.startX, line.startY, line.endX, line.endY, paint)
        }
    }

}
