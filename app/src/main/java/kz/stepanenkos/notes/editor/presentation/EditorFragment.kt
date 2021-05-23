package kz.stepanenkos.notes.editor.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestoreException
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.extensions.view.disabled
import kz.stepanenkos.notes.common.extensions.view.enabled
import kz.stepanenkos.notes.common.presentation.ContentNoteEditText
import kz.stepanenkos.notes.common.presentation.TitleNoteEditText
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditorFragment : Fragment() {
    private val editorViewModel: EditorViewModel by viewModel()

    private lateinit var titleNote: TitleNoteEditText
    private lateinit var contentNote: ContentNoteEditText
    private lateinit var doneNote: ImageView
    private lateinit var editNote: ImageView

    private var noteData: NoteData = NoteData()
    private var idNote: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isNotBlankTextFields(titleNote, contentNote) &&
                            noteData.titleNote == titleNote.text.toString() &&
                            noteData.contentNote == contentNote.text.toString()) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            fillNoteData(titleNote, contentNote)
                            editorViewModel.saveNote(noteData)
                        }
                        doneNoteUI()

                        Snackbar.make(
                            requireView(),
                            getString(R.string.editor_fragment_note_saved),
                            Snackbar.LENGTH_LONG
                        ).show()

                    }
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_editor, container, false)
        titleNote = root.findViewById(R.id.fragment_editor_title_note)
        contentNote = root.findViewById(R.id.fragment_editor_content_note)
        doneNote = root.findViewById(R.id.fragment_editor_apply_changed_button)
        editNote = root.findViewById(R.id.fragment_editor_edit_text_button)

        editNote.disabled()
        doneNote.enabled()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        idNote = arguments?.getString("ID")
        idNote?.let { noteById ->
            editNote.enabled()
            doneNote.disabled()
            editorViewModel.getNoteById(noteById)
        }

        editorViewModel.noteById.observe(viewLifecycleOwner, ::showNote)

        editorViewModel.errorReceivingNote.observe(viewLifecycleOwner, ::showError)

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        doneNote.setOnClickListener {
            if (!isNotBlankTextFields(titleNote, contentNote)) {
                Snackbar.make(
                    requireView(),
                    getString(R.string.editor_fragment_cannot_save_empty_note),
                    Snackbar.LENGTH_LONG
                ).show()
            }

            if (isNotBlankTextFields(titleNote, contentNote)) {

                fillNoteData(titleNote, contentNote)

                CoroutineScope(Dispatchers.IO).launch {
                    editorViewModel.saveNote(noteData)
                }

                doneNoteUI()

                Snackbar.make(
                    requireView(),
                    getString(R.string.editor_fragment_note_saved),
                    Snackbar.LENGTH_LONG
                ).show()
            }
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
    private fun fillNoteData(titleNote: EditText, contentNote: EditText) {
        if(isNotBlankTextFields(titleNote, contentNote)) {
            noteData.titleNote = titleNote.text.toString()
            noteData.contentNote = titleNote.text.toString()
            noteData.searchKeywords.clear()
            fillSearchKeywordsList(titleNote, contentNote)
        }
    }

    private fun fillSearchKeywordsList(titleNote: EditText, contentNote: EditText) {
        noteData.searchKeywords.add(titleNote.text.toString().toLowerCase(Locale.ROOT))
        noteData.searchKeywords.addAll(
            titleNote.text.toString().toLowerCase(Locale.ROOT).split(Regex("[\\p{Punct}\\s]+"))
        )

        noteData.searchKeywords.add(contentNote.text.toString().toLowerCase(Locale.ROOT))
        noteData.searchKeywords.addAll(
            contentNote.text.toString().toLowerCase(Locale.ROOT).split(Regex("[\\p{Punct}\\s]+"))
        )
    }

    private fun isNotBlankTextFields(titleNote: EditText, contentNote: EditText): Boolean {
        return titleNote.text.isNotBlank() && contentNote.text.isNotBlank()
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
