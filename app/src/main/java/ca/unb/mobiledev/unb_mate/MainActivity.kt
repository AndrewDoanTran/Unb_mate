package ca.unb.mobiledev.unb_mate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var userName: EditText
    private lateinit var studentId: EditText
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Check saved data first
        val prefs = getSharedPreferences("userInformation", MODE_PRIVATE)
        val savedName = prefs.getString("USER_NAME", "")
        val savedId = prefs.getString("STUDENT_ID", "")

        if (!savedName.isNullOrEmpty() && !savedId.isNullOrEmpty())
        {
            // Skip MainActivity and go to SecondActivity
            val intent = Intent(this, SecondActivity::class.java)

            startActivity(intent)
            finish() // close MainActivity
            return
        }

        //Set layout
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //Initialize UI elements
        userName = findViewById(R.id.userName)
        studentId = findViewById(R.id.studentId)
        nextButton = findViewById(R.id.submitButton)

        //Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Button click listener
        nextButton.setOnClickListener {
            val nameInput = userName.text.toString()
            val idInput = studentId.text.toString()

            if (nameInput.isEmpty() || idInput.isEmpty()) {
                Toast.makeText(this, "Please enter name and student ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save to SharedPreferences
            prefs.edit()
                .putString("USER_NAME", nameInput)
                .putString("STUDENT_ID", idInput)
                .apply()

            // Go to SecondActivity
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("USER_NAME", nameInput)
            intent.putExtra("STUDENT_ID", idInput)
            startActivity(intent)
            finish() // optional: prevent going back to MainActivity
        }
    }
}
