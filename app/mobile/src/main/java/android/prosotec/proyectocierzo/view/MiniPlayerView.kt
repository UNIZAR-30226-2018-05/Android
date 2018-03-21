package android.prosotec.proyectocierzo.view

import android.content.Context
import android.prosotec.proyectocierzo.R
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.view_new_mini_player.view.*

/**
 * Created by ccucr on 18/03/2018.
 */
class MiniPlayerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0
) : RelativeLayout(context, attrs, defStyle, defStyleRes) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_new_mini_player,this,true)
    }

}