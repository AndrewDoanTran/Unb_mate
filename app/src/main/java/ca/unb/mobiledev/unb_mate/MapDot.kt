package ca.unb.mobiledev.unb_mate

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class DotView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private var dotX: Float = -1f
    private var dotY: Float = -1f

    // Call this to update dot position
    fun setDot(x: Float, y: Float) {
        dotX = x
        dotY = y
        invalidate() // redraw
    }

    // Correct signature for onDraw
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (dotX >= 0 && dotY >= 0) {
            canvas.drawCircle(dotX, dotY, 20f, paint)
        }
    }
}
