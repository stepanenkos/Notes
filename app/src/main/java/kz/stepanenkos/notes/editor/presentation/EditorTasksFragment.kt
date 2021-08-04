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
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.extensions.view.disabled
import kz.stepanenkos.notes.common.extensions.view.enabled
import kz.stepanenkos.notes.common.model.TaskData
import kz.stepanenkos.notes.common.presentation.ContentEditText
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditorTasksFragment : Fragment() {
    private val editorViewModel: EditorViewModel by viewModel()

    private lateinit var content: ContentEditText
    private lateinit var doneNote: ImageView
    private lateinit var editNote: ImageView

    private var taskData: TaskData = TaskData()
    private var idTask: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isNotBlankTextFields(content) && isEqualsContentInNoteDataAndFields()) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            fillNoteData(content)
                            editorViewModel.saveTask(taskData)
                        }
                        doneNoteUI()

                        Snackbar.make(
                            requireView(),
                            getString(R.string.editor_notes_fragment_note_saved),
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
        val root = inflater.inflate(R.layout.fragment_editor_for_notes, container, false)
        content = root.findViewById(R.id.fragment_editor_content_note)
        doneNote = root.findViewById(R.id.fragment_editor_apply_changed_button)
        editNote = root.findViewById(R.id.fragment_editor_edit_text_button)

        editNote.disabled()
        doneNote.enabled()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        idTask = arguments?.getString("ID")
        idTask?.let { taskById ->
            editNote.enabled()
            doneNote.disabled()
            editorViewModel.getTaskById(taskById)
        }

        editorViewModel.taskById.observe(viewLifecycleOwner, ::showNote)

        editorViewModel.errorReceiving.observe(viewLifecycleOwner, ::showError)

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        doneNote.setOnClickListener {
            if (!isNotBlankTextFields(content)) {
                Snackbar.make(
                    requireView(),
                    getString(R.string.editor_notes_fragment_cannot_save_empty_note),
                    Snackbar.LENGTH_LONG
                ).show()
            }

            if (isNotBlankTextFields(content)) {

                fillNoteData(content)

                CoroutineScope(Dispatchers.IO).launch {
                    editorViewModel.saveTask(taskData)
                }

                doneNoteUI()

                Snackbar.make(
                    requireView(),
                    getString(R.string.editor_notes_fragment_note_saved),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        editNote.setOnClickListener {
            editNoteUI()
        }
    }

    private fun doneNoteUI() {
        content.disabled()
        doneNote.disabled()
        editNote.enabled()
    }

    private fun editNoteUI() {
        content.enabled()
        doneNote.enabled()
        editNote.disabled()
    }
    private fun fillNoteData(contentTask: EditText) {
        if(isNotBlankTextFields(contentTask)) {
            taskData.contentTask = contentTask.text.toString()
        }
    }

    private fun isNotBlankTextFields(contentTask: EditText): Boolean {
        return contentTask.text.isNotBlank() && contentTask.text.isNotBlank()
    }

    private fun isEqualsContentInNoteDataAndFields(): Boolean {
        return taskData.contentTask == content.text.toString()
    }

    private fun showNote(taskData: TaskData?) {
        if (taskData != null) {
            this.taskData = taskData
            content.disabled()
            content.setText(taskData.contentTask.trim())
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
