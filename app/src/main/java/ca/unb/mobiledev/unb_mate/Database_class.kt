package ca.unb.mobiledev.unb_mate


import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [Reminder::class], version = 1)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
}
