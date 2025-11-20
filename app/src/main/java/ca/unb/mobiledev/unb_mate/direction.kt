package ca.unb.mobiledev.unb_mate

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class direction : AppCompatActivity() {

    private lateinit var mapImage: ImageView

    // Dots
    private lateinit var startDot: ImageView
    private lateinit var endDot: ImageView
    private lateinit var libraryDot: ImageView
    private lateinit var studentUnionDot: ImageView
    private lateinit var keirsteadDot: ImageView
    private lateinit var securityDot: ImageView
    private lateinit var macLaganDot: ImageView
    private lateinit var headHallDot: ImageView
    private lateinit var forestryDot: ImageView
    private lateinit var tooleHallDot: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.direction_2d)

        mapImage = findViewById(R.id.mapImage)

        // Start / End / Connecting dots
        startDot = findViewById(R.id.startDot)
        endDot = findViewById(R.id.endDot)
        libraryDot = findViewById(R.id.libraryDot)
        studentUnionDot = findViewById(R.id.studentUnionDot)
        keirsteadDot = findViewById(R.id.keirsteadDot)
        securityDot = findViewById(R.id.securityDot)
        macLaganDot = findViewById(R.id.macLaganDot)
        headHallDot = findViewById(R.id.headHallDot)
        forestryDot = findViewById(R.id.forestryDot)
        tooleHallDot = findViewById(R.id.tooleHallDot)


        mapImage.post {
            val centerX = mapImage.width / 2f
            val centerY = mapImage.height / 2f


            val dots = listOf(
                startDot, endDot,
                libraryDot, studentUnionDot, keirsteadDot,
                securityDot, macLaganDot, headHallDot, forestryDot, tooleHallDot
            )

            dots.forEach { dot ->
                dot.x = centerX - dot.width / 2f
                dot.y = centerY - dot.height / 2f
                dot.visibility = ImageView.VISIBLE
            }
        }
    }
}
