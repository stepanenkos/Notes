package kz.stepanenkos.notes.listnotes.presentation.view


import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import kz.stepanenkos.notes.common.model.NoteData

class NoteDetailsLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<NoteData>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<NoteData>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as NotesAdapter.NotesViewHolder)
                .getItemDetails()
        }
        return null
    }
}