package kz.stepanenkos.notes.editor.presentation

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
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
import java.util.*

class EditorTasksFragment : Fragment(R.layout.fragment_editor_tasks) {
    private val editorViewModel: EditorViewModel by viewModel()
    private lateinit var binding: FragmentEditorTasksBinding

    private lateinit var contentTask: ContentEditText
    private lateinit var doneTaskButton: ImageView
    private lateinit var editTaskButton: ImageView
    private lateinit var setANotificationCheckBox: CheckBox
    private lateinit var informationAboutNotificationTextView: TextView

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
                            .isBlank() || isSave || (taskData != null && isEqualsFieldsInTaskDataAndFields()) -> {
                            isEnabled = false
                            requireActivity().onBackPressed()
                        }
                        taskData == null && contentTask.text.toString().isNotBlank() && !isSave -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                editorViewModel.saveTask(
                                    contentTask.text.toString(),
                                    setANotificationCheckBox.isChecked
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

                        taskData != null && !isEqualsFieldsInTaskDataAndFields() && !isSave -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                taskData?.copy(
                                    contentTask = contentTask.text.toString(),
                                    notificationOn = setANotificationCheckBox.isChecked
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
        setANotificationCheckBox = binding.fragmentEditorTasksCheckboxSetANotification
        informationAboutNotificationTextView =
            binding.fragmentEditorTasksTextViewInformationAboutNotification

        editTaskButton.disabled()
        doneTaskButton.enabled()

        idTask = arguments?.getString(TASK_ID)
        idTask?.let { taskById ->
            editTaskButton.enabled()
            doneTaskButton.disabled()
            setANotificationCheckBox.disabled()

            editorViewModel.getTaskById(taskById)
            isSave = false
        }

        editorViewModel.taskById.observe(viewLifecycleOwner, ::showTask)

        editorViewModel.errorReceiving.observe(viewLifecycleOwner, ::showError)

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        setANotificationCheckBox.setOnCheckedChangeListener { compoundButton, checked ->
            if(checked && setANotificationCheckBox.isEnabled) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(requireContext(), { view, selectedYear, monthOfYear, dayOfMonth ->


                }, year, month, day)

                dpd.show()
            }
        }
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
                        editorViewModel.saveTask(
                            contentTask.text.toString(),
                            setANotificationCheckBox.isChecked
                        )
                    }
                    Snackbar.make(
                        requireView(),
                        getString(R.string.editor_notes_fragment_note_saved),
                        Snackbar.LENGTH_LONG
                    ).show()
                    doneTaskUI()
                    isSave = true
                }

                taskData != null && !isEqualsFieldsInTaskDataAndFields() -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        taskData?.copy(
                            contentTask = contentTask.text.toString(),
                            notificationOn = setANotificationCheckBox.isChecked
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
        setANotificationCheckBox.disabled()
    }

    private fun editTaskUI() {
        contentTask.enabled()
        doneTaskButton.enabled()
        editTaskButton.disabled()
        setANotificationCheckBox.enabled()
    }

    private fun isEqualsFieldsInTaskDataAndFields(): Boolean {
        return taskData?.contentTask == contentTask.text.toString() && taskData?.notificationOn == setANotificationCheckBox.isChecked
    }

    private fun showTask(taskData: TaskData?) {
        if (taskData != null) {
            this.taskData = taskData
            setANotificationCheckBox.isChecked = taskData.notificationOn
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
