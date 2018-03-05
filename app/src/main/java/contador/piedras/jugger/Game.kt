package contador.piedras.jugger

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import contador.piedras.jugger.R.id.*
import org.jetbrains.anko.toast
import java.util.*

class Game : AppCompatActivity(), ColorPickerDialogListener {
    private var timer: Timer = Timer()
    private var isTimerRunning: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setListeners()

    }

    private fun startTimer() {
        if(isTimerRunning) {
            stopTimer()
            //TODO Change to pause icon
            return
        }
        isTimerRunning = true
        timer.scheduleAtFixedRate(
                CounterTask(this, Integer.parseInt(tv_counter.text.toString()),
                        Sound("censure","gong"),100, tv_counter),
                1500, 1500) //TODO Change to global value
    }

    private fun stopTimer(){
        timer.cancel()
        isTimerRunning = false
        timer = Timer()
        //TODO Change pause to play icon
    }

    var mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            //TODO add the detect of STONES, 100, Custom or infinite
            tv_counter.text = (Integer.parseInt(tv_counter.text.toString()) + 1).toString()
        }
    }

    private fun setListeners(){
        setUpdateCounterListener(b_plus_counter, "plus", tv_counter)
        setUpdateCounterListener(b_minus_counter, "minus", tv_counter)

        setUpdateCounterListener(b_plus_t1, "plus", tv_counter_t1)
        setUpdateCounterListener(b_minus_t1, "minus", tv_counter_t1)

        setUpdateCounterListener(b_plus_t2, "plus", tv_counter_t2)
        setUpdateCounterListener(b_minus_t2, "minus", tv_counter_t2)

        showAlertDialog(tv_t1)
        showAlertDialog(tv_t2)

        setLongClickListener(tv_t1)
        setLongClickListener(tv_t2)

        b_play.setOnClickListener { startTimer() }
        b_stop.setOnClickListener { stopTimer() }

    }

    private fun changeTeamColors(team: TextView) {
        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(false)
                .setDialogId(team.id)
                .setColor(Color.BLACK)
                .setShowAlphaSlider(true)
                .show(this)
    }

    private fun setLongClickListener(textView: TextView){
        textView.setOnLongClickListener({
            changeTeamColors(textView)
            true
        })
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        var view = findViewById<TextView>(dialogId)
        view.setTextColor(color)
    }

    private fun setUpdateCounterListener(button: ImageButton, mode: String, counter: TextView){
        button.setOnClickListener({
            updateCounter(counter, mode)
        })
    }

    private fun showAlertDialog(teamName: TextView){
        teamName.setOnClickListener{
            val alertDialog = AlertDialog.Builder(this).create()
            val editText = EditText(this)
            alertDialog.setView(editText)
            alertDialog.setTitle("Change name of ${teamName.text}")

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", {
                _, _ ->
                if(editText.text.toString().length > 10){
                    toast(R.string.name_too_long)
                }else{
                    teamName.text = editText.text.toString()
                }
            })
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", {
                _, _ ->
                alertDialog.cancel()
            })
            alertDialog.show()
        }
    }

    private fun updateCounter(counter: TextView, mode:String){
        var actualValue = Integer.parseInt(counter.text.toString())
        when(mode){
            "plus" -> actualValue += 1
            "minus" -> if(actualValue != 0) actualValue -= 1
        }
        counter.text = actualValue.toString()
    }

    override fun onDialogDismissed(dialogId: Int) {

    }
}
