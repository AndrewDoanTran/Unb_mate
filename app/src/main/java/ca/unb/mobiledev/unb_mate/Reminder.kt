package ca.unb.mobiledev.unb_mate

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val className: String,
    val dueDate: String,
    val dueTime: String,
    val completed: Boolean = false
)


//From my understanding that entry seem like the json file, one element contains so many informations
//