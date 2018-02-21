package com.example.ccanadas.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.KeyEvent
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import contador.piedras.jugger.JuggerStonesApplication
import contador.piedras.jugger.R
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert

class MainActivity : AppCompatActivity() {

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

    }

    private fun configListener(button: ImageButton, mode: String, counter: TextView){
        button.setOnClickListener({
            updateCounter(counter, mode)
        })
    }

    private fun showAlertDialog(teamName: TextView){

        teamName.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            val editText = EditText(this)
            val dialog = alertDialog.create()

            with(alertDialog){
                setTitle("Change name")
                setPositiveButton("Change"){ _, _ ->
                    teamName.text = editText.text.toString()
                }
                setNegativeButton("Cancel"){
                    dialog, _ ->
                    dialog.dismiss()
                }
            }
            dialog.setView(editText)
            dialog.show()
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                JuggerStonesApplication.increaseVolume()
                return true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN ->{
                JuggerStonesApplication.decreaseVolume()
                return true
            }
            KeyEvent.KEYCODE_BACK ->{
                finish()
                return true
            }
            else -> return true
        }
    }

}
