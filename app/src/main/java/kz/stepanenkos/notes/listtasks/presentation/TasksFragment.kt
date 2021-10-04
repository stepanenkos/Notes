package kz.stepanenkos.notes.listtasks.presentation

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.ktx.Firebase
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.model.TaskData
import kz.stepanenkos.notes.common.extensions.view.gone
import kz.stepanenkos.notes.common.extensions.view.show
import kz.stepanenkos.notes.databinding.FragmentTasksBinding
import kz.stepanenkos.notes.listtasks.presentation.view.TaskDetailsLookup
import kz.stepanenkos.notes.listtasks.presentation.view.TaskKeyProvider
import kz.stepanenkos.notes.listtasks.listeners.TaskClickListener
import kz.stepanenkos.notes.listtasks.presentation.view.TasksAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
const val TASK_ID = "TASK_ID"
class TasksFragment : Fragment(R.layout.fragment_tasks), TaskClickListener {
    private val tasksViewModel: TasksViewModel by viewModel()
    private lateinit var binding: FragmentTasksBinding

    private lateinit var recyclerView: RecyclerView
    private val tasksAdapter = TasksAdapter(this)

    private lateinit var tracker: SelectionTracker<TaskData>
    private lateinit var toolbar: MaterialToolbar
    private lateinit var checkBoxSelectAllTasks: MaterialCheckBox
    private lateinit var deleteSelectedTasks: ImageView
    private lateinit var infoCountSelectedTasks: MaterialTextView



    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (tracker.selection.size() == 0) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    } else {
                        tracker.clearSelection()
                    }
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTasksBinding.bind(view)
        recyclerView = binding.fragmentTasksRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        getSwapHelper().attachToRecyclerView(recyclerView)

        recyclerView.adapter = tasksAdapter
        toolbar = binding.fragmentTasksToolbar
        checkBoxSelectAllTasks = binding.fragmentTasksCheckboxSelectAllTasks
        deleteSelectedTasks = binding.fragmentTasksImageViewButtonDeleteTask
        infoCountSelectedTasks = binding.fragmentTasksTextViewInfoSelectItem

        tracker = SelectionTracker.Builder(
            "mySelection",
            recyclerView,
            TaskKeyProvider(tasksAdapter),
            TaskDetailsLookup(recyclerView),
            StorageStrategy.createParcelableStorage(TaskData::class.java)
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        tasksAdapter.setTracker(tracker)
        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<TaskData>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    if (tracker.selection.size() > 0) {
                        toolbar.show()
                        val countSelectedTasksText =
                            "${tracker.selection.size()}/${tasksAdapter.currentList.size}"
                        infoCountSelectedTasks.text = countSelectedTasksText
                    } else {
                        toolbar.gone()
                    }
                }
            })

        checkBoxSelectAllTasks.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tasksAdapter.currentList.forEach {
                    if (!tracker.isSelected(it)) {
                        tracker.select(it)
                    }
                }
            } else {
                tracker.clearSelection()
            }
        }

        deleteSelectedTasks.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.tasks_fragment_title_delete_task))
            builder.setMessage(getString(R.string.tasks_fragment_question_delete_task))
            builder.setPositiveButton(getString(R.string.positive_button_text)) { _, _ ->
                tracker.selection.forEach { taskData ->
                    tasksViewModel.deleteTask(taskData)
                }
            }
            builder.setNegativeButton(getString(R.string.negative_button_text)) { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
        }
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        tasksViewModel.onStart()

        tasksViewModel.allNotes.observe(viewLifecycleOwner) {
            tasksAdapter.submitList(it)
        }

        tasksViewModel.errorWhileGettingNotes.observe(viewLifecycleOwner, ::showError)
        if (Firebase.auth.currentUser == null) {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onTaskClick(taskData: TaskData) {
        val bundle = Bundle().apply {
            putInt(TASK_ID, taskData.id)
        }
        findNavController().navigate(R.id.editorTasksFragment, bundle)
    }

    override fun onCheckedTask(taskData: TaskData, checked: Boolean) {
        when(checked) {
            true -> {
                tasksViewModel.updateTask(
                    taskData.copy(doneTask = true)
                )
            }
            false -> {
                tasksViewModel.updateTask(
                    taskData.copy(doneTask = false)
                )
            }
        }
    }

    private fun getSwapHelper(): ItemTouchHelper {
        return ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.notes_fragment_title_delete_note))
                builder.setMessage(getString(R.string.notes_fragment_question_delete_note))
                builder.setPositiveButton(getString(R.string.positive_button_text)) { _, _ ->
                    tasksViewModel.deleteTask(tasksAdapter.currentList[viewHolder.adapterPosition])
                }
                builder.setNegativeButton(getString(R.string.negative_button_text)) { dialog, _ ->
                    dialog.dismiss()
                    tasksAdapter.notifyItemChanged(viewHolder.adapterPosition)
                }
                builder.create().show()
            }
        })
    }

    private fun showError(error: FirebaseFirestoreException) {
        Snackbar.make(requireView(), error.localizedMessage, Snackbar.LENGTH_LONG).show()
    }
}
