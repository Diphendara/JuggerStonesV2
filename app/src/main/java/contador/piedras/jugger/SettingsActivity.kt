package contador.piedras.jugger

import android.os.Bundle
import android.preference.*

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

            /*
            bindSummaryValue(findPreference("reverse"))
            bindSummaryValue(findPreference("immediateStart"))
            bindSummaryValue(findPreference("stop_after_point"))
            bindSummaryValue(findPreference("stop_after_gong"))
            */


            bindSummaryValue(findPreference("language"))
        }

        private fun bindSummaryValue(preference: Preference) {
            /* if(preference.key.contains("stones") or preference.key.contains("interval")){
                 preference.onPreferenceChangeListener = listOrEditTextListener
                 listOrEditTextListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.context).
                         getString(preference.key, ""))
                 return
             }
             */
            preference.onPreferenceChangeListener = listener

            when (preference) {
                is EditTextPreference, is ListPreference -> {
                    listener.onPreferenceChange(preference,
                            PreferenceManager.getDefaultSharedPreferences(preference.context)
                                    .getString(preference.key, ""))
                }

            //is CheckBoxPreference -> listener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.context).
            //      getBoolean(preference.key, false))
            }
        }

        private val listener = Preference.OnPreferenceChangeListener { preference: Preference, newValue ->
            when (preference) {
                is ListPreference -> {
                    val listPreference: ListPreference = preference
                    val index = listPreference.findIndexOfValue(newValue.toString())
                    if (index > 0) {
                        preference.setSummary(listPreference.entries[index])
                    } else {
                        preference.setSummary(null)
                    }
                    //https://youtu.be/KshhMCuxnHs?t=211
                }
                is EditTextPreference -> preference.setSummary(newValue.toString())
                is CheckBoxPreference -> {
                    preference.isChecked = newValue as Boolean
                }
            }
            true
        }

    }

    fun Boolean.toInt() = if (this) 1 else 0

    private val listOrEditTextListener = Preference.OnPreferenceChangeListener { preference: Preference, newValue ->
        if (preference is EditTextPreference) {
            preference.setSummary(newValue.toString())
        } else if (preference is ListPreference) {
            val index = preference.findIndexOfValue(newValue.toString())
            if (index > 0) {
                preference.setSummary(preference.entries[index])
            } else {
                preference.setSummary(null)
            }
        }
        false
    }

}