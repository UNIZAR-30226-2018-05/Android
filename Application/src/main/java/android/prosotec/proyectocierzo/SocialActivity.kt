package android.prosotec.proyectocierzo

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import cierzo.model.objects.User
import com.example.android.mediasession.CierzoApp
import kotlinx.android.synthetic.main.activity_social.*

import com.example.android.mediasession.R
import io.swagger.client.ApiException

class SocialActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_social)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (getIntent().hasExtra("MODE")) {
            val mode = getIntent().getIntExtra("MODE",0)
            if(mode == 0 ){
                bt_social_profile.setImageResource(R.drawable.ic_settings_white_24dp)
            }
        } else {
            throw IllegalArgumentException("Activity cannot find  extras " + "MODE");
        }

        bt_social_profile.setOnClickListener {
            intent = Intent(applicationContext, ProfileChangeActivity::class.java)
            startActivity(intent)
        }


    }

    private inner class profileAsyncTask : AsyncTask<String, Void, Any>() {
        override fun doInBackground(vararg params: String?): Any {
            try {
                val userAux = (application as CierzoApp).mUserLogged.getUser() as User
                val infoAux: Set<Any> = userAux.getInfo()
                tv_social_name.text = infoAux.elementAt(2) as String
                tv_social_mail.text = infoAux.elementAt(1) as String
                tv_social_bio.text = infoAux.elementAt(3) as String
            } catch (e: ApiException) {
                return e
            }
            return true
        }
    }


}
