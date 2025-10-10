package ca.unb.mobiledev.unb_mate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adapter class for RecyclerView
class ReminderAdapter(
    private val reminders: List<Reminder> // list of reminders from your database
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    // ViewHolder defines how each item looks
    class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.assignmentTitle)
        val classText: TextView = itemView.findViewById(R.id.className)
        val dueDateText: TextView = itemView.findViewById(R.id.dueDate)
        val dueTimeText: TextView = itemView.findViewById(R.id.dueTime)
    }

    // Create a new view for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reminder_items, parent, false)
        return ReminderViewHolder(view)
    }

    // Bind data to the view
    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.titleText.text = reminder.title
        holder.classText.text = reminder.className
        holder.dueDateText.text = reminder.dueDate
        holder.dueTimeText.text = reminder.dueTime
    }

    override fun getItemCount(): Int {
        return reminders.size
    }
}
