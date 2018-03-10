package contador.piedras.jugger

import android.content.Context
import android.content.SharedPreferences

class Prefs (context: Context) {
    private val PREFS_FILENAME = "contador.piedras.jugger.prefs"
    private val MAX_VALUE = "max_counter"
    private val STONE_SOUND = "stone_sound"
    private val GONG_SOUND = "gong_sound"
    private val COUNTER_DELAY = "counter_delay"
    private val COUNTER_INTERVAL = "counter_interval"
    private val IS_ON = "is_counter_on"

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    var maxValue: Long
        get() = prefs.getLong(MAX_VALUE, 2)
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

}