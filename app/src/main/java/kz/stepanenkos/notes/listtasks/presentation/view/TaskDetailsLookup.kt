package kz.stepanenkos.notes.listtasks.presentation.view


import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import kz.stepanenkos.notes.common.model.TaskData

class TaskDetailsLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<TaskData>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<TaskData>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as TasksAdapter.TasksViewHolder)
                .getItemDetails()
        }
        return null
    }
}