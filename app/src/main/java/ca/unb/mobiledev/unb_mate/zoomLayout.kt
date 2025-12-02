package ca.unb.mobiledev.unb_mate

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.FrameLayout

class ZoomLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var scaleFactor = 1f
    private val matrix = Matrix()
    private val scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor

            // limit zoom
            scaleFactor = scaleFactor.coerceIn(0.5f, 3.0f)

            matrix.setScale(scaleFactor, scaleFactor)
            this@ZoomLayout.pivotX = detector.focusX
            this@ZoomLayout.pivotY = detector.focusY
            this@ZoomLayout.scaleX = scaleFactor
            this@ZoomLayout.scaleY = scaleFactor
            return true
        }
    })

    private var lastX = 0f
    private var lastY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)

        if (!scaleDetector.isInProgress) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.x
                    lastY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.x - lastX
                    val dy = event.y - lastY

                    translationX += dx
                    translationY += dy
                }
            }
        }
        return true
    }
}
