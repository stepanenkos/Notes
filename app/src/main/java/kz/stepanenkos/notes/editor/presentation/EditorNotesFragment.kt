package kz.stepanenkos.notes.editor.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.extensions.view.disabled
import kz.stepanenkos.notes.common.extensions.view.enabled
import kz.stepanenkos.notes.common.model.NoteData
import kz.stepanenkos.notes.common.presentation.ContentEditText
import kz.stepanenkos.notes.common.presentation.TitleEditText
import kz.stepanenkos.notes.databinding.FragmentEditorNotesBinding
import kz.stepanenkos.notes.listnotes.presentation.NOTE_ID
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditorNotesFragment : Fragment(R.layout.fragment_editor_notes) {
    private val editorViewModel: EditorViewModel by viewModel()
    private var binding: FragmentEditorNotesBinding? = null

    private lateinit var titleNote: TitleEditText
    private lateinit var contentNote: ContentEditText
    private lateinit var doneNote: ImageView
    private lateinit var editNote: ImageView

    private var idNote: String? = null
    private var noteData: NoteData? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (idNote != null && isEqualsContentInNoteDataAndFields()) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    } else if (idNote != null && !isEqualsContentInNoteDataAndFields()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            noteData?.copy(
                                titleNote = titleNote.text.toString(),
                                contentNote = contentNote.text.toString()
                            )?.let {
                                editorViewModel.updateNote(
                                    it
                                )
                            }
                        }
                        isEnabled = false
                        requireActivity().onBackPressed()
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            editorViewModel.saveNote(titleNote.text.toString(), contentNote.text.toString())
                        }
                        doneNoteUI()

                        Snackbar.make(
                            requireView(),
                            getString(R.string.editor_notes_fragment_note_saved),
                            Snackbar.LENGTH_LONG
                        ).show()
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditorNotesBinding.bind(view)

        titleNote = binding!!.fragmentEditorTitleNote
        contentNote = binding!!.fragmentEditorContentNote
        doneNote = binding!!.fragmentEditorApplyChangedButton
        editNote = binding!!.fragmentEditorEditTextButton

        editNote.disabled()
        doneNote.enabled()

        idNote = arguments?.getString(NOTE_ID)
        idNote?.let { noteById ->
            editNote.enabled()
            doneNote.disabled()
            editorViewModel.getNoteById(noteById)
        }

        editorViewModel.noteById.observe(viewLifecycleOwner, ::showNote)

        editorViewModel.errorReceiving.observe(viewLifecycleOwner, ::showError)

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        doneNote.setOnClickListener {
            if (!isNotBlankTextFields(titleNote, contentNote)) {
                Snackbar.make(
                    requireView(),
                    getString(R.string.editor_notes_fragment_cannot_save_empty_note),
                    Snackbar.LENGTH_LONG
                ).show()
            } else if(noteData != null && !isEqualsContentInNoteDataAndFields()) {
                CoroutineScope(Dispatchers.IO).launch {
                    noteData?.copy(
                        titleNote = titleNote.text.toString(),
                        contentNote = contentNote.text.toString()
                    )?.let {
                        editorViewModel.updateNote(
                            it
                        )
                    }
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                editorViewModel.saveNote(titleNote.text.toString(), contentNote.text.toString())
            }

            doneNoteUI()

            Snackbar.make(
                requireView(),
                getString(R.string.editor_notes_fragment_note_saved),
                Snackbar.LENGTH_LONG
            ).show()

        }

        editNote.setOnClickListener {
            editNoteUI()
        }
    }

    private fun doneNoteUI() {
        titleNote.disabled()
        contentNote.disabled()
        doneNote.disabled()
        editNote.enabled()
    }

    private fun editNoteUI() {
        titleNote.enabled()
        contentNote.enabled()
        doneNote.enabled()
        editNote.disabled()
    }

    private fun isNotBlankTextFields(titleNote: EditText, contentNote: EditText): Boolean {
        return titleNote.text.isNotBlank() && contentNote.text.isNotBlank()
    }

    private fun isEqualsContentInNoteDataAndFields(): Boolean {
        return noteData?.titleNote == titleNote.text.toString() &&
                noteData?.contentNote == contentNote.text.toString()
    }

    private fun showNote(noteData: NoteData?) {
        if (noteData != null) {
            this.noteData = noteData
            titleNote.disabled()
            contentNote.disabled()
            titleNote.setText(noteData.titleNote)
            contentNote.setText(noteData.contentNote.trim())
        }
    }

    private fun showError(firebaseFirestoreException: FirebaseFirestoreException) {
        Snackbar.make(
            requireView(),
            firebaseFirestoreException.localizedMessage,
            Snackbar.LENGTH_LONG
        ).show()
    }
}
