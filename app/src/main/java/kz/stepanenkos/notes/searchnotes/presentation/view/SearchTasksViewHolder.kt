package kz.stepanenkos.notes.searchnotes.presentation.view

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.presentation.BaseViewHolder
import kz.stepanenkos.notes.common.presentation.ContentTextView
import kz.stepanenkos.notes.listtasks.listeners.TaskClickListener
import kz.stepanenkos.notes.searchnotes.data.AllListItem
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class SearchTasksViewHolder(
    itemView: View,
    private val taskClickListener: TaskClickListener
) : BaseViewHolder<AllListItem>(itemView) {
    private val taskContainer: CardView = itemView.findViewById(R.id.task_item_card_view)
    private val content: ContentTextView = itemView.findViewById(R.id.task_item_content_task)
    private val dateOfCreateTask: TextView =
        itemView.findViewById(R.id.task_item_date_of_create_task)

    override fun onBind(data: AllListItem) {
        val taskData = (data as? AllListItem.TasksListItem)?.taskData ?: return

        content.text = taskData.contentTask
        dateOfCreateTask.text = ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(taskData.dateOfTask), ZoneId.of(
                ZoneId.systemDefault().rules.getOffset(
                    Instant.now()
                ).toString()
            )
        )
            .format(DateTimeFormatter.ofPattern(itemView.context.getString(R.string.search_notes_view_holder_date_pattern)))
        taskContainer.setOnClickListener {
            taskClickListener.onTaskClick(taskData)
        }
    }
}