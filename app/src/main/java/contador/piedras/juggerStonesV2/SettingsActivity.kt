package contador.piedras.juggerStonesV2

import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.*
import java.util.*
import android.content.Intent
import org.jetbrains.anko.toast

class SettingsActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Load setting fragment
        fragmentManager.beginTransaction().replace(android.R.id.content,
                MainSettingsFragment()).commit()
    }

    class MainSettingsFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
            bindSummaryValue(findPreference("stonesMaxValue"))
            bindSummaryValue(findPreference("counter_interval"))
            bindSummaryValue(findPreference("stone_sound"))
            bindSummaryValue(findPreference("gong_sound"))
            bindSummaryValue(findPreference("immediateStart"))
            bindSummaryValue(findPreference("stop_after_gong"))
            bindSummaryValue(findPreference("gong_after_point"))
            bindSummaryValue(findPreference("reverse"))
            bindSummaryValue(findPreference("language"))

        }

        private fun bindSummaryValue(preference: Preference) {
            preference.onPreferenceChangeListener = listener
            when (preference) {
                is EditTextPreference, is ListPreference -> {
                    listener.onPreferenceChange(preference,
                            PreferenceManager.getDefaultSharedPreferences(preference.context)
                                    .getString(preference.key, ""))
                }
                is CheckBoxPreference -> listener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.context).getBoolean(preference.key, false))
            }
        }

        private val listener = Preference.OnPreferenceChangeListener { preference: Preference, newValue ->
            when (preference) {
                is ListPreference -> {
                    val listPreference: ListPreference = preference
                    val index = listPreference.findIndexOfValue(newValue.toString())
                    if (index >= 0) {
                        preference.setSummary(listPreference.entries[index])
                        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(preference.context)
                        if (preference.key == "language"){
                            val actualLanguage = sharedPreference.getString("language", "en")
                            if( newValue != actualLanguage){
                                changeLanguage(preference, newValue.toString())
                            }
                        }
                        if(preference.key == "stone_sound" || preference.key == "gong_sound" ){
                            val actualStone = sharedPreference.getString("stone_sound", "stone")
                            val actualGong = sharedPreference.getString("gong_sound", "gong")
                            if(preference.key == "stone_sound" && newValue != actualStone){
                                val soundPlayer = Sound(newValue.toString(),actualGong)
                                soundPlayer.playStone(preference.context)
                            }
                            if(preference.key == "gong_sound" && newValue != actualGong){
                                val soundPlayer = Sound(actualStone,newValue.toString())
                                soundPlayer.playGong(preference.context)
                            }
                        }
                    } else {
                        var index = listPreference.entryValues.indexOf(Locale.getDefault().toString().substring(0..1))
                        if (index == -1) index = 0
                        preference.setSummary(listPreference.entries[index])
                        preference.setValueIndex(index)
                    }
                }
                is EditTextPreference -> {
                    if (newValue != "") preference.setSummary(newValue.toString())
                }
                is CheckBoxPreference -> {
                    preference.isChecked = newValue as Boolean
                }
            }
            true
        }

        @SuppressLint("NewApi")
        private fun changeLanguage(preference: Preference, languageCode: String) {
            val res = preference.context.resources
            val dm = res.displayMetrics
            val conf = res.configuration
            conf.setLocale(Locale(languageCode))
            res.updateConfiguration(conf, dm)
            val intent = Intent(preference.context, SettingsActivity::class.java)
            startActivity(intent)
            activity.finish()
        }

    }
}