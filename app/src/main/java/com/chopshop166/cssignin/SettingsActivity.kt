package com.chopshop166.cssignin

import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager.commit {
            replace(R.id.settings, SettingsFragment())
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            setHasOptionsMenu(true)

            val listener = EditTextPreference.OnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                editText.setSelectAllOnFocus(true)
                editText.setSingleLine()
                editText.maxLines = 1
            }

            findPreference<EditTextPreference>("firstname_text")?.setOnBindEditTextListener(listener)
            findPreference<EditTextPreference>("lastname_text")?.setOnBindEditTextListener(listener)
        }
    }
}