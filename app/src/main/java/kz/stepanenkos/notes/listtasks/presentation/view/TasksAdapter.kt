package kz.stepanenkos.notes.listtasks.presentation.view

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.model.TaskData
import kz.stepanenkos.notes.common.extensions.view.gone
import kz.stepanenkos.notes.common.extensions.view.show
import kz.stepanenkos.notes.common.presentation.ContentTextView
import kz.stepanenkos.notes.common.presentation.TitleTextView
import kz.stepanenkos.notes.listtasks.listeners.TaskClickListener
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class TasksAdapter(
    private val taskClickListener: TaskClickListener
) : ListAdapter<TaskData, TasksAdapter.TasksViewHolder>(TasksDiffUtilCallback()) {
    private var tracker: SelectionTracker<TaskData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        return TasksViewHolder(
            itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.note_item, parent, false),
            taskClickListener = taskClickListener
        )
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        tracker?.let {
            holder.onBind(getItem(position), it.isSelected(getItem(position)))
        }
    }

    fun setTracker(tracker: SelectionTracker<TaskData>) {
        this.tracker = tracker
    }

    override fun getItemId(position: Int) = position.toLong()

    inner class TasksViewHolder(
        itemView: View,
        private val taskClickListener: TaskClickListener
    ) : RecyclerView.ViewHolder(itemView) {
        private val noteContainer: CardView = itemView.findViewById(R.id.note_item_card_view)
        private val title: TitleTextView = itemView.findViewById(R.id.note_item_title_note)
        private val contentTask: ContentTextView =
            itemView.findViewById(R.id.note_item_content_note)
        private val checkBox: CheckBox = itemView.findViewById(R.id.note_item_checkbox)
        private val dateOfCreateNote: TextView =
            itemView.findViewById(R.id.note_item_date_of_create_note)

        fun onBind(taskData: TaskData, isActivated: Boolean = false) {
            contentTask.text = Html.fromHtml(taskData.contentTask).toString()
            dateOfCreateNote.text = ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(taskData.dateOfTask), ZoneId.of(
                    ZoneId.systemDefault().rules.getOffset(
                        Instant.now()
                    ).toString()
                )
            ).format(
                DateTimeFormatter.ofPattern(
                    "HH:mm dd MMMM yyyy"
                )
            )
            noteContainer.setOnClickListener {
                taskClickListener.onTaskClick(taskData)
            }
            if (isActivated) {
                checkBox.show()
                checkBox.isChecked = isActivated
            } else {
                checkBox.isChecked = isActivated
                checkBox.gone()
            }
            checkBox.isActivated = !isActivated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<TaskData> =
            object : ItemDetailsLookup.ItemDetails<TaskData>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): TaskData = getItem(adapterPosition)
            }
    }

}

