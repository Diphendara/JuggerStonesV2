package contador.piedras.jugger

import android.os.Bundle
import android.support.v7.app.AppCompatActivity


class MyPreferenceActivity :  AppCompatActivity(){

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)
        //https://developer.android.com/reference/android/preference/PreferenceActivity.html
        //https://developer.android.com/reference/android/preference/PreferenceFragment.html
    }
}