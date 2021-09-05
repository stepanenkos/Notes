package kz.stepanenkos.notes.mainscreen.presentation.view

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import kz.stepanenkos.notes.listnotes.presentation.NotesFragment
import kz.stepanenkos.notes.listtasks.presentation.TasksFragment

class MainAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> NotesFragment()
            1 -> TasksFragment()
            else -> NotesFragment()
        }
    }

}