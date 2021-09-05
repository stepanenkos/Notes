package kz.stepanenkos.notes.mainscreen.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.databinding.FragmentMainBinding
import kz.stepanenkos.notes.mainscreen.presentation.view.MainAdapter


class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var binding: FragmentMainBinding

    private lateinit var mainAdapter: MainAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)
        mainAdapter = MainAdapter(this)
        binding.fragmentMainViewPager.adapter = mainAdapter
        TabLayoutMediator(binding.fragmentMainTabLayout, binding.fragmentMainViewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.main_fragment_tablayout_note)
                }
                1 -> tab.text = getString(R.string.main_fragment_tablayout_task)
            }
        }.attach()

    }
}