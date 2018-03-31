package contador.piedras.jugger

import android.content.Context
import android.content.SharedPreferences


data class Prefs (val context: Context){

    val PREFS_FILENAME = "contador.piedras.jugger.prefs"
    val MAX_VALUE = "max_counter"
    val STONE_SOUND = "stone_sound"
    val GONG_SOUND = "gong_sound"
    val COUNTER_DELAY = "counter_delay"
    val COUNTER_INTERVAL = "counter_interval"
    val IS_ON = "is_counter_on"
    val LANGUAGE = "languague"

    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var maxValue: Long
        get() = prefs.getLong(MAX_VALUE, 100)
        set(value) = prefs.edit().putLong(MAX_VALUE,value).apply()

    var stoneSound: String
        get() = prefs.getString(STONE_SOUND, "censure")
        set(value) = prefs.edit().putString(STONE_SOUND,value).apply()

    var gongSound: String
        get() = prefs.getString(GONG_SOUND, "gong")
        set(value) = prefs.edit().putString(GONG_SOUND,value).apply()

    var counterDelay: Long
        get() = prefs.getLong(COUNTER_DELAY, 1500)
        set(value) = prefs.edit().putLong(COUNTER_DELAY,value).apply()

    var counterInterval: Long
        get() = prefs.getLong(COUNTER_INTERVAL, 1500)
        set(value) = prefs.edit().putLong(COUNTER_INTERVAL,value).apply()

    var isTimerRunning: Boolean
        get() = prefs.getBoolean(IS_ON, false)
        set(value) = prefs.edit().putBoolean(IS_ON,value).apply()

    var language: String
        get() = prefs.getString(LANGUAGE, "en")
        set(value) = prefs.edit().putString(LANGUAGE,value).apply()

}