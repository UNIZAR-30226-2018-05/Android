package android.prosotec.proyectocierzo

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.android.mediasession.CierzoApp
import com.example.android.mediasession.R
import io.swagger.client.ApiException
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.activity_new_playlist.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class NewPlaylistActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_new_playlist)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bt_create.setOnClickListener {
            if(!et_playlist_name.equals("") && !et_desc.equals("")){
                val error = newPlaylistTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,et_playlist_name.text.toString(),et_desc.text.toString()).get()
                if(error is ApiException){
                    error_text_p.text = "Error al crear la PlayList"
                    error_layout_p.visibility = View.VISIBLE
                }else{
                    finish()
                }
            }else{
                error_text_p.text = "Faltan datos"
                error_layout_p.visibility = View.VISIBLE
            }
        }
        et_playlist_name.setOnClickListener {
            error_layout_p.visibility = View.GONE
        }
        et_desc.setOnClickListener {
            error_layout_p.visibility = View.GONE
        }
    }

    private inner class newPlaylistTask : AsyncTask<String, Void, Any>() {
        override fun doInBackground(vararg params: String?): Any {
            try {
                (application as CierzoApp).mUserLogged.newPlaylist(params[0]!!,params[1]!!)
            } catch (e: ApiException) {
                return e
            }
            return true
        }
    }

}
