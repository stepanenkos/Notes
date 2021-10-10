package kz.stepanenkos.notes.listtasks.presentation.view

import android.graphics.Paint
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
import kz.stepanenkos.notes.common.extensions.view.gone
import kz.stepanenkos.notes.common.extensions.view.show
import kz.stepanenkos.notes.common.model.TaskData
import kz.stepanenkos.notes.common.presentation.ContentTextView
import kz.stepanenkos.notes.listtasks.listeners.TaskCheckedListener
import kz.stepanenkos.notes.listtasks.listeners.TaskClickListener
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class TasksAdapter(
    private val taskClickListener: TaskClickListener,
    private val taskCheckedListener: TaskCheckedListener
) : ListAdapter<TaskData, TasksAdapter.TasksViewHolder>(TasksDiffUtilCallback()) {
    private var tracker: SelectionTracker<TaskData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        return TasksViewHolder(
            itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.task_item, parent, false),
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
        private val taskContainer: CardView = itemView.findViewById(R.id.task_item_card_view)
        private val contentTask: ContentTextView =
            itemView.findViewById(R.id.task_item_content_task)
        private val checkBoxSelectTask: CheckBox = itemView.findViewById(R.id.task_item_for_select_checkbox)
        private val dateOfCreateTask: TextView =
            itemView.findViewById(R.id.task_item_date_of_create_task)
        private val checkBoxIsDoneTask: CheckBox = itemView.findViewById(R.id.task_item_is_done_task_checkbox)

        fun onBind(taskData: TaskData, isActivated: Boolean = false) {
            contentTask.text = taskData.contentTask
            dateOfCreateTask.text = ZonedDateTime.ofInstant(
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

            checkBoxIsDoneTask.isChecked = taskData.doneTask
            if(taskData.doneTask) {
                contentTask.paintFlags = contentTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                contentTask.paintFlags = 0
            }
            taskContainer.setOnClickListener {
                taskClickListener.onTaskClick(taskData)
            }

            checkBoxIsDoneTask.setOnCheckedChangeListener { _, checked ->
                if(checked) {
                    contentTask.paintFlags = contentTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    contentTask.paintFlags = 0
                }
                taskCheckedListener.onCheckedTask(taskData, checked)
            }

            if (isActivated) {
                checkBoxSelectTask.show()
                checkBoxSelectTask.isChecked = isActivated
            } else {
                checkBoxSelectTask.isChecked = isActivated
                checkBoxSelectTask.gone()
            }
            checkBoxSelectTask.isActivated = !isActivated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<TaskData> =
            object : ItemDetailsLookup.ItemDetails<TaskData>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): TaskData = getItem(adapterPosition)
            }
    }

}

