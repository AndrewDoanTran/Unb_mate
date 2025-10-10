package ca.unb.mobiledev.unb_mate

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ReminderDao {

    @Insert
    suspend fun insertReminder(reminder: Reminder)

    @Query("SELECT * FROM reminders")
    fun getAllReminders(): LiveData<List<Reminder>>

    @Delete
    suspend fun delete(reminder: Reminder)
}
