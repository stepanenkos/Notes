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
    private lateinit var binding: FragmentEditorTasksBinding

    private lateinit var contentTask: ContentEditText
    private lateinit var doneTaskButton: ImageView
    private lateinit var editTaskButton: ImageView

    private var taskData: TaskData? = null
    private var idTask: String? = null
    private var isSave: Boolean = false
    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when {
                        contentTask.text.toString()
                            .isBlank() || isSave || (taskData != null && isEqualsContentInTaskDataAndFields()) -> {
                            isEnabled = false
                            requireActivity().onBackPressed()
                        }
                        taskData == null && contentTask.text.toString().isNotBlank() && !isSave -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                editorViewModel.saveTask(
                                    contentTask.text.toString()
                                )
                            }
                            Snackbar.make(
                                requireView(),
                                getString(R.string.editor_tasks_fragment_task_saved),
                                Snackbar.LENGTH_LONG
                            ).show()
                            doneTaskUI()
                            isSave = true
                        }

                        taskData != null && !isEqualsContentInTaskDataAndFields() && !isSave -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                taskData?.copy(
                                    contentTask = contentTask.text.toString()
                                )?.let {
                                    editorViewModel.updateTask(
                                        it
                                    )
                                }
                            }
                            Snackbar.make(
                                requireView(),
                                getString(R.string.editor_tasks_fragment_task_saved),
                                Snackbar.LENGTH_LONG
                            ).show()
                            doneTaskUI()
                            isSave = true
                        }
                    }
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditorTasksBinding.bind(view)
        contentTask = binding.fragmentEditorContentTask
        doneTaskButton = binding.fragmentEditorTasksApplyChangedButton
        editTaskButton = binding.fragmentEditorTasksEditTextButton

        editTaskButton.disabled()
        doneTaskButton.enabled()

        idTask = arguments?.getString(TASK_ID)
        idTask?.let { taskById ->
            editTaskButton.enabled()
            doneTaskButton.disabled()
            editorViewModel.getTaskById(taskById)
            isSave = false
        }

        editorViewModel.taskById.observe(viewLifecycleOwner, ::showTask)

        editorViewModel.errorReceiving.observe(viewLifecycleOwner, ::showError)

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        doneTaskButton.setOnClickListener {
            when {
                contentTask.text.toString().isBlank() -> {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.editor_tasks_fragment_cannot_save_empty_task),
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                taskData == null && contentTask.text.toString().isNotBlank() -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        editorViewModel.saveTask(contentTask.text.toString())
                    }
                    Snackbar.make(
                        requireView(),
                        getString(R.string.editor_notes_fragment_note_saved),
                        Snackbar.LENGTH_LONG
                    ).show()
                    doneTaskUI()
                    isSave = true
                }

                taskData != null && !isEqualsContentInTaskDataAndFields() -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        taskData?.copy(
                            contentTask.text.toString()
                        )?.let {
                            editorViewModel.updateTask(
                                it
                            )
                        }
                    }
                    Snackbar.make(
                        requireView(),
                        getString(R.string.editor_notes_fragment_note_saved),
                        Snackbar.LENGTH_LONG
                    ).show()
                    doneTaskUI()
                    isSave = true
                }
            }
        }

        editTaskButton.setOnClickListener {
            editTaskUI()
        }
    }

    private fun doneTaskUI() {
        contentTask.disabled()
        doneTaskButton.disabled()
        editTaskButton.enabled()
    }

    private fun editTaskUI() {
        contentTask.enabled()
        doneTaskButton.enabled()
        editTaskButton.disabled()
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
