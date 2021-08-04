package kz.stepanenkos.notes.listnotes.presentation

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
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
import kz.stepanenkos.notes.common.model.NoteData
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.extensions.view.gone
import kz.stepanenkos.notes.common.extensions.view.show
import kz.stepanenkos.notes.databinding.FragmentNotesBinding
import kz.stepanenkos.notes.listnotes.listeners.NoteClickListener
import kz.stepanenkos.notes.listnotes.presentation.view.NoteDetailsLookup
import kz.stepanenkos.notes.listnotes.presentation.view.NoteKeyProvider
import kz.stepanenkos.notes.listnotes.presentation.view.NotesAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotesFragment : Fragment(R.layout.fragment_notes), NoteClickListener {
    private val notesViewModel: NotesViewModel by viewModel()
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private val notesAdapter = NotesAdapter(this)

    private lateinit var tracker: SelectionTracker<NoteData>
    private lateinit var toolbar: MaterialToolbar
    private lateinit var checkBoxSelectAllNotes: MaterialCheckBox
    private lateinit var deleteSelectedNotes: ImageView
    private lateinit var infoCountSelectedNotes: MaterialTextView

    private val NOTE_ID = "ID"

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
        _binding = FragmentNotesBinding.bind(view)
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
                tracker.selection.forEach { noteData ->
                    notesViewModel.deleteNote(noteData)
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
        notesViewModel.onStart()

        notesViewModel.allNotes.observe(viewLifecycleOwner) {
            notesAdapter.submitList(it)
        }

        notesViewModel.errorWhileGettingNotes.observe(viewLifecycleOwner, ::showError)
        if (Firebase.auth.currentUser == null) {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onNoteClick(noteData: NoteData) {
        val bundle = Bundle().apply {
            putString(NOTE_ID, noteData.id)
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
                builder.setNegativeButton(getString(R.string.negative_button_text)) { dialog, which ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
