package contador.piedras.juggerStonesV2

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

class Sound(private val stone: String, private val gong: String) {

    fun playStone(context: Context) {
        play(context, stone)
    }

    fun playGong(context: Context) {
        play(context, gong)
    }

    private fun play(context: Context, name: String) {
        val uri = Uri.parse("android.resource://" + context.packageName + "/raw/" + name)
        val auxStone = MediaPlayer.create(context, uri)
        auxStone.start()
        auxStone.setOnCompletionListener { mp -> mp.release() }
    }

}