package contador.piedras.jugger


import android.content.Context
import android.widget.TextView
import org.jetbrains.anko.runOnUiThread

import java.util.TimerTask

class CounterTask(private val context: Context, private var stones: Int, private val sound: Sound, private var maxValue: Int, private var tv_counter: TextView) : TimerTask() {

    override fun run() {
        stones += 1
        context.runOnUiThread {
            tv_counter.text = stones.toString()
        }
        if(stones == maxValue) {
            sound.playGong(context)
        } else{
            sound.playStone(context)
        }
    }
}