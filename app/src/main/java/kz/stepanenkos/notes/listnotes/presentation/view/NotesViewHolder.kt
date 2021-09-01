package kz.stepanenkos.notes.listnotes.presentation.view

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.extensions.view.gone
import kz.stepanenkos.notes.common.extensions.view.show
import kz.stepanenkos.notes.common.model.NoteData
import kz.stepanenkos.notes.common.presentation.ContentTextView
import kz.stepanenkos.notes.common.presentation.TitleTextView
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
    private val title: TitleTextView = itemView.findViewById(R.id.note_item_title_note)
    private val content: ContentTextView = itemView.findViewById(R.id.note_item_content_note)
    private val checkBox: CheckBox = itemView.findViewById(R.id.note_item_checkbox)
    private val dateOfCreateNote: TextView =
        itemView.findViewById(R.id.note_item_date_of_create_note)

    fun onBind(noteData: NoteData, isActivated: Boolean = false) {
        title.text = noteData.titleNote
        content.text = noteData.contentNote
        dateOfCreateNote.text = ZonedDateTime.ofInstant(Instant.ofEpochSecond(noteData.dateOfNote), ZoneId.of(
            ZoneId.systemDefault().rules.getOffset(
                Instant.now()
            ).toString()
        )).format(DateTimeFormatter.ofPattern("HH:mm dd MMMM yyyy"))
        noteContainer.setOnClickListener {
            noteClickListener.onNoteClick(noteData)
        }
        if(isActivated) {
            checkBox.show()
            checkBox.isChecked = isActivated
        } else {
            checkBox.isChecked = isActivated
            checkBox.gone()
        }
        checkBox.isActivated = !isActivated
    }

}