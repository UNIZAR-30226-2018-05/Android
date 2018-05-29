package android.prosotec.proyectocierzo

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.android.mediasession.CierzoApp
import com.example.android.mediasession.R
import io.swagger.client.ApiException
import kotlinx.android.synthetic.main.activity_new_playlist.*
import kotlinx.android.synthetic.main.activity_profile_change.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ProfileChangeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile_change)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



    }

    private fun prepareListeners(){
        et_mail_ps.setOnClickListener {
            error_layout_p.visibility = View.GONE
        }
        et_pass_ps.setOnClickListener {
            error_layout_p.visibility = View.GONE
        }
        et_pass2_ps.setOnClickListener {
            error_layout_p.visibility = View.GONE
        }
        bt_social_ps.setOnClickListener {
            newProfileDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,et_nickname_ps.text.toString()
                    ,et_name_ps.text.toString(),et_bio_ps.text.toString())
        }
        bt_signup_ps.setOnClickListener {
            if(et_mail_ps.equals("") || et_pass_ps.equals(et_pass2_ps)){
                val error = newSignUpDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,et_nickname_ps.text.toString()
                        ,et_name_ps.text.toString(),et_bio_ps.text.toString()).get()
                if(error is ApiException){
                    error_text_p.text = "Error al modificar los datos"
                    error_layout_p.visibility = View.VISIBLE
                }else{
                    finish()
                }
            }else{
                error_text_p.text = "Las contraseñas no existen o el correo no es válido"
                error_layout_p.visibility = View.VISIBLE
            }

        }
    }

    private inner class newProfileDataTask : AsyncTask<String, Void, Any>() {
        override fun doInBackground(vararg params: String?): Any {
            try {
                (application as CierzoApp).mUserLogged.editUserInfo(params[0]!!,params[1]!!,params[2]!!)
            } catch (e: ApiException) {
                return e
            }
            return true
        }
    }

    private inner class newSignUpDataTask : AsyncTask<String, Void, Any>() {
        override fun doInBackground(vararg params: String?): Any {
            try {
                (application as CierzoApp).mUserLogged.editCredentials(params[0]!!,params[1]!!)
            } catch (e: ApiException) {
                return e
            }
            return true
        }
    }
}
