package contador.piedras.juggerStonesV2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.text.TextUtils.isEmpty
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.util.*

/**
 * This class has the necessary for the basic application function
 */
class Game : AppCompatActivity(), ColorPickerDialogListener {

    private var timer: Timer = Timer()
    private var preferences: Prefs? = null

    /**
     * Starts Crashlytics
     * It initialize the preferences object
     * It change the language of the app to the preferences one
     * It Call the function who set all the listeners
     * If the app restarts in a controlled way sets the data of the view at the same before the restarts
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        preferences = Prefs(this)

        changeLanguage(this, preferences!!.language)
        setContentView(R.layout.activity_main)
        setListeners(preferences!!)
        if(!(preferences!!.showRateAlert)) {
            showRateAlert(preferences!!)
        }

        val isRestart = intent.getBooleanExtra("restart",false)
        if (isRestart) {
            tv_t1.text = intent.getStringExtra("nameTeamOne")
            tv_t2.text = intent.getStringExtra("nameTeamTwo")
            tv_t1.setTextColor(intent.getIntExtra("colorTeamOne", -16711936))
            tv_t2.setTextColor(intent.getIntExtra("colorTeamTwo", -65536))
            tv_counter_t1.text = intent.getStringExtra("counterTeamOne")
            tv_counter_t2.text = intent.getStringExtra("counterTeamTwo")
            tv_stones.text = intent.getStringExtra("stones")
        }
    }

    private fun showRateAlert(preferences: Prefs) {
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle(getString(R.string.rate_title))
        alertDialog.setMessage(getString(R.string.rate_body))
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.rate)) { _, _ ->
            preferences.showRateAlert = true
            val playStoreUri: Uri = Uri.parse("market://details?id=contador.piedras.juggerStonesV2")
            val playStoreIntent = Intent(Intent.ACTION_VIEW, playStoreUri)
            startActivity(playStoreIntent)
            alertDialog.cancel()
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.rate_no)) { _, _ ->
            alertDialog.cancel()
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.never)) { _, _ ->
            preferences.showRateAlert = true
            alertDialog.cancel()
        }
        alertDialog.show()
    }

    /**
     * Stop the timer if the app goes to onPause
     */
    override fun onPause() {
        super.onPause()
        stopTimer(Prefs(this), "pause")
    }

    /**
     * It change the language of the entire app
     * @param context the actual context of the app
     * @param languageCode the code of the language selected
     */
    @SuppressLint("NewApi")
    private fun changeLanguage(context: Context, languageCode: String) {
        val res = context.resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.setLocale(Locale(languageCode))
        res.updateConfiguration(conf, dm)
    }

    /**
     * On a restart this will restart the app from 0 but save the data, this method usually is used
     * for the language change
     */
    override fun onRestart() {
        super.onRestart()
        val intent = Intent(baseContext, Game::class.java)
        intent.putExtra("nameTeamOne", tv_t1.text.toString())
        intent.putExtra("nameTeamTwo", tv_t2.text.toString())
        intent.putExtra("colorTeamOne", tv_t1.currentTextColor)
        intent.putExtra("colorTeamTwo", tv_t2.currentTextColor)
        intent.putExtra("counterTeamOne", tv_counter_t1.text.toString())
        intent.putExtra("counterTeamTwo", tv_counter_t2.text.toString())
        intent.putExtra("stones", tv_stones.text.toString())
        intent.putExtra("restart",true)
        startActivity(intent)
        finish()
    }

