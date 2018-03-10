package contador.piedras.jugger

import android.os.Bundle
import android.preference.PreferenceActivity

class MyPreferenceActivity : PreferenceActivity(){

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)

    }
}