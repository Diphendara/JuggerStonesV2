package contador.piedras.jugger

import android.app.AlertDialog
import android.app.ProgressDialog.show
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener


class Game : AppCompatActivity(), ColorPickerDialogListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setListeners()

    }

    private fun setListeners(){
        configListener(b_plus_counter, "plus", tv_counter)
        configListener(b_minus_counter, "minus", tv_counter)

        configListener(b_plus_t1, "plus", tv_counter_t1)
        configListener(b_minus_t1, "minus", tv_counter_t1)

        configListener(b_plus_t2, "plus", tv_counter_t2)
        configListener(b_minus_t2, "minus", tv_counter_t2)

        showAlertDialog(tv_t1)
        showAlertDialog(tv_t2)

        congifLongListener(tv_t1)
        congifLongListener(tv_t2)

    }

    private fun changeTeamColors(team: TextView) {
        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.STYLE_NORMAL)
                .setAllowPresets(false)
                .setDialogId(team.id)
                .setColor(Color.BLACK)
                .setShowAlphaSlider(true)
                .show(this)
    }

    private fun congifLongListener(textView: TextView){
        textView.setOnLongClickListener({
            changeTeamColors(textView)
            true
        })
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        var view = findViewById<TextView>(dialogId)
        view.setTextColor(color)
    }

    private fun configListener(button: ImageButton, mode: String, counter: TextView){
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
                teamName.text = editText.text.toString()
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
