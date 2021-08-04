package kz.stepanenkos.notes.mainscreen.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.databinding.FragmentMainBinding
import kz.stepanenkos.notes.listnotes.presentation.NotesFragment
import kz.stepanenkos.notes.listtasks.presentation.TasksFragment
import kz.stepanenkos.notes.mainscreen.presentation.view.MainAdapter


class MainFragment : Fragment(R.layout.fragment_main) {
    private var _binding: FragmentMainBinding? = null

    private lateinit var mainAdapter: MainAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)
        mainAdapter = MainAdapter(this)
        _binding!!.fragmentMainViewPager.adapter = mainAdapter
        TabLayoutMediator(_binding!!.fragmentMainTabLayout, _binding!!.fragmentMainViewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.main_fragment_tablayout_note)
                }
                1 -> tab.text = getString(R.string.main_fragment_tablayout_task)
            }
        }.attach()

    }
}