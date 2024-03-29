package kz.stepanenkos.notes.searchnotes.presentation.view

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.presentation.BaseViewHolder
import kz.stepanenkos.notes.common.presentation.ContentTextView
import kz.stepanenkos.notes.common.presentation.TitleTextView
import kz.stepanenkos.notes.listnotes.listeners.NoteClickListener
import kz.stepanenkos.notes.searchnotes.data.AllListItem
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class SearchNotesViewHolder(
    itemView: View,
    private val noteClickListener: NoteClickListener
) : BaseViewHolder<AllListItem>(itemView) {
    private val noteContainer: CardView = itemView.findViewById(R.id.note_item_card_view)
    private val title: TitleTextView = itemView.findViewById(R.id.note_item_title_note)
    private val content: ContentTextView = itemView.findViewById(R.id.note_item_content_note)
    private val dateOfCreateNote: TextView =
        itemView.findViewById(R.id.note_item_date_of_create_note)

    override fun onBind(data: AllListItem) {
        val noteData = (data as? AllListItem.NotesListItem)?.noteData ?: return

        title.text = noteData.titleNote
        content.text = noteData.contentNote
        dateOfCreateNote.text = ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(noteData.dateOfNote), ZoneId.of(
                ZoneId.systemDefault().rules.getOffset(
                    Instant.now()
                ).toString()
            )
        )
            .format(DateTimeFormatter.ofPattern(itemView.context.getString(R.string.search_notes_view_holder_date_pattern)))
        noteContainer.setOnClickListener {
            noteClickListener.onNoteClick(noteData)
        }
    }
}