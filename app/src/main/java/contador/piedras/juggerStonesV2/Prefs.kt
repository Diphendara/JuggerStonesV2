package contador.piedras.juggerStonesV2

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.preference.PreferenceManager
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
data class Prefs(private val context: Context) {

    val MAX_VALUE = "stonesMaxValue"
    val COUNTER_INTERVAL = "counter_interval"
    val STONE_SOUND = "stone_sound"
    val GONG_SOUND = "gong_sound"
    val COUNTER_DELAY = "counter_delay"
    val IS_ON = "is_counter_on"
    val STOP_AFTER_GONG = "stop_after_gong"
    val STOP_AFTER_POINT = "stop_after_point"
    val START_VALUE = "start_value"
    val ON_REVERSE ="reverse"
    val LANGUAGE = "language"
    val SHOW_RATE_ALERT = "showRateAlert"
    val GONG_AFTER_POINT = "gong_after_point"

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var showRateAlert: Boolean
        get() = prefs.getBoolean(SHOW_RATE_ALERT, false)
        set(value) = prefs.edit().putBoolean(SHOW_RATE_ALERT, value).apply()

    var maxValue: Long
        get(){
            val actualValue = prefs.getString(MAX_VALUE, "100")
            return if ( actualValue == "") 100 else actualValue.toLong()
        }
        set(value) = prefs.edit().putLong(MAX_VALUE, value).apply()

    var onReverse: Boolean
        get() = prefs.getBoolean(ON_REVERSE, false)
        set(value) = prefs.edit().putBoolean(ON_REVERSE, value).apply()

    var startCounter:String
        get() = if(onReverse) maxValue.toString() else "0"
        set(value) = prefs.edit().putString(START_VALUE, value).apply()

    var stopAfterGong: Boolean
        get() = prefs.getBoolean(STOP_AFTER_GONG, false)
        set(value) = prefs.edit().putBoolean(STOP_AFTER_GONG, value).apply()

    var stopAfterPoint: Boolean
        get() = prefs.getBoolean(STOP_AFTER_POINT, true)
        set(value) = prefs.edit().putBoolean(STOP_AFTER_POINT, value).apply()

    var counterInterval: Long
        get()
        {
            val actualValue = prefs.getString(COUNTER_INTERVAL, "1.5")
            return if ( actualValue == "")  1500 else (actualValue.toFloat() * 1000).toLong()
        }
        set(value) = prefs.edit().putLong(COUNTER_INTERVAL, value).apply()

    var stoneSound: String
        get() = prefs.getString(STONE_SOUND, "censure")
        set(value) = prefs.edit().putString(STONE_SOUND, value).apply()

    var gongSound: String
        get() = prefs.getString(GONG_SOUND, "gong")
        set(value) = prefs.edit().putString(GONG_SOUND, value).apply()

    var counterDelay: Long
        get() = if (prefs.getBoolean("immediateStart", false)) 0 else counterInterval
        set(value) = prefs.edit().putLong(COUNTER_DELAY, value).apply()

    var isTimerRunning: Boolean
        get() = prefs.getBoolean(IS_ON, false)
        set(value) = prefs.edit().putBoolean(IS_ON, value).apply()

    var language: String
        get() = prefs.getString(LANGUAGE, Locale.getDefault().toString().substring(0..1))
        set(value) = prefs.edit().putString(LANGUAGE, value).apply()

    var gongAfterPoint: Boolean
        get() = prefs.getBoolean(GONG_AFTER_POINT, false)
        set(value) = prefs.edit().putBoolean(GONG_AFTER_POINT, value).apply()

}