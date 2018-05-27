package android.prosotec.proyectocierzo

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.example.android.mediasession.CierzoApp
import com.example.android.mediasession.R
import com.example.android.mediasession.R.id.et_mail
import io.swagger.client.ApiException
import kotlinx.android.synthetic.main.activity_login.*
import android.widget.Toast
import org.jetbrains.annotations.Nullable


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val clickListener = ClickListener()
        findViewById<View>(R.id.bt_access).setOnClickListener(clickListener)
        findViewById<View>(R.id.et_mail).setOnClickListener(clickListener)
        findViewById<View>(R.id.et_pass).setOnClickListener(clickListener)
        findViewById<View>(R.id.bt_access_direct).setOnClickListener(clickListener)
        findViewById<View>(R.id.et_pass).setOnKeyListener(View.OnKeyListener {
            v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                findViewById<View>(R.id.bt_access).callOnClick()
                return@OnKeyListener true
            }
            false
        })

        if ((application as CierzoApp).mUserLogged.isLogged()) {
            intent = Intent(applicationContext,Main2Activity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }

    private fun checkValues(hardMail: String?, hardPass: String?) {
        if (hardMail != null && hardPass != null) {
            loginTask().execute(hardMail,hardPass)
            intent = Intent(applicationContext, Main2Activity::class.java)
            startActivity(intent)
            finish()
        } else {
            val mail: String = findViewById<EditText>(R.id.et_mail).text.toString()
            val pass: String = findViewById<EditText>(R.id.et_pass).text.toString()
            var error_text: TextView = findViewById(R.id.error_text)
            var error_layout: LinearLayout = findViewById(R.id.error_layout)
            var success: Boolean = false

            var result: Any = loginTask().execute(mail, pass).get()
            if (result is ApiException) {
                error_text.text = when (result.code) {
                    400 -> "El correo y la contraseña no coinciden"
                    401 -> "Error 401"
                    else -> "Error desconocido"
                }
                error_layout.visibility = View.VISIBLE
                success = false
            } else if (result is Boolean) {
                success = result
            }

            if (success) {
                intent = Intent(applicationContext, Main2Activity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            when (v.id) {
                R.id.bt_access -> checkValues(null, null)
                R.id.et_mail -> findViewById<LinearLayout>(R.id.error_layout).visibility = View.GONE
                R.id.et_pass-> findViewById<LinearLayout>(R.id.error_layout).visibility = View.GONE
                R.id.bt_access_direct -> checkValues("alice@mail.net","ApassA")
            }
        }
    }

    private inner class loginTask : AsyncTask<String, Void, Any>() {
        override fun doInBackground(vararg params: String?): Any {
            try {
                cierzo.model.login(params[0]!!, params[1]!!)
            } catch (e: ApiException) {
                return e
            }
            return true
        }
    }
}
