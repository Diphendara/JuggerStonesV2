package contador.piedras.juggerStonesV2


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import com.github.barteksc.pdfviewer.PDFView

class RegulationView : AppCompatActivity() {

    private var webView: WebView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdfview)
        val a = findViewById<View>(R.id.pdfView) as PDFView
        a.fromAsset(intent.getStringExtra("document")).load()

    }
}