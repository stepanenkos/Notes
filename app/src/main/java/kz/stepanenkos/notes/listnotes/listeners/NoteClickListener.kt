package kz.stepanenkos.notes.listnotes.listeners

import kz.stepanenkos.notes.NoteData

interface NoteClickListener {
    fun onNoteClick(noteData: NoteData)
}