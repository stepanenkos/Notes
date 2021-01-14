package kz.stepanenkos.notes.editor.presentation

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.presentation.AbstractTextWatcher
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class EditorFragment : Fragment() {
    private val editorViewModel: EditorViewModel by viewModel()

    private lateinit var titleNote: EditText
    private lateinit var contentNote: EditText
    private lateinit var doneNote: ImageView
    private lateinit var editNote: ImageView
    private lateinit var boldButton: ImageView
    private lateinit var underlinedButton: ImageView

    private var noteData: NoteData = NoteData()
    private var isChecked = false
    private var isForEdit = false
    private var startSelectionIndex: Int = -1
    private var endSelectionIndex: Int = -1
    private var isBold = false
    private var spannableString: SpannableString = SpannableString("")

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getLong("ID")?.let { editorViewModel.getNoteById(it) }
        editorViewModel.noteById.observe(viewLifecycleOwner, {
            isForEdit = true
            noteData = it
            titleNote.isEnabled = false
            contentNote.isEnabled = false
            titleNote.setText(it.titleNote)
            contentNote.setText(Html.fromHtml(it.contentNote))
        })
        contentNote.addTextChangedListener(object : AbstractTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                if (isBold) {
                    contentNote.text.setSpan(
                        StyleSpan(Typeface.BOLD),
                        0,
                        contentNote.text.length,
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                    )
                    var leftSpan: StyleSpan? = null
                    val leftSpans = contentNote.text.getSpans(contentNote.selectionStart, contentNote.selectionStart, StyleSpan::class.java)
                    if (leftSpans.isNotEmpty()) {
                        leftSpan = leftSpans[0]
                    }
                    var rightSpan: StyleSpan? = null
                    val rightSpans = contentNote.text.getSpans(contentNote.selectionEnd, contentNote.selectionEnd, StyleSpan::class.java)
                    if (rightSpans.isNotEmpty()) {
                        rightSpan = rightSpans[0]
                    }
                    val leftSpanStart = contentNote.text.getSpanStart(leftSpan)
                    val rightSpanEnd = contentNote.text.getSpanEnd(rightSpan)
                    Log.d("TAG", "afterTextChanged: if getSpanStart $leftSpanStart")
                    Log.d("TAG", "afterTextChanged: if getSpanEnd $rightSpanEnd")
                    Log.d("TAG", "afterTextChanged: if getSpans ${contentNote.text.getSpans(0, contentNote.text.length, StyleSpan::class.java)}")
                } else {
                    var leftSpan: StyleSpan? = null
                    val leftSpans = contentNote.text.getSpans(contentNote.selectionStart, contentNote.selectionStart, StyleSpan::class.java)
                    if (leftSpans.isNotEmpty()) {
                        leftSpan = leftSpans[0]
                    }
                    var rightSpan: StyleSpan? = null
                    val rightSpans = contentNote.text.getSpans(contentNote.selectionEnd, contentNote.selectionEnd, StyleSpan::class.java)
                    if (rightSpans.isNotEmpty()) {
                        rightSpan = rightSpans[0]
                    }
                    val leftSpanStart = contentNote.text.getSpanStart(leftSpan)
                    val rightSpanEnd = contentNote.text.getSpanEnd(rightSpan)
                    Log.d("TAG", "afterTextChanged: else getSpanStart $leftSpanStart")
                    Log.d("TAG", "afterTextChanged: else getSpanEnd $rightSpanEnd")
                    Log.d("TAG", "afterTextChanged: else getSpans ${contentNote.text.getSpans(0, contentNote.text.length, StyleSpan::class.java)}")
                    contentNote.text.removeSpan(
                        contentNote.text.getSpans(
                            rightSpanEnd,
                            rightSpanEnd,
                            StyleSpan::class.java
                        )[0]
                    )
                }
            }
        })
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        doneNote.setOnClickListener {
            if (titleNote.text.toString().isNotBlank() &&
                contentNote.text.toString().isNotBlank() && !isForEdit
            ) {
                editorViewModel.addNote(
                    titleNote = titleNote.text.toString(),
                    contentNote = Html.toHtml(contentNote.text)
                )
                titleNote.isEnabled = false
                contentNote.isEnabled = false
            } else {

                noteData.titleNote = titleNote.text.toString()
                noteData.contentNote = Html.toHtml(contentNote.text)
                noteData.dateOfNote = ZonedDateTime.now(
                    ZoneId.of(
                        ZoneId.systemDefault().rules.getOffset(
                            Instant.now()
                        ).toString()
                    )
                )
                editorViewModel.updateNote(noteData)
                titleNote.isEnabled = false
                contentNote.isEnabled = false
                isForEdit = false
            }

        }

        editNote.setOnClickListener {

            titleNote.isEnabled = true

            contentNote.isEnabled = true

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
                contentNote.text.replace(0, contentNote.text.length, Html.fromHtml(toHtml))
            } else if (spans.isNotEmpty()) {
                spannableString.removeSpan(spans.first())
                contentNote.setSelection(endSelectionIndex)
                val toHtml = Html.toHtml(spannableString)
                contentNote.text.replace(0, contentNote.text.length, Html.fromHtml(toHtml))
            }
        }


    }
}