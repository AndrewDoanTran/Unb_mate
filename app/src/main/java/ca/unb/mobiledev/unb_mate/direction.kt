package ca.unb.mobiledev.unb_mate

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener

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

    // Intersections
    private lateinit var intersection1: ImageView
    private lateinit var intersection2: ImageView
    private lateinit var intersection3: ImageView
    private lateinit var intersection4: ImageView
    private lateinit var intersection5: ImageView

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

        // Intersections
        intersection1 = findViewById(R.id.intersection1)
        intersection2 = findViewById(R.id.intersection2)
        intersection3 = findViewById(R.id.intersection3)
        intersection4 = findViewById(R.id.intersection4)
        intersection5 = findViewById(R.id.intersection5)

        val startInput = findViewById<EditText>(R.id.startingLocat)
        val endInput = findViewById<EditText>(R.id.endingLocat)

        startInput.addTextChangedListener {
            updateStartAndEnd(startInput.text.toString(), endInput.text.toString())
        }

        endInput.addTextChangedListener {
            updateStartAndEnd(startInput.text.toString(), endInput.text.toString())
        }

        // Position all dots on the map
        mapImage.post {
            val centerX = mapImage.width / 2f
            val centerY = mapImage.height / 2f

            fun placeDot(dot: ImageView, offsetX: Float, offsetY: Float) {
                dot.x = centerX - dot.width / 2f + offsetX
                dot.y = centerY - dot.height / 2f + offsetY
                dot.visibility = ImageView.VISIBLE
            }

            placeDot(libraryDot, 158f, 80f)
            placeDot(studentUnionDot, -100f, 50f)
            placeDot(keirsteadDot, 250f, 95f)
            placeDot(securityDot, 350f, 145f)
            placeDot(macLaganDot, 350f, 250f)
            placeDot(headHallDot, 350f, 700f)
            placeDot(forestryDot, 250f, 345f)
            placeDot(tooleHallDot, 158f, 345f)

            placeDot(intersection1, 120f, 120f)
            placeDot(intersection2, 190f, 120f)
            placeDot(intersection3, 120f, 250f)
            placeDot(intersection4, 190f, 250f)
            placeDot(intersection5, 350f, 340f)
        }
    }

    private fun updateStartAndEnd(startName: String, endName: String) {
        val buildingMap = mapOf(
            "library" to libraryDot,
            "student union" to studentUnionDot,
            "keirstead" to keirsteadDot,
            "security" to securityDot,
            "maclagan" to macLaganDot,
            "head hall" to headHallDot,
            "forestry" to forestryDot,
            "toole" to tooleHallDot
        )

        // Make all connecting dots visible
        buildingMap.values.forEach { it.visibility = ImageView.VISIBLE }
        intersection1.visibility = ImageView.VISIBLE
        intersection2.visibility = ImageView.VISIBLE
        intersection3.visibility = ImageView.VISIBLE
        intersection4.visibility = ImageView.VISIBLE
        intersection5.visibility = ImageView.VISIBLE

        // Hide start and end dots first
        startDot.visibility = ImageView.INVISIBLE
        endDot.visibility = ImageView.INVISIBLE

        if (startName.isNotBlank()) {
            buildingMap[startName.lowercase()]?.let { dot ->
                startDot.x = dot.x
                startDot.y = dot.y
                startDot.visibility = ImageView.VISIBLE
                dot.visibility = ImageView.INVISIBLE
            }
        }

        if (endName.isNotBlank()) {
            buildingMap[endName.lowercase()]?.let { dot ->
                endDot.x = dot.x
                endDot.y = dot.y
                endDot.visibility = ImageView.VISIBLE
                dot.visibility = ImageView.INVISIBLE
            }
        }

        // Draw route if both start and end are provided
        if (startName.isNotBlank() && endName.isNotBlank()) {
            drawRouteLines()
        } else {
            findViewById<LineView>(R.id.lineView).setLines(emptyList())
        }
    }

    private fun drawRouteLines() {
        val lineView = findViewById<LineView>(R.id.lineView)

        val startPos = Pair(startDot.x + startDot.width / 2f, startDot.y + startDot.height / 2f)
        val endPos = Pair(endDot.x + endDot.width / 2f, endDot.y + endDot.height / 2f)

        val allDots = listOf(
            libraryDot, studentUnionDot, keirsteadDot,
            securityDot, macLaganDot, headHallDot,
            forestryDot, tooleHallDot,
            intersection1, intersection2, intersection3,
            intersection4, intersection5
        )

        val availableDots = allDots.filter { it.visibility == ImageView.VISIBLE }
            .map { Pair(it.x + it.width / 2f, it.y + it.height / 2f) }
            .toMutableList()

        val path = mutableListOf<Pair<Float, Float>>()
        path.add(startPos)

        var current = startPos

        while (availableDots.isNotEmpty()) {
            val closest = availableDots.minByOrNull { distance(it, current) } ?: break
            path.add(closest)
            current = closest
            availableDots.remove(closest)
        }

        path.add(endPos)

        val lines = path.zipWithNext { a, b ->
            LineView.Line(a.first, a.second, b.first, b.second)
        }

        lineView.setLines(lines)
    }

    private fun distance(a: Pair<Float, Float>, b: Pair<Float, Float>): Float
    {
        return Math.hypot((a.first - b.first).toDouble(), (a.second - b.second).toDouble()).toFloat()
    }
}
