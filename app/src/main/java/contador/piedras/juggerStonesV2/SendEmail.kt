package contador.piedras.juggerStonesV2

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceActivity
import org.jetbrains.anko.toast


@SuppressLint("ExportedPreferenceActivity")
class SendEmail: PreferenceActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        sendEmail()
        super.onCreate(savedInstanceState)
    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_TEXT, arrayOf(R.string.email_current))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.email_subject)

        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.email_send_message)))
            finish()
        } catch (ex: android.content.ActivityNotFoundException) {
            toast(getString(R.string.email_client_warning)).show()
        }

    }
}

