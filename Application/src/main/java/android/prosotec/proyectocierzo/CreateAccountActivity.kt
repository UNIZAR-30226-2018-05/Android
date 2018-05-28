package android.prosotec.proyectocierzo

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.LinearLayout
import com.example.android.mediasession.R
import io.swagger.client.ApiException
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.activity_login.*

class CreateAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_account)



        bt_access_c.setOnClickListener {

        }

    }

    private inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            when (v.id) {
                R.id.bt_access_c -> { if(et_pass_c != et_pass2_c){
                        error_text_c.text = "Las contraseñas no coinciden"
                        error_layout_c.visibility = View.VISIBLE
                     }else{
                        if(!et_mail_c.text.equals("") && !et_name_c.text.equals("") && !et_nickname_c.text.equals("")){
                           singUpTask()
                        }else{
                            error_text_c.text = "Faltan datos por insertar"
                            error_layout_c.visibility = View.VISIBLE
                        }

                     }
                }
                R.id.et_nickname_c-> findViewById<LinearLayout>(R.id.error_layout).visibility = View.GONE
                R.id.et_name_c-> findViewById<LinearLayout>(R.id.error_layout).visibility = View.GONE
                R.id.et_mail_c -> findViewById<LinearLayout>(R.id.error_layout).visibility = View.GONE
                R.id.et_pass_c-> findViewById<LinearLayout>(R.id.error_layout).visibility = View.GONE
                R.id.et_pass2_c-> findViewById<LinearLayout>(R.id.error_layout).visibility = View.GONE
            }
        }
    }

    private inner class singUpTask : AsyncTask<String, Void, Any>() {
        override fun doInBackground(vararg params: String?): Any {
            try {
                cierzo.model.signup(et_mail_c.text.toString(),et_name_c.text.toString(),et_nickname_c.text.toString(),et_pass_c.text.toString())
            } catch (e: ApiException) {
                return e
            }
            return true
        }
    }
}
