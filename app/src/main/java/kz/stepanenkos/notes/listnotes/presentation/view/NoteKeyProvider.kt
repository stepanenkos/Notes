package kz.stepanenkos.notes.listnotes.presentation.view

import androidx.recyclerview.selection.ItemKeyProvider
import kz.stepanenkos.notes.NoteData

class NoteKeyProvider (private val adapter: NotesAdapter) : ItemKeyProvider<NoteData>(SCOPE_CACHED) {
    override fun getKey(position: Int): NoteData =
        adapter.currentList[position]
    override fun getPosition(key: NoteData): Int =
        adapter.currentList.indexOfFirst {it == key}
}