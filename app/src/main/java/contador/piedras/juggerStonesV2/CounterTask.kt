package contador.piedras.juggerStonesV2

import android.content.Context
import android.widget.TextView
import org.jetbrains.anko.runOnUiThread

import java.util.TimerTask

class CounterTask(private val context: Context, private var stones: Long, private val sound: Sound, private var preferences: Prefs, private var tv_counter: TextView) : TimerTask() {

    override fun run() {
        if (preferences.onReverse) stones -= 1 else stones += 1
        context.runOnUiThread {
            tv_counter.text = stones.toInt().toString()
        }
        if ((stones == preferences.maxValue && !preferences.onReverse) || (stones == "0".toLong() && preferences.onReverse)) {
            sound.playGong(context)
            if (preferences.stopAfterGong || preferences.onReverse) {
                this.cancel()
            }
        } else {
            sound.playStone(context)
        }
    }
}