package android.prosotec.proyectocierzo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.activity_social.*

import com.example.android.mediasession.R

class SocialActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_social)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /** Necesario comprobar si el perfil que se muestra es mio (el boton es un engranaje) o es de otra persona (es un corazón)**/
        bt_social.setImageResource(R.drawable.ic_settings_white_24dp)

        /** Cargar los datos de la persona y el reciclerView**/
        tv_social_name.text = "José Félix Longares"
        tv_social_mail.text = "jofelomo@mail.net"
        tv_social_bio.text = "Estudiante de Ingeniería informática"
    }



}
