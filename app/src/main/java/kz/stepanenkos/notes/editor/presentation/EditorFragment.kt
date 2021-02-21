package kz.stepanenkos.notes.editor.presentation

import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.extensions.view.disabled
import kz.stepanenkos.notes.common.presentation.ContentNoteEditText
import kz.stepanenkos.notes.common.presentation.TitleNoteEditText
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditorFragment : Fragment() {
    private val editorViewModel: EditorViewModel by viewModel()

    private lateinit var titleNote: TitleNoteEditText
    private lateinit var contentNote: ContentNoteEditText
    private lateinit var doneNote: ImageView
    private lateinit var editNote: ImageView
    private lateinit var boldButton: ImageView
    private lateinit var underlinedButton: ImageView

    private var noteData: NoteData = NoteData()
    private var noteDataId = ""
    private var isChecked = false
    private var isForEdit = false
    private var startSelectionIndex: Int = -1
    private var endSelectionIndex: Int = -1
    private var isBold = false
    private var spannableString: SpannableString = SpannableString("")
    private var idNote: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_editor, container, false)
        titleNote = root.findViewById(R.id.fragment_editor_title_note)
        contentNote = root.findViewById(R.id.fragment_editor_content_note)
        doneNote = root.findViewById(R.id.fragment_editor_apply_changed)
        editNote = root.findViewById(R.id.fragment_editor_edit_text)
        boldButton = root.findViewById(R.id.fragment_editor_format_bold)
        underlinedButton = root.findViewById(R.id.fragment_editor_format_underlined)
        return root
    }

    override fun onStart() {
        super.onStart()
        idNote = arguments?.getString("ID")
        idNote?.let { editorViewModel.getNoteById(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        idNote = arguments?.getString("ID")
        idNote?.let { editorViewModel.getNoteById(it) }
        editorViewModel.noteById.observe(viewLifecycleOwner, { noteDataInDB ->
            isForEdit = true
            Log.d("TAG", "onViewCreated: $noteDataInDB")
            if (noteDataInDB != null) {
                noteData = noteDataInDB
                titleNote.disabled()
                contentNote.disabled()
                titleNote.setText(noteDataInDB.titleNote)
                contentNote.setText(noteDataInDB.contentNote.trim())
            }
        })
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        doneNote.setOnClickListener {
            if (titleNote.text.toString().isNotBlank() &&
                contentNote.text.toString().isNotBlank() && !isForEdit
            ) {
                noteData.titleNote = titleNote.text.toString()
                noteData.contentNote = contentNote.text.toString()

                editorViewModel.saveNote(noteData)

                titleNote.disabled()
                contentNote.disabled()
                doneNote.disabled()

            } else {
                noteData.titleNote = titleNote.text.toString()
                noteData.contentNote = contentNote.text.toString()

                editorViewModel.updateNote(noteData)

                titleNote.disabled()
                contentNote.disabled()
                doneNote.disabled()

                isForEdit = false
            }

        }

        editNote.setOnClickListener {
            editorViewModel.getNoteById(noteDataId)
            Log.d("TAG", "editNote: $noteData")

            titleNote.isEnabled = true
            isForEdit = true
            contentNote.isEnabled = true
            doneNote.isEnabled = true
        }


        boldButton.setOnClickListener {
            isBold = !isBold
/*            val spanHelper = SpanStyleHelper(
                contentNote,
                contentNote.text,
                contentNote.selectionStart,
                contentNote.selectionEnd
            )
            spanHelper.toggleBoldSelectedText()*/

        }

        underlinedButton.setOnClickListener {
            isChecked = !isChecked
            spannableString = SpannableString(contentNote.text)
            startSelectionIndex = contentNote.selectionStart
            endSelectionIndex = contentNote.selectionEnd
            val spans = spannableString.getSpans(
                startSelectionIndex,
                endSelectionIndex,
                UnderlineSpan::class.java
            )

            if (spans.isEmpty()) {
                spannableString.setSpan(
                    UnderlineSpan(),
                    startSelectionIndex,
                    endSelectionIndex,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                val toHtml = Html.toHtml(spannableString)
                contentNote.text?.replace(0, contentNote.text!!.length, Html.fromHtml(toHtml))
            } else if (spans.isNotEmpty()) {
                spannableString.removeSpan(spans.first())
                contentNote.setSelection(endSelectionIndex)
                val toHtml = Html.toHtml(spannableString)
                contentNote.text?.replace(0, contentNote.text!!.length, Html.fromHtml(toHtml))
            }
        }


    }
}