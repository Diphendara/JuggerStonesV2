package contador.piedras.jugger

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import org.jetbrains.anko.toast


class SendEmail: PreferenceActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        sendEmail()
        super.onCreate(savedInstanceState)

    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_TEXT, arrayOf("cristiancvacas@gmail.com")) // TODO to string xml
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "JuggerStones V2 Report")  // TODO to string xml

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."))  // TODO to string xml
            finish()
        } catch (ex: android.content.ActivityNotFoundException) {
            toast("There is no email client installed").show()  // TODO to string xml
        }

    }
}

