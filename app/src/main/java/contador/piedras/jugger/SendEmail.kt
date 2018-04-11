package contador.piedras.jugger

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast



class SendEmail: PreferenceActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        sendEmail()
        super.onCreate(savedInstanceState)

    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.type = "text/plain"

        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("cristiancvacas@gmail.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "JuggerStones V2 Report")

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."))
            finish()
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(applicationContext,"There is no email client installed.", Toast.LENGTH_SHORT).show()
        }

    }
}

