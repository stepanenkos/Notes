package kz.stepanenkos.notes.listnotes.listeners

import kz.stepanenkos.notes.common.model.NoteData

interface NoteClickListener {
    fun onNoteClick(noteData: NoteData)
}