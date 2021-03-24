package kz.stepanenkos.notes.listnotes.presentation

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
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
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.extensions.view.gone
import kz.stepanenkos.notes.common.extensions.view.show
import kz.stepanenkos.notes.listnotes.listeners.NoteClickListener
import kz.stepanenkos.notes.listnotes.presentation.view.NoteDetailsLookup
import kz.stepanenkos.notes.listnotes.presentation.view.NoteKeyProvider
import kz.stepanenkos.notes.listnotes.presentation.view.NotesAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotesFragment : Fragment(), NoteClickListener {
    private val notesViewModel: NotesViewModel by viewModel()
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
                    if (tracker.selection.size() > 0) {
                        tracker.clearSelection()
                    } else {
                        requireActivity().onBackPressed()
                    }
                }

            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_notes, container, false)
        recyclerView = root.findViewById(R.id.fragment_notes_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val swapHelper = getSwapHelper()
        swapHelper.attachToRecyclerView(recyclerView)
        recyclerView.adapter = notesAdapter
        toolbar = root.findViewById(R.id.fragment_notes_toolbar)
        checkBoxSelectAllNotes = root.findViewById(R.id.fragment_notes_checkbox_select_all_notes)
        deleteSelectedNotes = root.findViewById(R.id.fragment_notes_image_view_button_delete_note)
        infoCountSelectedNotes = root.findViewById(R.id.fragment_notes_text_view_info_select_item)


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
                        infoCountSelectedNotes.text =
                            "${tracker.selection.size()}/${notesAdapter.currentList.size}"
                    } else {
                        toolbar.gone()
                    }
                }
            })

        checkBoxSelectAllNotes.setOnCheckedChangeListener { buttonView, isChecked ->
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
            builder.setTitle("Удаление заметки")
            builder.setMessage("Вы действительно хотите удалить заметку?")
            builder.setPositiveButton("Да") { dialog, which ->
                val newListNotes = notesAdapter.currentList.toMutableList()
                tracker.selection.forEach {
                    notesViewModel.deleteNote(it)
                    newListNotes.remove(it)
                }

                notesAdapter.submitList(newListNotes)
            }
            builder.setNegativeButton("Нет") { dialog, which ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()

            /*tracker.selection.forEach {
                notesViewModel.deleteNote(it)
                val newListNotes = notesAdapter.currentList.toMutableList()
                newListNotes.remove(it)
                notesAdapter.submitList(newListNotes)
            }*/
        }

        return root
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        notesViewModel.onStart()

        notesViewModel.allNotes.observe(viewLifecycleOwner) {
            notesAdapter.submitList(it)
        }
        if (Firebase.auth.currentUser == null) {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onNoteClick(noteData: NoteData) {
        val bundle = Bundle().apply {
            putString("ID", noteData.id)
        }
        findNavController().navigate(R.id.editorFragment, bundle)
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
                builder.setTitle("Удаление заметки")
                builder.setMessage("Вы действительно хотите удалить заметку?")
                builder.setPositiveButton("Да") { dialog, which ->
                    notesViewModel.deleteNote(notesAdapter.currentList[viewHolder.adapterPosition])
                    notesViewModel.onStart()
                    notesViewModel.allNotes.observe(viewLifecycleOwner) {
                        notesAdapter.submitList(it)
                    }
                }
                builder.setNegativeButton("Нет") { dialog, which ->
                    dialog.dismiss()
                    notesAdapter.notifyItemChanged(viewHolder.adapterPosition)
                }
                val dialog = builder.create()
                dialog.show()
            }
        })
    }
}
