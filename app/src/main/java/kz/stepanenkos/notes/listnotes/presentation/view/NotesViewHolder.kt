package kz.stepanenkos.notes.listnotes.presentation.view

import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.listnotes.listeners.NoteClickListener
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class NotesViewHolder(
    itemView: View,
    private val noteClickListener: NoteClickListener
) : RecyclerView.ViewHolder(itemView) {
    private val noteContainer: CardView = itemView.findViewById(R.id.note_item_card_view)
    private val titleNote: TextView = itemView.findViewById(R.id.note_item_title_note)
    private val contentNote: TextView = itemView.findViewById(R.id.note_item_content_note)
    private val dateOfCreateNote: TextView =
        itemView.findViewById(R.id.note_item_date_of_create_note)

    fun onBind(noteData: NoteData) {
        titleNote.text = noteData.titleNote
        contentNote.text = Html.fromHtml(noteData.contentNote).toString()
        dateOfCreateNote.text = ZonedDateTime.ofInstant(Instant.ofEpochSecond(noteData.dateOfNote), ZoneId.of(
            ZoneId.systemDefault().rules.getOffset(
                Instant.now()
            ).toString()
        )).format(DateTimeFormatter.ofPattern("HH:mm dd MMMM yyyy"))
        noteContainer.setOnClickListener {
            noteClickListener.onNoteClick(noteData)
        }
    }
}