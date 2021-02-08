package kz.stepanenkos.notes.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.core.content.edit
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import kz.stepanenkos.notes.R

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        PreferenceManager.getDefaultSharedPreferences(requireContext()).registerOnSharedPreferenceChangeListener(this)
        val titleNoteFontSizePreference = preferenceManager.findPreference<ListPreference>("title_note_font_size")
        val contentNoteFontSizePreference = preferenceManager.findPreference<ListPreference>("content_note_font_size")
        titleNoteFontSizePreference?.setDefaultValue(titleNoteFontSizePreference.entries[2])
        contentNoteFontSizePreference?.setDefaultValue(contentNoteFontSizePreference.entries[2])
/*        Log.d("TAG", "entries: ${Arrays.toString(fontSizePreference?.entries)}")
        Log.d("TAG", "entry values: ${
            Arrays.toString(fontSizePreference?.entryValues)}")
        Log.d("TAG", "entry: ${fontSizePreference?.entry}")
        Log.d("TAG", "indexOfValue: ${fontSizePreference?.findIndexOfValue(fontSizePreference.value)}")
        Log.d("TAG", "value: ${fontSizePreference?.value}")*/
        PreferenceManager.getDefaultSharedPreferences(requireContext()).edit {

            titleNoteFontSizePreference?.value?.let { putString("title_note_font_size", it) }
            contentNoteFontSizePreference?.value?.let { putString("content_note_font_size", it) }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        /* activity?.finish()
        activity?.overridePendingTransition(R.anim.none, R.anim.none)
        val intent = activity?.intent
        intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)*/
        activity?.recreate()
    }


}