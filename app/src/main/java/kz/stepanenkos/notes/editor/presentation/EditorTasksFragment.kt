package kz.stepanenkos.notes.editor.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
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
import kz.stepanenkos.notes.common.model.TaskData
import kz.stepanenkos.notes.common.presentation.ContentEditText
import kz.stepanenkos.notes.databinding.FragmentEditorTasksBinding
import kz.stepanenkos.notes.listtasks.presentation.TASK_ID
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditorTasksFragment : Fragment(R.layout.fragment_editor_tasks) {
    private val editorViewModel: EditorViewModel by viewModel()
    private var binding: FragmentEditorTasksBinding? = null

    private lateinit var contentTask: ContentEditText
    private lateinit var doneNote: ImageView
    private lateinit var editNote: ImageView

    private var taskData: TaskData? = null
    private var idTask: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (idTask != null && isEqualsContentInTaskDataAndFields()) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    } else if (idTask != null && !isEqualsContentInTaskDataAndFields()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            taskData?.copy(
                                contentTask = contentTask.text.toString()
                            )?.let {
                                editorViewModel.updateTask(
                                    it
                                )
                            }
                        }
                        isEnabled = false
                        requireActivity().onBackPressed()
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            editorViewModel.saveTask(contentTask = contentTask.text.toString())
                        }
                        doneTaskUI()

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
        binding = FragmentEditorTasksBinding.bind(view)
        contentTask = binding!!.fragmentEditorContentTask
        doneNote = binding!!.fragmentEditorTasksApplyChangedButton
        editNote = binding!!.fragmentEditorTasksEditTextButton

        editNote.disabled()
        doneNote.enabled()

        idTask = arguments?.getString(TASK_ID)
        idTask?.let { taskById ->
            editNote.enabled()
            doneNote.disabled()
            editorViewModel.getTaskById(taskById)
        }

        editorViewModel.taskById.observe(viewLifecycleOwner, ::showTask)

        editorViewModel.errorReceiving.observe(viewLifecycleOwner, ::showError)

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        doneNote.setOnClickListener {
            if (contentTask.text.toString().isBlank()) {
                Snackbar.make(
                    requireView(),
                    getString(R.string.editor_tasks_fragment_cannot_save_empty_task),
                    Snackbar.LENGTH_LONG
                ).show()
            } else if(taskData != null && !isEqualsContentInTaskDataAndFields()) {
                CoroutineScope(Dispatchers.IO).launch {
                    taskData?.copy(
                        contentTask = contentTask.text.toString()
                    )?.let {
                        editorViewModel.updateTask(
                            it
                        )
                    }
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                editorViewModel.saveTask(contentTask.text.toString())
            }

            doneTaskUI()

            Snackbar.make(
                requireView(),
                getString(R.string.editor_tasks_fragment_task_saved),
                Snackbar.LENGTH_LONG
            ).show()
        }

        editNote.setOnClickListener {
            editTaskUI()
        }
    }

    private fun doneTaskUI() {
        contentTask.disabled()
        doneNote.disabled()
        editNote.enabled()
    }

    private fun editTaskUI() {
        contentTask.enabled()
        doneNote.enabled()
        editNote.disabled()
    }

    private fun isEqualsContentInTaskDataAndFields(): Boolean {
        return taskData?.contentTask == contentTask.text.toString()
    }

    private fun showTask(taskData: TaskData?) {
        if (taskData != null) {
            this.taskData = taskData
            contentTask.disabled()
            contentTask.setText(taskData.contentTask.trim())
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
