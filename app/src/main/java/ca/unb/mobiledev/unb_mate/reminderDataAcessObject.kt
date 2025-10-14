package ca.unb.mobiledev.unb_mate

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ReminderDao {

    @Insert
    suspend fun insertReminder(reminder: Reminder): Long

    @Query("SELECT * FROM reminders ORDER BY dueDate ASC , dueTime ASC")
    fun getAllReminders(): LiveData<List<Reminder>>

    @Query("DELETE FROM reminders WHERE title = :title AND className = :className AND dueDate = :dueDate AND dueTime = :dueTime")
    suspend fun deleteReminder(title: String, className: String, dueDate: String, dueTime: String)

    // Delete reminders where due date and time is before now
    @Query("DELETE FROM reminders WHERE dueDate || ' ' || dueTime < :currentDateTime")
    suspend fun deletePastReminders(currentDateTime: String)

}
