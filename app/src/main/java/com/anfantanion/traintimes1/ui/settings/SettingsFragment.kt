package com.anfantanion.traintimes1.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import androidx.navigation.findNavController
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.anfantanion.traintimes1.R


class SettingsFragment : PreferenceFragmentCompat(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback, SharedPreferences.OnSharedPreferenceChangeListener  {

    private lateinit var settingsViewModel: SettingsViewModel
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferencescreen,rootKey)

        preferenceManager.findPreference<EditTextPreference>("default_changetime")!!.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        setRefreshIntervalEnabled(preferenceManager.sharedPreferences.getBoolean("automatic_refresh_enable",false))
        setNotifyEnabled(preferenceManager.sharedPreferences.getBoolean("notify_change_enable",false))

        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

    }

    fun setRefreshIntervalEnabled(boolean: Boolean){
        preferenceManager.findPreference<EditTextPreference>("refresh_every")!!.isEnabled = boolean
        preferenceManager.findPreference<EditTextPreference>("refresh_every")!!.isVisible = boolean
    }

    fun setNotifyEnabled(boolean: Boolean){
        preferenceManager.findPreference<EditTextPreference>("notify_change_time")!!.isEnabled = boolean
        preferenceManager.findPreference<EditTextPreference>("notify_change_time")!!.isVisible = boolean
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat?,
        pref: Preference?
    ): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when(key){
            "automatic_refresh_enable" -> {
                val value = PreferenceManager.getDefaultSharedPreferences(this.context).getBoolean(key,false)
                setRefreshIntervalEnabled(value)
            }
            "notify_change_enable" -> {
                val value = PreferenceManager.getDefaultSharedPreferences(this.context).getBoolean(key,false)
                setNotifyEnabled(value)
            }
        }
    }
}