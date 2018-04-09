package android.prosotec.proyectocierzo.fragment

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.prosotec.proyectocierzo.*
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import cierzo.model.*
import cierzo.model.objects.Song
import com.squareup.picasso.Picasso
import io.swagger.client.models.SongItem
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.fragment_mini_player.*


/**
 * A simple [Fragment] subclass.
 */
class MiniPlayerFragment : Fragment() {

    private var mContext: Context? = null

    private lateinit var mPlayerAdapter: PlayerAdapter
    private var mUserIsSeeking: Boolean = false
    private var songProgress: Int = 0

    /**
     * Hilo/Tarea que actualiza el campo de texto del tiempo actual del reproductor
     */
    private val mUpdateTime = object : Runnable {
        override fun run() {
            if (mPlayerAdapter.isPlaying()) {
                var time: Int = seek_bar.progress/1000
                var timeM: Int = time/60
                var timeS: Int = time.rem(60)
                var timeStringM = if (timeM < 10) { "0$timeM" } else { timeM.toString() }
                var timeStringS = if (timeS < 10) { "0$timeS" } else { timeS.toString() }
                actual_time.text = "$timeStringM:$timeStringS"
                actual_time.postDelayed(this, 500)
            } else {
                actual_time.removeCallbacks(this)
            }
        }
    }

    fun initialize(context: Context) {
        mContext = context

        initializePlayerView()
        initializePlaybackController()

        // Cambiar por posición del ArrayList recibido de la API
        skip_prev.setOnClickListener {
            mPlayerAdapter.release()
            when(songProgress){
                1 -> {mPlayerAdapter.loadMedia(R.raw.song5)
                    play.setImageResource(R.drawable.ic_pause_white_24dp)
                    autor.text = "Avenged Sevenfold"
                    titulo.text = "Hail to the king"
                    mPlayerAdapter.play()
                    songProgress = 5}
                2 -> {mPlayerAdapter.loadMedia(R.raw.song1)
                    play.setImageResource(R.drawable.ic_pause_white_24dp)
                    autor.text="Linkin park"
                    titulo.text="Numb"
                    mPlayerAdapter.play()
                    songProgress = 1}
                3 -> {mPlayerAdapter.loadMedia(R.raw.song2)
                    play.setImageResource(R.drawable.ic_pause_white_24dp)
                    autor.text = "Breaking Benjamin"
                    titulo.text = "Diary of Jane"
                    mPlayerAdapter.play()
                    songProgress = 2}
                4 -> {mPlayerAdapter.loadMedia(R.raw.song3)
                    play.setImageResource(R.drawable.ic_pause_white_24dp)
                    autor.text = "Green Day"
                    titulo.text = "Basket Case"
                    mPlayerAdapter.play()
                    songProgress = 3}
                5 -> {
                    var song: Song? = getSong("123456789")
                    if (song != null) {
                        autor.text = song.authorName
                        titulo.text = song.name
                        mPlayerAdapter.loadMedia(song.fileURL)
                        Picasso.get().load(song.imageURL).into(cover_image)
                        mPlayerAdapter.play()
                        songProgress = 4
                    }
                }
                else ->{mPlayerAdapter.loadMedia(R.raw.song4)
                    play.setImageResource(R.drawable.ic_pause_white_24dp)
                    autor.text = "The Offspring"
                    titulo.text = "Pretty Fly"
                    mPlayerAdapter.play()
                    songProgress = 5}
            }
        }
        skip_next.setOnClickListener {
            mPlayerAdapter.release()
            when(songProgress){
                1 -> {mPlayerAdapter.loadMedia(R.raw.song2)
                    play.setImageResource(R.drawable.ic_pause_white_24dp)
                    autor.text = "Breaking Benjamin"
                    titulo.text = "Diary of Jane"
                    mPlayerAdapter.play()
                    songProgress = 2}
                2 -> {mPlayerAdapter.loadMedia(R.raw.song3)
                    play.setImageResource(R.drawable.ic_pause_white_24dp)
                    autor.text = "Green Day"
                    titulo.text = "Basket Case"
                    mPlayerAdapter.play()
                    songProgress = 3}
                3 -> {mPlayerAdapter.loadMedia(R.raw.song4)
                    play.setImageResource(R.drawable.ic_pause_white_24dp)
                    autor.text = "The Offspring"
                    titulo.text = "Pretty Fly"
                    mPlayerAdapter.play()
                    songProgress = 4}
                4 -> {mPlayerAdapter.loadMedia(R.raw.song5)
                    play.setImageResource(R.drawable.ic_pause_white_24dp)
                    autor.text = "Avenged Sevenfold"
                    titulo.text = "Hail to the king"
                    mPlayerAdapter.play()
                    songProgress = 5}
                5 -> {
                    var song: Song? = getSong("123456789")
                    if (song != null) {
                        autor.text = song.authorName
                        titulo.text = song.name
                        mPlayerAdapter.loadMedia(song.fileURL)
                        Picasso.get().load(song.imageURL).into(cover_image)
                        mPlayerAdapter.play()
                        songProgress = 1
                    }
                }
                else ->{mPlayerAdapter.loadMedia(R.raw.song1)
                    play.setImageResource(R.drawable.ic_pause_white_24dp)
                    autor.text="Linkin park"
                    titulo.text="Numb"
                    mPlayerAdapter.play()
                    songProgress = 1}
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_mini_player, container, false)
    }

    /**
     * Se inicializa el controlador del reproductor
     */
    private fun initializePlaybackController() {
        var mMediaPlayerHolder = MediaPlayerHolder(mContext!!)
        mMediaPlayerHolder.setPlaybackInfoListener(PlaybackListener())
        mPlayerAdapter = mMediaPlayerHolder
    }

    /**
     * Inicializa la vista del reproductor
     */
    private fun initializePlayerView() {
        play.setOnClickListener(
                object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        if (mPlayerAdapter.isPlaying()) {
                            play.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                            mPlayerAdapter.pause()

                        } else {
                            mPlayerAdapter.play()
                            play.setImageResource(R.drawable.ic_pause_white_24dp)

                        }
                    }
                }
        )

        skip_prev.setOnClickListener(
                object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        mPlayerAdapter.reset()
                        mPlayerAdapter.play()
                    }
                }
        )

        initializeSeekbar()
    }

    /**
     * Inicializa la barra de progreso del reproductor
     */
    private fun initializeSeekbar() {
        seek_bar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    var userSelectedPosition = 0

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                        mUserIsSeeking = true
                    }

                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            userSelectedPosition = progress
                        }
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        mUserIsSeeking = false
                        mPlayerAdapter.seekTo(userSelectedPosition)
                    }
                }
        )
    }

    /**
     * Listener del reproductor
     */
    inner class PlaybackListener : PlaybackInfoListener() {

        override fun onDurationChanged(duration: Int) {
            seek_bar.max = duration

            var time: Int = duration/1000
            var timeM: Int = time/60
            var timeS: Int = time.rem(60)
            var timeStringM = if (timeM < 10) { "0$timeM" } else { timeM.toString() }
            var timeStringS = if (timeS < 10) { "0$timeS" } else { timeS.toString() }
            length.text = "$timeStringM:$timeStringS"
        }

        override fun onPositionChanged(position: Int) {
            if (!mUserIsSeeking) {
                seek_bar.progress = position
            }
        }

        override fun onStateChanged(state: PlaybackInfoListener.State) {
            when (state) {
                State.PLAYING -> actual_time.post(mUpdateTime)
            }
        }

        override fun onPlaybackCompleted() {

        }
    }


}// Required empty public constructor
