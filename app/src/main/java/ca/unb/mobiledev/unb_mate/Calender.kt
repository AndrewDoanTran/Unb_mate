package ca.unb.mobiledev.unb_mate

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.launch



class Calender : AppCompatActivity() {

    private lateinit var assignmentName: EditText
    private lateinit var className: EditText
    private lateinit var date: EditText
    private lateinit var time: EditText
    private lateinit var submitButton: Button
    private lateinit var viewSchedule: Button
    private lateinit var reminderDatabase: ReminderDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.assignment_reminder)

        // RecyclerView setup
        val recyclerView = findViewById<RecyclerView>(R.id.reminderList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize views
        assignmentName = findViewById(R.id.assignmentTitle)
        className = findViewById(R.id.className)
        date = findViewById(R.id.dueDate)
        time = findViewById(R.id.dueTime)
        submitButton = findViewById(R.id.submitButton)
        viewSchedule = findViewById(R.id.viewDueDate)
        //editButton = findViewById(R.id.update)

        // Initialize Room database
        reminderDatabase = Room.databaseBuilder(
            applicationContext,
            ReminderDatabase::class.java,
            "reminder_database"
        ).build()

        // Save button
        submitButton.setOnClickListener {
            val title = assignmentName.text.toString()
            val course = className.text.toString()
            val dueDate = date.text.toString()
            val dueTime = time.text.toString()

            if (title.isNotEmpty() && course.isNotEmpty() && dueDate.isNotEmpty() && dueTime.isNotEmpty()) {
                val reminder = Reminder(
                    title = title,
                    className = course,
                    dueDate = dueDate,
                    dueTime = dueTime
                )

                // Clear fields
                assignmentName.text.clear()
                className.text.clear()
                date.text.clear()
                time.text.clear()

                // Insert in background
                lifecycleScope.launch {
                    reminderDatabase.reminderDao().insertReminder(reminder)
                    runOnUiThread {
                        Toast.makeText(this@Calender, "Reminder saved!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // View Schedule button
        viewSchedule.setOnClickListener {
            // Clear fields
            assignmentName.text.clear()
            className.text.clear()
            date.text.clear()
            time.text.clear()

            // Show RecyclerView
            recyclerView.visibility = View.VISIBLE
            assignmentName.visibility = View.GONE
            className.visibility = View.GONE
            date.visibility = View.GONE
            time.visibility = View.GONE
            submitButton.visibility = View.GONE
            viewSchedule.visibility = View.GONE



            // Observe reminders
            reminderDatabase.reminderDao().getAllReminders().observe(this) { reminders ->
                recyclerView.adapter = ReminderAdapter(reminders)
            }
        }
    }
}
