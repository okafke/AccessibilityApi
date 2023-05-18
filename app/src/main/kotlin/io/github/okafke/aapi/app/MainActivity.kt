package io.github.okafke.aapi.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import io.github.okafke.aapi.app.aidl.NavigationTreeService
import io.github.okafke.aapi.app.util.FileHelper


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val goto = findPreference<Preference>(getString(R.string.goto_accessibility_key))
            goto!!.setOnPreferenceClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                true
            }

            val clearData = findPreference<Preference>(getString(R.string.clear_data_key))
            clearData!!.setOnPreferenceClickListener {
                FileHelper.deleteFile(requireContext(), NavigationTreeService.FILE_NAME)
                FileHelper.deleteFile(requireContext(), AppManager.FILE_NAME)
                true
            }
        }
    }

}