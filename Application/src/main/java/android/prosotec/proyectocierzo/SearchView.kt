package android.prosotec.proyectocierzo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.android.mediasession.R
import kotlinx.android.synthetic.main.activity_search_view.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SearchView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search_view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

}
