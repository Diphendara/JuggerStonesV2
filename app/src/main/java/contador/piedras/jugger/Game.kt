package contador.piedras.jugger

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils.isEmpty
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import org.jetbrains.anko.toast
import java.util.*

class Game : AppCompatActivity(), ColorPickerDialogListener {
    private var timer: Timer = Timer()
    private var preferences: Prefs? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferences = Prefs(this)
        setListeners(preferences!!)

        button.setOnClickListener({
            var a: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            tv_stones.text = a.getString("stonesMaxValue_custom","")
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun startTimer(preferences: Prefs) {
        if (preferences.isTimerRunning) {
            stopTimer(preferences)
            //TODO Change to pause icon
            return
        }
        preferences.isTimerRunning = true
        timer.scheduleAtFixedRate(
                CounterTask(this, tv_stones.text.toString().toLong(),
                        Sound(preferences.stoneSound, preferences.gongSound),
                        preferences.maxValue, tv_stones), preferences.counterDelay,
                preferences.counterInterval)
    }

    private fun stopTimer(prefs: Prefs) {
        timer.cancel()
        prefs.isTimerRunning = false
        timer = Timer()
        //TODO Change pause to play icon
    }

    private fun setListeners(preferences: Prefs) {
        setUpdateCounterListener(b_plus_counter, "plus", tv_stones)
        setUpdateCounterListener(b_minus_counter, "minus", tv_stones)

        setUpdateCounterListener(b_plus_t1, "plus", tv_counter_t1)
        setUpdateCounterListener(b_minus_t1, "minus", tv_counter_t1)

        setUpdateCounterListener(b_plus_t2, "plus", tv_counter_t2)
        setUpdateCounterListener(b_minus_t2, "minus", tv_counter_t2)

        renameOneTeam(tv_t1)
        renameOneTeam(tv_t2)

        setLongClickListener(tv_t1)
        setLongClickListener(tv_t2)

        b_play.setOnClickListener { startTimer(preferences) }
        b_stop.setOnClickListener { stopTimer(preferences) }

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

    private fun setLongClickListener(textView: TextView) {
        textView.setOnLongClickListener({
            changeTeamColors(textView)
            true
        })
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        findViewById<TextView>(dialogId).setTextColor(color)
    }

    private fun setUpdateCounterListener(button: ImageButton, mode: String, counter: TextView) {
        button.setOnClickListener({
            updateCounter(counter, mode)
        })
    }

    private fun renameOneTeam(teamName: TextView) {
        teamName.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this).create()
            val editText = EditText(this)
            alertDialog.setView(editText)
            alertDialog.setTitle("Change name of ${teamName.text}")

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", { _, _ ->
                if (checkNameEditText(editText)) {
                    teamName.text = editText.text.toString()
                } else {
                    toast(R.string.name_warning)
                }
            })
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", { _, _ ->
                alertDialog.cancel()
            })
            alertDialog.show()
        }
    }

    private fun renameTeams() {
        val alertDialog = AlertDialog.Builder(this).create()
        val editTextTeam1 = EditText(this)
        editTextTeam1.hint = tv_t1.text.toString()
        val editTextTeam2 = EditText(this)
        editTextTeam2.hint = tv_t2.text.toString()
        alertDialog.setTitle(getString(R.string.change_team_names))

        val marginDP = 25
        val marginPX= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginDP.toFloat(), resources.displayMetrics).toInt()
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(marginPX, 0, marginPX, 0)

        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.addView(editTextTeam1)
        linearLayout.addView(editTextTeam2)

        alertDialog.setView(linearLayout)

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", { _, _ ->
            if (checkNameEditText(editTextTeam1) || checkNameEditText(editTextTeam2)) {
                tv_t1.text = editTextTeam1.text.toString()
                tv_t2.text = editTextTeam2.text.toString()
            } else {
                toast(R.string.name_warning)
            }
        })
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", { _, _ ->
            alertDialog.cancel()
        })

        alertDialog.show()
    }

    private fun checkNameEditText(editText: EditText): Boolean {
        if (editText.text.toString().length > 10 || editText.text.toString().length < 3) {
            return false
        }
        return true
    }

    private fun updateCounter(counter: TextView, mode: String) {
        var actualValue = Integer.parseInt(counter.text.toString())
        when (mode) {
            "plus" -> actualValue += 1
            "minus" -> if (actualValue != 0) actualValue -= 1
        }
        counter.text = actualValue.toString()
    }

    private fun resetTeams(){
        tv_t1.text = getString(R.string.team1)
        tv_counter_t1.text = 0.toString()
        tv_t2.text = getString(R.string.team2)
        tv_counter_t2.text = 0.toString()
    }

    private fun setStones(){
        val alertDialog = AlertDialog.Builder(this).create()
        val editText = EditText(this)
        alertDialog.setView(editText)
        alertDialog.setTitle("Set stones")

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", { _, _ ->
            if (!isEmpty(editText.text.trim())) {
                tv_stones.text = editText.text.toString()
            }
        })
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", { _, _ ->
            alertDialog.cancel()
        })
        alertDialog.show()

    }

    override fun onDialogDismissed(dialogId: Int) {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent: Intent
        when (item.itemId) {
            R.id.teams_rename -> {
                renameTeams()
                return true
            }
            R.id.teams_changeColor_1 -> {
                changeTeamColors(tv_t1)
                return true
            }
            R.id.teams_changeColor_2 -> {
                changeTeamColors(tv_t2)
                return true
            }
            R.id.teams_reset -> {
                resetTeams()
                return true
            }
            R.id.editStones -> {
                setStones()
                return true
            }
        R.id.action_settings -> {
            stopTimer(preferences!!)
            intent = Intent(this, SettingsActivity::class.java)

            startActivity(intent)
            return true
         }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}