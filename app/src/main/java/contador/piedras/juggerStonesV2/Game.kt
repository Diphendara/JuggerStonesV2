package contador.piedras.juggerStonesV2

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import android.text.InputType


class Game : AppCompatActivity(), ColorPickerDialogListener{

    private var timer: Timer = Timer()
    private var preferences: Prefs? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = Prefs(this)
        changeLanguage(this, preferences!!.language)
        setContentView(R.layout.activity_main)
        setListeners(preferences!!)

        val isRestart = intent.getBooleanExtra("restart",false)
        if (isRestart) {
            tv_t1.text = intent.getStringExtra("nameTeamOne")
            tv_t2.text = intent.getStringExtra("nameTeamTwo")
            tv_counter_t1.text = intent.getStringExtra("counterTeamOne")
            tv_counter_t2.text = intent.getStringExtra("counterTeamTwo")
            tv_stones.text = intent.getStringExtra("stones")
        }
    }

    override fun onPause() {
        super.onPause()
        stopTimer(Prefs(this))
    }

    @SuppressLint("NewApi")
    private fun changeLanguage(context: Context, languageCode: String) {
        val res = context.resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.setLocale(Locale(languageCode))
        res.updateConfiguration(conf, dm)
    }

    override fun onRestart() {
        super.onRestart()
        val intent = Intent(baseContext, Game::class.java)
        intent.putExtra("nameTeamOne", tv_t1.text.toString())
        intent.putExtra("nameTeamTwo", tv_t2.text.toString())
        intent.putExtra("counterTeamOne", tv_counter_t1.text.toString())
        intent.putExtra("counterTeamTwo", tv_counter_t2.text.toString())
        intent.putExtra("stones", tv_stones.text.toString())
        intent.putExtra("restart",true)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun startTimer(preferences: Prefs) {
        b_change_mode.isEnabled = false
        if (preferences.isTimerRunning) {
            b_play.setImageResource(R.drawable.ic_play)
            stopTimer(preferences)
            return
        }
        if (tv_stones.text == "0") {
            tv_stones.text = preferences.startCounter
        }
        preferences.isTimerRunning = true
        timer.scheduleAtFixedRate(
                CounterTask(this, tv_stones.text.toString().toLong(),
                        Sound(preferences.stoneSound, preferences.gongSound), preferences,
                        tv_stones), preferences.counterDelay,
                preferences.counterInterval)
        b_play.setImageResource(R.drawable.ic_pause)

    }

    private fun stopTimer(prefs: Prefs) {
        timer.cancel()
        prefs.isTimerRunning = false
        timer = Timer()
        b_play.setImageResource(R.drawable.ic_play)
        b_change_mode.isEnabled = true
    }

    private fun setListeners(preferences: Prefs) {
        setUpdateCounterListener(b_plus_counter, "plus", tv_stones, preferences)
        setUpdateCounterListener(b_minus_counter, "minus", tv_stones, preferences)

        setUpdateCounterListener(b_plus_t1, "plus", tv_counter_t1, preferences)
        setUpdateCounterListener(b_minus_t1, "minus", tv_counter_t1, preferences)

        setUpdateCounterListener(b_plus_t2, "plus", tv_counter_t2, preferences)
        setUpdateCounterListener(b_minus_t2, "minus", tv_counter_t2, preferences)

        renameOneTeam(tv_t1)
        renameOneTeam(tv_t2)

        setLongClickListener(tv_t1)
        setLongClickListener(tv_t2)

        b_play.setOnClickListener { startTimer(preferences) }
        b_stop.setOnClickListener { stopTimer(preferences) }

        b_change_mode.setOnClickListener({
            if(!preferences.onReverse){
                preferences.onReverse = true
                b_change_mode.setImageResource(R.drawable.timer_minus)
            }else{
                preferences.onReverse = false
                b_change_mode.setImageResource(R.drawable.timer_plus)
            }
        })

        share.setOnClickListener({
            val shareIntent = Intent(android.content.Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            var shareText = getString(R.string.tweet_text) + " "
            shareText += tv_t1.text.toString() + " "+tv_counter_t1.text.toString()
            shareText += " - "
            shareText += tv_counter_t2.text.toString() +" "+ tv_t2.text.toString()
            shareText += " " + getString(R.string.playStore_link)
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,shareText)
            startActivity(Intent.createChooser(shareIntent,getString(R.string.share_title)))
        })



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

    private fun setUpdateCounterListener(button: ImageButton, mode: String, counter: TextView, preferences: Prefs) {
        button.setOnClickListener({
            updateCounter(counter, mode)
            if (preferences.stopAfterPoint) {
                stopTimer(preferences)
            }
        })
    }

    private fun renameOneTeam(teamName: TextView) {
        teamName.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this).create()
            val editText = EditText(this)
            alertDialog.setView(editText)
            alertDialog.setTitle(getString(R.string.change_team_name)+ " ${teamName.text}")

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), { _, _ ->
                if (checkNameEditText(editText)) {
                    teamName.text = editText.text.toString()
                } else {
                    toast(R.string.name_warning)
                }
            })
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), { _, _ ->
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
        val marginPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginDP.toFloat(), resources.displayMetrics).toInt()
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(marginPX, 0, marginPX, 0)

        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.addView(editTextTeam1)
        linearLayout.addView(editTextTeam2)

        alertDialog.setView(linearLayout)

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), { _, _ ->
            if (checkNameEditText(editTextTeam1) || checkNameEditText(editTextTeam2)) {
                tv_t1.text = editTextTeam1.text.toString()
                tv_t2.text = editTextTeam2.text.toString()
            } else {
                toast(R.string.name_warning)
            }
        })
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), { _, _ ->
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

    private fun resetTeams() {
        tv_t1.text = getString(R.string.team1)
        tv_counter_t1.text = 0.toString()
        tv_t2.text = getString(R.string.team2)
        tv_counter_t2.text = 0.toString()
    }

    private fun setStones() {
        val alertDialog = AlertDialog.Builder(this).create()
        val editText = EditText(this)
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        alertDialog.setView(editText)
        alertDialog.setTitle(getString(R.string.set_stones))

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), { _, _ ->
            if (!isEmpty(editText.text.trim()) && 1000 > editText.text.trim().toString().toInt() ) {
                tv_stones.text = editText.text.toString()
            }else{
                toast(getString(R.string.stones_warning)).show()
            }
        })

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.cancel), { _, _ ->
            alertDialog.cancel()
        })
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.resetStones), {_,_ ->
            tv_stones.text = "0"
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
            R.id.menu_regulation -> {

                val alertDialog = AlertDialog.Builder(this).create()
                alertDialog.setTitle(getString(R.string.regulation))

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.language_spanish), { _, _ ->
                    openRegulation("es")
                })
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.language_german), { _, _ ->
                    openRegulation("de")
                })
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.language_english), {_,_ ->
                    openRegulation("en")
                })
                alertDialog.show()

                alertDialog.show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun openRegulation(language:String){
        val intent = Intent(applicationContext, RegulationView::class.java)
        intent.putExtra("document", "regulation_$language.pdf")
        startActivity(intent)
    }
}