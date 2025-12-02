package ca.unb.mobiledev.unb_mate

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import java.util.Locale
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.widget.Toast
import kotlin.math.hypot

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

    private val REQUEST_CODE_SPEECH_START = 100
    private val REQUEST_CODE_SPEECH_END = 101

    // Dot class to store ImageView and name
    data class Dot(val name: String, val image: ImageView)

    // Predefined routes
    private lateinit var route1: List<Dot>
    private lateinit var route2: List<Dot>
    private lateinit var route3: List<Dot>
    private lateinit var route4: List<Dot>
    private lateinit var route5: List<Dot>
    private lateinit var route6: List<Dot>

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
        val micButtonInput = findViewById<ImageView>(R.id.startMicButton)
        val micButtonEnd = findViewById<ImageView>(R.id.destMicButton)

        startInput.addTextChangedListener {
            updateStartAndEnd(startInput.text.toString(), endInput.text.toString())
        }
        endInput.addTextChangedListener {
            updateStartAndEnd(startInput.text.toString(), endInput.text.toString())
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.RECORD_AUDIO), 200
            )
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

            // Initialize routes with Dot objects
            route1 = listOf(
                Dot("student union", studentUnionDot),
                Dot("intersection1", intersection1),
                Dot("library", libraryDot),
                Dot("intersection2", intersection2),
                Dot("keirstead", keirsteadDot),
                Dot("security", securityDot),
                Dot("maclagan", macLaganDot),
                Dot("intersection5", intersection5),
                Dot("head hall", headHallDot)
            )

            route2 = listOf(
                Dot("student union", studentUnionDot),
                Dot("intersection1", intersection1),
                Dot("intersection3", intersection3),
                Dot("toole", tooleHallDot),
                Dot("forestry", forestryDot),
                Dot("intersection5", intersection5),
                Dot("head hall", headHallDot)
            )

            route3 = listOf(
                Dot("student union", studentUnionDot),
                Dot("library", libraryDot),
                Dot("intersection1", intersection1),
                Dot("intersection3", intersection3),
                Dot("toole", tooleHallDot),
                Dot("intersection4", intersection4),
                Dot("intersection2", intersection2)
            )

            route4 = listOf(
                Dot("toole", tooleHallDot),
                Dot("forestry", forestryDot),
                Dot("intersection5", intersection5),
                Dot("head hall", headHallDot)
            )

            route5 = listOf(
                Dot("toole", tooleHallDot),
                Dot("intersection4", intersection4),
                Dot("keirstead", keirsteadDot)
            )

            route6 = listOf(
                Dot("forestry", forestryDot),
                Dot("intersection5", intersection5),
                Dot("maclagan", macLaganDot),
                Dot("security", securityDot),
                Dot("keirstead", keirsteadDot),
                Dot("library", libraryDot)
            )
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

        // Make all dots visible
        buildingMap.values.forEach { it.visibility = ImageView.VISIBLE }
        intersection1.visibility = ImageView.VISIBLE
        intersection2.visibility = ImageView.VISIBLE
        intersection3.visibility = ImageView.VISIBLE
        intersection4.visibility = ImageView.VISIBLE
        intersection5.visibility = ImageView.VISIBLE

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
            val shortestPath = findShortestRouteSegment(startName.lowercase(), endName.lowercase())
            shortestPath?.let { drawRouteLines(it) }
        } else {
            findViewById<LineView>(R.id.lineView).setLines(emptyList())
        }
    }

    // Calculate total distance along a segment
    private fun totalDistance(segment: List<Dot>): Float {
        var distance = 0f
        for (i in 0 until segment.size - 1) {
            distance += distanceBetween(segment[i].image, segment[i + 1].image)
        }
        return distance
    }

    private fun distanceBetween(a: ImageView, b: ImageView): Float {
        val x1 = a.x + a.width / 2f
        val y1 = a.y + a.height / 2f
        val x2 = b.x + b.width / 2f
        val y2 = b.y + b.height / 2f
        return hypot((x1 - x2), (y1 - y2))
    }

    // Find the shortest segment from start to end among predefined routes
    private fun findShortestRouteSegment(startName: String, endName: String): List<Dot>? {
        val routes = listOf(route1, route2, route3, route4, route5, route6)
        var shortestDistance = Float.MAX_VALUE
        var bestSegment: List<Dot>? = null

        for (route in routes) {
            val startIndex = route.indexOfFirst { it.name.equals(startName, ignoreCase = true) }
            val endIndex = route.indexOfFirst { it.name.equals(endName, ignoreCase = true) }

            if (startIndex != -1 && endIndex != -1) {
                // start -> end
                if (startIndex < endIndex) {
                    val segment = route.subList(startIndex, endIndex + 1)
                    val distance = totalDistance(segment)
                    if (distance < shortestDistance) {
                        shortestDistance = distance
                        bestSegment = segment
                    }
                }
                // end -> start (reverse)
                if (endIndex < startIndex) {
                    val segment = route.subList(endIndex, startIndex + 1).reversed()
                    val distance = totalDistance(segment)
                    if (distance < shortestDistance) {
                        shortestDistance = distance
                        bestSegment = segment
                    }
                }
            }
        }
        return bestSegment
    }

    private fun drawRouteLines(routeSegment: List<Dot>) {
        val lineView = findViewById<LineView>(R.id.lineView)
        val lines = routeSegment.zipWithNext { a, b ->
            LineView.Line(
                a.image.x + a.image.width / 2f,
                a.image.y + a.image.height / 2f,
                b.image.x + b.image.width / 2f,
                b.image.y + b.image.height / 2f
            )
        }
        lineView.setLines(lines)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val result = results?.get(0) ?: ""
            Toast.makeText(this, "Recognized: $result", Toast.LENGTH_SHORT).show()

            when (requestCode) {
                REQUEST_CODE_SPEECH_START -> findViewById<EditText>(R.id.startingLocat).setText(result)
                REQUEST_CODE_SPEECH_END -> findViewById<EditText>(R.id.endingLocat).setText(result)
            }
        } else {
            Toast.makeText(this, "Speech not recognized", Toast.LENGTH_SHORT).show()
        }
    }
}
