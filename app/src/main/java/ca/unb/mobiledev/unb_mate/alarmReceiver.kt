package ca.unb.mobiledev.unb_mate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Get the reminder data from the intent
        val reminderId = intent.getIntExtra("id", -1)
        val message = intent.getStringExtra("message") ?: "You have an assignment!"

        // Build the notification
        val builder = NotificationCompat.Builder(context, "schedule_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Assignment Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Show the notification
        with(NotificationManagerCompat.from(context)) {
            notify(reminderId, builder.build())
        }
        Log.d("AlarmReceiver", "Received alarm with message: $message")
        Toast.makeText(context, "Alarm triggered!", Toast.LENGTH_SHORT).show()

    }
}
