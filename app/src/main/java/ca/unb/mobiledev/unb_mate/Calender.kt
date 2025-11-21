package ca.unb.mobiledev.unb_mate

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.format

class Calender : AppCompatActivity() {

    private lateinit var assignmentName: EditText
    private lateinit var className: EditText
    private lateinit var date: EditText
    private lateinit var time: EditText
    private lateinit var submitButton: Button
    private lateinit var viewSchedule: Button
    private lateinit var reminderDatabase: ReminderDatabase

    private lateinit var testingNofication : Button

    private lateinit var deleteReminder : Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.assignment_reminder)

        // Check notification permission & create notification channel
        checkNotificationPermission()
        createNotificationChannel()

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
        //testingNofication = findViewById(R.id.testingNofication)
        deleteReminder = findViewById(R.id.deleteReminder)

        // Initialize Room database
        reminderDatabase = Room.databaseBuilder(
            applicationContext,
            ReminderDatabase::class.java,
            "reminder_database"
        ).build()

        // Save button
        submitButton.setOnClickListener {
            val title = assignmentName.text.toString().trim()
            val course = className.text.toString().trim()
            val dueDate = date.text.toString().trim()
            val dueTime = time.text.toString().trim()

            if (title.isEmpty() || course.isEmpty() || dueDate.isEmpty() || dueTime.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            dateFormat.isLenient = false
            timeFormat.isLenient = false

            val format = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            val now = format.format(Date())


            lifecycleScope.launch {
                reminderDatabase.reminderDao().deletePastReminders(now)
            }
            try {

                // Combine date & time
                val combinedFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())



                combinedFormat.isLenient = false
                val triggerTime = combinedFormat.parse("$dueDate $dueTime")?.time
                    ?: throw IllegalArgumentException("Invalid date/time")

                if (triggerTime < System.currentTimeMillis()) {
                    Toast.makeText(this, "Please enter a future time!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val reminder = Reminder(title = title, className = course, dueDate = dueDate, dueTime = dueTime)

                // Clear fields
                assignmentName.text.clear()
                className.text.clear()
                date.text.clear()
                time.text.clear()

                lifecycleScope.launch {
                    val reminderId = reminderDatabase.reminderDao().insertReminder(reminder).toInt()
                    scheduleAlarm(reminderId, triggerTime, title, course)
                    runOnUiThread {
                        Toast.makeText(this@Calender, "Reminder saved and alarm set!", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(this, "Invalid date/time format! Use dd-MM-yyyy for date and HH:mm for time.", Toast.LENGTH_LONG).show()
            }
        }


        // View Schedule button
        viewSchedule.setOnClickListener {
            // Clear input fields
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
            deleteReminder.visibility = View.GONE

            // Observe reminders
            val format = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            val now = format.format(Date())
            lifecycleScope.launch {
                reminderDatabase.reminderDao().deletePastReminders(now)
            }

            reminderDatabase.reminderDao().getAllReminders().observe(this) { reminders ->
                recyclerView.adapter = ReminderAdapter(reminders)
            }
        }

//
//        testingNofication.setOnClickListener {
//            val builder = NotificationCompat.Builder(this, "schedule_channel")
//                .setSmallIcon(android.R.drawable.ic_dialog_info)
//                .setContentTitle("Test Notification")
//                .setContentText("This is a test notification!")
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true)
//
//            NotificationManagerCompat.from(this).notify(999, builder.build())
//        }

        deleteReminder.setOnClickListener {
            val title = assignmentName.text.toString().trim()
            val course = className.text.toString().trim()
            val dueDate = date.text.toString().trim()
            val dueTime = time.text.toString().trim()

            if (title.isEmpty() || course.isEmpty() || dueDate.isEmpty() || dueTime.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields to delete", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // Delete reminder from DB
                reminderDatabase.reminderDao().deleteReminder(title, course, dueDate, dueTime)

                // Optionally cancel the alarm if it was scheduled
                // If you stored reminderId somewhere, use it here. Otherwise, you can skip.
                // cancelAlarm(reminderId)

                runOnUiThread {
                    Toast.makeText(this@Calender, "Reminder deleted successfully!", Toast.LENGTH_SHORT).show()

                    // Clear fields
                    assignmentName.text.clear()
                    className.text.clear()
                    date.text.clear()
                    time.text.clear()
                }
            }
        }

    }

    // ---------------- Notification Permission ----------------
    private fun checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED) {

                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications allowed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notifications denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ---------------- Notification Channel ----------------
    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Assignment Schedule Channel"
            val descriptionText = "Channel for assignment reminders"
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH
            val channel = android.app.NotificationChannel("schedule_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(android.app.NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    // ---------------- Schedule Alarm ----------------
    private fun scheduleAlarm(reminderId: Int, triggerTime: Long, title: String, course: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("id", reminderId)
            putExtra("message", "Reminder: $title for $course is due soon!")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Check exact alarm permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Please allow exact alarms in system settings", Toast.LENGTH_LONG).show()
                val intentSettings = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intentSettings)
                return
            }
        }

        // Schedule alarm
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } catch (e: SecurityException) {
            Toast.makeText(this, "Cannot schedule exact alarms on this device.", Toast.LENGTH_LONG).show()
        }
    }


    // ---------------- Optional: Cancel Alarm ----------------
    private fun cancelAlarm(reminderId: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
