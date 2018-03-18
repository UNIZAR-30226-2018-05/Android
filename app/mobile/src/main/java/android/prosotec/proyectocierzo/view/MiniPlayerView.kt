package android.prosotec.proyectocierzo.view

import android.content.Context
import android.prosotec.proyectocierzo.R
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

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
        var title = findViewById<TextView>(R.id.title)
        var author = findViewById<TextView>(R.id.author)
        var length = findViewById<TextView>(R.id.length)
        var actual_time = findViewById<TextView>(R.id.actual_time)
        var cover = findViewById<ImageView>(R.id.cover_image)
        var skip_prev_button = findViewById<ImageButton>(R.id.skip_prev)
        var skip_next_button = findViewById<ImageButton>(R.id.skip_next)
        var play_button = findViewById<ImageButton>(R.id.play)
    }

}