    /**
     * It set the menu options
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Manage the timer
     * If it is already started it will stop it
     * @throws NumberFormatException for try use the empty string as counter
     * @param preferences the preferences of the app
     */
    private fun startTimer(preferences: Prefs) {
        b_change_mode.isEnabled = false
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (preferences.isTimerRunning) {
            b_play.setImageResource(R.drawable.ic_play)
            stopTimer(preferences, "pause")
            return
        }
        if (tv_stones.text == "0") { tv_stones.text = preferences.startCounter } //For the reverse option
        preferences.isTimerRunning = true
        try{
            timer.scheduleAtFixedRate(
                    CounterTask(this, tv_stones.text.toString().toLong(),
                            Sound(preferences.stoneSound, preferences.gongSound), preferences,
                            tv_stones), preferences.counterDelay,
                    preferences.counterInterval)
            b_play.setImageResource(R.drawable.ic_pause)
        }catch (e: NumberFormatException){
            val alertDialog = AlertDialog.Builder(this).create()
            alertDialog.setTitle(getString(R.string.error))
            alertDialog.setMessage(getString(R.string.error_values))
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok)) { _, _ -> alertDialog.cancel()}
            alertDialog.show()
        }
    }

    /**
     * It stop the Timer
     * @param preferences the preferences of the app
     * @param mode the mode how stop the timer, set the text stones to 0 (stop) or not (just pause)
     */
    private fun stopTimer(preferences: Prefs, mode: String) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        timer.cancel()
        preferences.isTimerRunning = false
        timer = Timer()
        b_play.setImageResource(R.drawable.ic_play)
        b_change_mode.isEnabled = true
        if(mode == "stop"){ tv_stones.text = "0" }
    }

    /**
     * Set the listeners of all buttons
     * @param preferences the preferences of the app
     */
    private fun setListeners(preferences: Prefs) {
        setUpdateCounterListener(b_plus_counter, "plus", tv_stones, preferences)
        setUpdateCounterListener(b_minus_counter, "minus", tv_stones, preferences)

        setUpdateTeamPointsListener(b_plus_t1, "plus", tv_counter_t1, preferences)
        setUpdateTeamPointsListener(b_minus_t1, "minus", tv_counter_t1, preferences)

        setUpdateTeamPointsListener(b_plus_t2, "plus", tv_counter_t2, preferences)
        setUpdateTeamPointsListener(b_minus_t2, "minus", tv_counter_t2, preferences)

        renameOneTeam(tv_t1)
        renameOneTeam(tv_t2)

        changeTeamListener(tv_t1)
        changeTeamListener(tv_t2)

        b_play.setOnClickListener { startTimer(preferences) }
        b_stop.setOnClickListener { stopTimer(preferences, "stop") }

        b_change_mode.setOnClickListener{changeMode(preferences)}

        b_share.setOnClickListener{ share() }

        b_reset_all.setOnClickListener{ resetAllViews() }
    }

    /**
     * Reset all the textView objects, but first shows a warning
     */
    private fun resetAllViews(){
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle(getString(R.string.reset_all))

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok)) { _, _ ->
            resetTeams()
            tv_stones.text = "0"
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { _, _ ->
            alertDialog.cancel()
        }
        alertDialog.show()
    }

    /**
     * It open the share option showing the name of the teams and the points of each
     */
    private fun share(){
        val shareIntent = Intent(android.content.Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        var shareText = getString(R.string.tweet_text) + " "
        shareText += tv_t1.text.toString() + " "+tv_counter_t1.text.toString()
        shareText += " - "
        shareText += tv_counter_t2.text.toString() +" "+ tv_t2.text.toString()
        shareText += " " + getString(R.string.playStore_link)
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,shareText)
        startActivity(Intent.createChooser(shareIntent,getString(R.string.share_title)))
    }

    /**
     * It changes the mode of the timer by changing the preference of the app
     * @param preferences the preferences of the app
     */
    private fun changeMode(preferences: Prefs){
        if(!preferences.onReverse){
            preferences.onReverse = true
            b_change_mode.setImageResource(R.drawable.timer_minus)
        }else{
            preferences.onReverse = false
            b_change_mode.setImageResource(R.drawable.timer_plus)
        }
    }

    /**
     * It invoque the library for the change the color of a textView
     * @param team TextView who color will be changed
     */
    private fun changeTeamColors(team: TextView) {
        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(false)
                .setDialogId(team.id)
                .setColor(Color.BLACK)
                .setShowAlphaSlider(true)
                .show(this)
    }

    /**
     * @param textView TextView who color will be changed
     */
    private fun changeTeamListener(textView: TextView) {
        textView.setOnLongClickListener{
            changeTeamColors(textView)
            true
        }
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        findViewById<TextView>(dialogId).setTextColor(color)
    }

    /**
     * It update the counter
     * @param button the button who will be have the listener
     * @param mode the mode of the listener, can be "plus" or "min" to increase or decrease by one a textView
     * @param counter the TextView who will be modified
     * @param preferences the preferences of the app
     */
    private fun setUpdateCounterListener(button: ImageButton, mode: String, counter: TextView, preferences: Prefs) {
        button.setOnClickListener{
            updateCounter(counter, mode)
        }
    }

    private fun setUpdateTeamPointsListener(button: ImageButton, mode: String, counter: TextView, preferences: Prefs) {
        button.setOnClickListener{
            updateCounter(counter, mode)
            if (preferences.stopAfterPoint) {
                stopTimer(preferences, "pause")
                if(preferences.gongAfterPoint) {
                    val sound = Sound(preferences.stoneSound, preferences.gongSound)
                    sound.playGong(this)
                }
            }
        }
    }
    /**
     * Shows a alertDialog for rename one team
     * @param teamName TextView who will be changed
     */
    private fun renameOneTeam(teamName: TextView) {
        teamName.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this).create()
            val editText = EditText(this)
            alertDialog.setView(editText)
            alertDialog.setTitle(getString(R.string.change_team_name)+ " ${teamName.text}")

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok)) { _, _ ->
                if (checkNameEditText(editText)) {
                    teamName.text = editText.text.toString()
                } else {
                    toast(R.string.name_warning)
                }
            }
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { _, _ -> alertDialog.cancel() }
            alertDialog.show()
        }
    }
    /**
     * Shows a alertDialog for rename the two teams
     */
    private fun renameTeams() {
        val alertDialog = AlertDialog.Builder(this).create()
        val editTextTeam1 = EditText(this)
        editTextTeam1.hint = tv_t1.text.toString()
        val editTextTeam2 = EditText(this)
        editTextTeam2.hint = tv_t2.text.toString()
        alertDialog.setTitle(getString(R.string.change_team_names))

        val marginPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25.toFloat(), resources.displayMetrics).toInt()
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(marginPX, 0, marginPX, 0)

        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.addView(editTextTeam1)
        linearLayout.addView(editTextTeam2)

        alertDialog.setView(linearLayout)

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok)) { _, _ ->
            if (checkNameEditText(editTextTeam1) || checkNameEditText(editTextTeam2)) {
                tv_t1.text = editTextTeam1.text.toString()
                tv_t2.text = editTextTeam2.text.toString()
            } else {
                toast(R.string.name_warning)
            }
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { _, _ ->  alertDialog.cancel() }
        alertDialog.show()
    }

    /**
     * It checks if the string used when renamed a team has between 3 and 10 characters
     * @param editText editText who have the string to check
     */
    private fun checkNameEditText(editText: EditText): Boolean {
        if (editText.text.toString().length > 10 || editText.text.toString().length < 3) {
            return false
        }
        return true
    }

    /**
     * It update the TextView
     * @param counter TextView to update
     * @param mode mode to update, increase or decrease the value
     */
    private fun updateCounter(counter: TextView, mode: String) {
        var actualValue = Integer.parseInt(counter.text.toString())
        when (mode) {
            "plus" -> actualValue += 1
            "minus" -> if (actualValue != 0) actualValue -= 1
        }
        counter.text = actualValue.toString()
    }

    /**
     * It reset the name of the teams to the default ones
     */
    private fun resetTeams() {
        tv_t1.text = getString(R.string.team1)
        tv_counter_t1.text = 0.toString()
        tv_t2.text = getString(R.string.team2)
        tv_counter_t2.text = 0.toString()
    }

    /**
     * It shows a alertdialog who can change the currents stones
     */
    private fun setStones() {
        val alertDialog = AlertDialog.Builder(this).create()
        val editText = EditText(this)
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        alertDialog.setView(editText)
        alertDialog.setTitle(getString(R.string.set_stones))

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok)) { _, _ ->
            if (!isEmpty(editText.text.trim()) && 1000 > editText.text.trim().toString().toInt() ) {
                tv_stones.text = editText.text.toString()
            }else{
                toast(getString(R.string.stones_warning)).show()
            }
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.cancel)) { _, _ ->
            alertDialog.cancel()
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.resetStones)) {_,_ ->
            tv_stones.text = "0"
        }
        alertDialog.show()
    }

    override fun onDialogDismissed(dialogId: Int) {

    }

    /**
     * A simple when to the option menu
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        stopTimer(preferences!!, "pause")
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
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_regulation -> {
                showAlertRegulation()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * It show a alert to open the pdf in english, german or spanish
     */
    private fun showAlertRegulation(){
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle(getString(R.string.regulation))
        alertDialog.setMessage(getString(R.string.jugger_link))

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.language_spanish)) { _, _ ->
            openRegulation("es")
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.language_german)) { _, _ ->
            openRegulation("de")
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.language_english)) { _, _ ->
            openRegulation("en")
        }
        alertDialog.show()
    }

    /**
     * It open the activity to read the regulation
     */
    private fun openRegulation(language:String){
        val intent = Intent(applicationContext, RegulationView::class.java)
        intent.putExtra("document", "regulation_$language.pdf")
        startActivity(intent)
    }
}