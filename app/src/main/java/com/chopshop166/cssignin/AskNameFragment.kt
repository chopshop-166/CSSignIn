package com.chopshop166.cssignin

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.chopshop166.cssignin.databinding.DialogNameBinding


class AskNameFragment : DialogFragment() {

    private var _binding: DialogNameBinding? = null

    // This property is only valid between onCreateDialog and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.let {
            _binding = DialogNameBinding.inflate(LayoutInflater.from(it))
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
                .setTitle(R.string.enter_name_text)
                .setPositiveButton(R.string.ok_text) { _, _ ->
                    val prefs = PreferenceManager.getDefaultSharedPreferences(it.applicationContext)
                    prefs.edit {
                        putString("firstname_text", binding.dialogFirstNameEt.text.toString())
                        putString("lastname_text", binding.dialogLastNameEt.text.toString())
                    }
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}