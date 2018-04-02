package contador.piedras.jugger

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


data class Prefs (val context: Context){

    val MAX_VALUE = "stonesMaxValue"
    val COUNTER_INTERVAL = "counter_interval"
    val STONE_SOUND = "stone_sound"
    val GONG_SOUND = "gong_sound"

    val COUNTER_DELAY = "counter_delay"
    val IS_ON = "is_counter_on"
    val LANGUAGE = "languague"

    val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var maxValue: Long
        get() = prefs.getString(MAX_VALUE, "100").toLong()
        set(value) = prefs.edit().putLong(MAX_VALUE,value).apply()

    var counterInterval: Long
        get() = (prefs.getString(COUNTER_INTERVAL, "1.5").toFloat()*1000).toLong()
        set(value) = prefs.edit().putLong(COUNTER_INTERVAL,value).apply()


    var stoneSound: String
        get() = prefs.getString(STONE_SOUND, "censure")
        set(value) = prefs.edit().putString(STONE_SOUND,value).apply()

    var gongSound: String
        get() = prefs.getString(GONG_SOUND, "gong")
        set(value) = prefs.edit().putString(GONG_SOUND,value).apply()

    var counterDelay: Long
        get() = prefs.getLong(COUNTER_DELAY, 1500)
        set(value) = prefs.edit().putLong(COUNTER_DELAY,value).apply()


    var isTimerRunning: Boolean
        get() = prefs.getBoolean(IS_ON, false)
        set(value) = prefs.edit().putBoolean(IS_ON,value).apply()

    var language: String
        get() = prefs.getString(LANGUAGE, "en")
        set(value) = prefs.edit().putString(LANGUAGE,value).apply()

}