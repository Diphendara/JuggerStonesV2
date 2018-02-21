package contador.piedras.jugger

import android.app.Application
import android.content.res.Configuration
import android.os.Build
import android.view.ContextThemeWrapper
import java.util.*

object LocaleUtils {
    // https://stackoverflow.com/a/36922319/2715720
    @JvmStatic var locale: Locale? = Locale.getDefault()
        set(locale) {
            field = locale
            if (this.locale != null) Locale.setDefault(this.locale)
        }

    fun updateConfig(wrapper: ContextThemeWrapper) {
        if (locale != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val configuration = Configuration()
            configuration.setLocale(locale)
            wrapper.applyOverrideConfiguration(configuration)
        }
    }

    @JvmStatic fun updateConfig(app: Application, configuration: Configuration) {
        if (locale != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //Wrapping the configuration to avoid Activity endless loop
            val config = Configuration(configuration)
            config.locale = locale
            val res = app.resources
            res.updateConfiguration(config, res.displayMetrics)
        }
    }
}