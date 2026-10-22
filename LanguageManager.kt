package com.example.e_comm.util

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.e_comm.R

/**
 * Handles in-app language switching (English, isiZulu, Afrikaans).
 * AppCompatDelegate.setApplicationLocales() applies the language app-wide
 * and recreates the current screen so the new strings load immediately.
 */
object LanguageManager {

    private const val TAG = "LanguageManager"

    // Order must match the items in R.array.language_names
    private val languageTags = listOf("en", "zu", "af")

    fun setupSpinner(context: Context, spinner: Spinner) {
        val adapter = ArrayAdapter.createFromResource(
            context, R.array.language_names, android.R.layout.simple_spinner_item
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spinner.adapter = adapter

        val currentTag = currentLanguageTag()
        val currentIndex = languageTags.indexOf(currentTag).takeIf { it >= 0 } ?: 0
        // 'false' stops this initial selection from triggering a language change
        spinner.setSelection(currentIndex, false)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val chosen = languageTags[position]
                if (chosen != currentLanguageTag()) {
                    Log.d(TAG, "Switching language to $chosen")
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(chosen))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun currentLanguageTag(): String {
        val tags = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        return if (tags.isEmpty()) "en" else tags.substringBefore("-").substringBefore(",")
    }
}