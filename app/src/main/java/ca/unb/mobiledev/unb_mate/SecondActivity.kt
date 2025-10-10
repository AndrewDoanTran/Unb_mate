package ca.unb.mobiledev.unb_mate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity)

        val prefs = getSharedPreferences("userInformation", MODE_PRIVATE)
        val userName = prefs.getString("USER_NAME", "")
        val studentId = prefs.getString("STUDENT_ID", "")
        val countGetIn = prefs.getInt("COUNT_GET_IN", 0) // get from SharedPreferences

        val welcomeText = findViewById<TextView>(R.id.welcomeText)

        if (!userName.isNullOrEmpty() && countGetIn > 0) {
            welcomeText.text = "Welcome back $userName ($studentId)!"
        } else {
            welcomeText.text = "Welcome $userName ($studentId)!"
        }

// Increment counter and save
        prefs.edit().putInt("COUNT_GET_IN", countGetIn + 1).apply()


        val calendarBtn = findViewById<Button>(R.id.calendarButton)
        val navBtn = findViewById<Button>(R.id.navigationButton)
        val qrBtn = findViewById<Button>(R.id.qrButton)

        // Open Calendar
        calendarBtn.setOnClickListener{
            val calendarIntent = Intent(this, Calender::class.java)
            startActivity(calendarIntent)
        }

        // Open Google Maps (example: UNB)
        navBtn.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:45.9454,-66.6417?q=University of New Brunswick")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        // Placeholder for QR code scanning
//        qrBtn.setOnClickListener {
//            // Later you can integrate ZXing or ML Kit for scanning
//            welcomeText.text = "QR Scan feature coming soon..."
//        }
    }
}
