package com.chopshop166.cssignin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            setHasOptionsMenu(true)

            val firstnamePreference = findPreference<EditTextPreference>("firstname_text")
            firstnamePreference?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()

            val lastnamePreference = findPreference<EditTextPreference>("lastname_text")
            lastnamePreference?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        }
    }
}