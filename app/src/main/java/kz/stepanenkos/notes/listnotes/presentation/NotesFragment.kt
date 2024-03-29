package kz.stepanenkos.notes.listnotes.presentation

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.extensions.view.gone
import kz.stepanenkos.notes.common.extensions.view.show
import kz.stepanenkos.notes.common.model.NoteData
import kz.stepanenkos.notes.databinding.FragmentNotesBinding
import kz.stepanenkos.notes.listnotes.listeners.NoteClickListener
import kz.stepanenkos.notes.listnotes.presentation.view.NoteDetailsLookup
import kz.stepanenkos.notes.listnotes.presentation.view.NoteKeyProvider
import kz.stepanenkos.notes.listnotes.presentation.view.NotesAdapter
import org.koin.android.ext.android.inject

const val NOTE_ID = "NOTE_ID"

class NotesFragment : Fragment(R.layout.fragment_notes), NoteClickListener {
    private val notesViewModel: NotesViewModel by inject()
    private lateinit var binding: FragmentNotesBinding

    private lateinit var recyclerView: RecyclerView
    private val notesAdapter = NotesAdapter(this)

    private lateinit var tracker: SelectionTracker<NoteData>
    private lateinit var toolbar: MaterialToolbar
    private lateinit var checkBoxSelectAllNotes: MaterialCheckBox
    private lateinit var deleteSelectedNotes: ImageView
    private lateinit var infoCountSelectedNotes: MaterialTextView


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
        binding = FragmentNotesBinding.bind(view)

        recyclerView = binding.fragmentNotesRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        getSwapHelper().attachToRecyclerView(recyclerView)

        recyclerView.adapter = notesAdapter
        toolbar = binding.fragmentNotesToolbar
        checkBoxSelectAllNotes = binding.fragmentNotesCheckboxSelectAllNotes
        deleteSelectedNotes = binding.fragmentNotesImageViewButtonDeleteNote
        infoCountSelectedNotes = binding.fragmentNotesTextViewInfoSelectItem

        tracker = SelectionTracker.Builder(
            "mySelection",
            recyclerView,
            NoteKeyProvider(notesAdapter),
            NoteDetailsLookup(recyclerView),
            StorageStrategy.createParcelableStorage(NoteData::class.java)
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        notesAdapter.setTracker(tracker)
        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<NoteData>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    if (tracker.selection.size() > 0) {
                        toolbar.show()
                        val countSelectedNotesText =
                            "${tracker.selection.size()}/${notesAdapter.currentList.size}"
                        infoCountSelectedNotes.text = countSelectedNotesText
                    } else {
                        toolbar.gone()
                    }
                }
            })

        checkBoxSelectAllNotes.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                notesAdapter.currentList.forEach {
                    if (!tracker.isSelected(it)) {
                        tracker.select(it)
                    }
                }
            } else {
                tracker.clearSelection()
            }
        }

        deleteSelectedNotes.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.notes_fragment_title_delete_note))
            builder.setMessage(getString(R.string.notes_fragment_question_delete_note))
            builder.setPositiveButton(getString(R.string.positive_button_text)) { _, _ ->
                notesViewModel.deleteNote(tracker.selection.toList())
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
        notesViewModel.onStart()

        lifecycleScope.launchWhenStarted {
            notesViewModel.allNotes
                .onEach { listNoteData ->
                    notesAdapter.submitList(listNoteData)
                }.launchIn(lifecycleScope)

            notesViewModel.errorWhileGettingNotes
                .onEach {firebaseFirestoreException ->
                    showError(firebaseFirestoreException)
                }.launchIn(lifecycleScope)
        }


        if (Firebase.auth.currentUser == null) {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onNoteClick(noteData: NoteData) {
        val bundle = Bundle().apply {
            putInt(NOTE_ID, noteData.id)
        }
        findNavController().navigate(R.id.editorNotesFragment, bundle)
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
                    notesViewModel.deleteNote(notesAdapter.currentList[viewHolder.adapterPosition])
                }
                builder.setNegativeButton(getString(R.string.negative_button_text)) { dialog, _ ->
                    dialog.dismiss()
                    notesAdapter.notifyItemChanged(viewHolder.adapterPosition)
                }
                builder.create().show()
            }
        })
    }

    private fun showError(error: FirebaseFirestoreException) {
        Snackbar.make(requireView(), error.localizedMessage, Snackbar.LENGTH_LONG).show()
    }
}
