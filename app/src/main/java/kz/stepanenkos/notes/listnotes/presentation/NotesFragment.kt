package kz.stepanenkos.notes.listnotes.presentation

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.listnotes.listeners.NoteClickListener
import kz.stepanenkos.notes.searchnotes.presentation.view.SearchNotesAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotesFragment : Fragment(), NoteClickListener {
    private val notesViewModel: NotesViewModel by viewModel()
    private lateinit var recyclerView: RecyclerView
    private val notesAdapter = SearchNotesAdapter(this)

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

        return root
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
                val oldPosition = viewHolder.oldPosition
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Удаление заметки")
                builder.setMessage("Вы действительно хотите удалить заметку?")
                builder.setPositiveButton("Да") {dialog, which ->
                    notesViewModel.deleteNote(notesAdapter.currentList[viewHolder.adapterPosition])
                    notesViewModel.onStart()
                    notesViewModel.allNotes.observe(viewLifecycleOwner) {
                        notesAdapter.submitList(it)
                    }
                }
                builder.setNegativeButton("Нет") {dialog, which ->
                    dialog.dismiss()
                    notesAdapter.notifyItemChanged(viewHolder.adapterPosition)
                }
                val dialog = builder.create()
                dialog.show()
            }
        })
    }

    override fun onNoteClick(noteData: NoteData) {
        val bundle = Bundle().apply {
            putString("ID", noteData.id)
        }
        findNavController().navigate(R.id.editorFragment, bundle)
    }

}
