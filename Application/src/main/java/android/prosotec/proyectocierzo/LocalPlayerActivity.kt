package android.prosotec.proyectocierzo

///////////////////////////////////
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_local_player.*
///////////////////////////


import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v4.content.ContextCompat
import android.widget.*

import com.chibde.visualizer.LineVisualizer
import com.example.android.mediasession.R
import android.media.MediaMetadataRetriever
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.media.audiofx.Equalizer
import android.util.Log
import com.example.android.mediasession.service.players.MediaPlayerAdapter
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.equalizer_view.*
import kotlinx.android.synthetic.main.view_player.*
import java.io.File


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class LocalPlayerActivity : AppCompatActivity() {

    private var mlineVisualizer: LineVisualizer? = null
    private var mMediaPlayer = MediaPlayer()
    private var mEqualizer: Equalizer? = null

    private val mSeekbarUpdateHandler = Handler()
    private val mUpdateSeekbar = object : Runnable {
        override fun run() {
            seek_bar_local.setProgress(mMediaPlayer.currentPosition)
            current_time_l.text = setTextTime(mMediaPlayer.currentPosition)
            mSeekbarUpdateHandler.postDelayed(this, 50)
        }
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_local_player)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val clickListener = ClickListener()
        findViewById<View>(R.id.ib_prev_l).setOnClickListener(clickListener)
        findViewById<View>(R.id.play_l).setOnClickListener(clickListener)
        findViewById<View>(R.id.ib_next_l).setOnClickListener(clickListener)
        equalizer_view_l.visibility = View.GONE
        equalizer_l.setOnClickListener{
            equalizer_view_l.visibility = View.VISIBLE
            fullscreen_content_l.visibility = View.GONE
        }
        back_eq.setOnClickListener{
            equalizer_view_l.visibility = View.GONE
            fullscreen_content_l.visibility = View.VISIBLE
        }


        prepareSong()
        prepareUI()
        prepareVisualizer()
        mEqualizer = Equalizer(0, mMediaPlayer.audioSessionId)
        mEqualizer?.setEnabled(true)
        prepareButtonsEqualizer()
        prepareButtonsEqualizerListeners()
    }

    private fun prepareButtonsEqualizer(){
        pre1.text = mEqualizer?.getPresetName(0)
        pre2.text = mEqualizer?.getPresetName(1)
        pre3.text = mEqualizer?.getPresetName(2)
        pre4.text = mEqualizer?.getPresetName(3)
        pre5.text = mEqualizer?.getPresetName(4)
        pre6.text = mEqualizer?.getPresetName(5)
        pre7.text = mEqualizer?.getPresetName(6)
        pre8.text = mEqualizer?.getPresetName(7)
        pre9.text = mEqualizer?.getPresetName(8)
        pre10.text = mEqualizer?.getPresetName(9)
    }

    private fun prepareButtonsEqualizerListeners(){
        pre1.setOnClickListener{
            mEqualizer?.usePreset(0)
        }
        pre2.setOnClickListener{
            mEqualizer?.usePreset(1)
        }
        pre3.setOnClickListener{
            mEqualizer?.usePreset(2)
        }
        pre4.setOnClickListener{
            mEqualizer?.usePreset(3)
        }
        pre5.setOnClickListener{
            mEqualizer?.usePreset(4)
        }
        pre6.setOnClickListener{
            mEqualizer?.usePreset(5)
        }
        pre7.setOnClickListener{
            mEqualizer?.usePreset(6)
        }
        pre8.setOnClickListener{
            mEqualizer?.usePreset(7)
        }
        pre9.setOnClickListener{
            mEqualizer?.usePreset(8)
        }
        pre10.setOnClickListener{
            mEqualizer?.usePreset(9)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer.release()
        mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar)
        mlineVisualizer?.release()
        mEqualizer?.release()
    }

     private fun prepareSong(){
        // Código para capturar el intent de mp3 en local.
        if (Intent.ACTION_VIEW == intent.action) {
            val uri = intent.data
            val fileName = File(uri.path).name
            val metaRetriver: MediaMetadataRetriever
            metaRetriver = MediaMetadataRetriever()
            metaRetriver.setDataSource(this, uri)
            val titulo = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            if(titulo != null){
                song_name_l.text = titulo
            }else{
                song_name_l.text = fileName
            }
            artist_name_l.text = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)

            val cover = metaRetriver.embeddedPicture
            if(cover != null){
                val image = BitmapFactory.decodeByteArray(cover, 0, cover.size)
                cover_image_1.setImageBitmap(image)
            }


            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer.setDataSource(getApplicationContext(), uri)
            mMediaPlayer.prepare()
            mMediaPlayer.start()
        }
    }

    private inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            when (v.id) {
                R.id.ib_prev_l -> mMediaPlayer.seekTo(mMediaPlayer.currentPosition - 10000)
                R.id.play_l -> if (mMediaPlayer.isPlaying) {
                    mMediaPlayer.pause()
                    play_l.isPressed  = true
                    play_l.setImageResource(R.drawable.ic_play_arrow_white_24dp)

                } else {
                    mMediaPlayer.start()
                    play_l.isPressed = false
                    play_l.setImageResource(R.drawable.ic_pause_white_24dp)
                }
                R.id.ib_next_l -> mMediaPlayer.seekTo(mMediaPlayer.currentPosition + 30000)
            }
        }
    }

    private fun prepareVisualizer(){
        mlineVisualizer = LineVisualizer(this)
        fullscreen_content_l.addView(mlineVisualizer)
        mlineVisualizer?.setColor(ContextCompat.getColor(this, R.color.colorAccent))
        mlineVisualizer?.setStrokeWidth(5)
        mlineVisualizer?.setPlayer(mMediaPlayer)
    }

    private fun prepareUI(){
        seek_bar_local.max = mMediaPlayer.duration
        final_time_l.text = setTextTime(mMediaPlayer.duration)
        current_time_l.text = setTextTime(0)
        mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);

        seek_bar_local.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Write code to perform some action when progress is changed.
                if(fromUser){
                    mMediaPlayer.seekTo(progress)
                    current_time_l.text = setTextTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is started.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is stopped.
                current_time_l.text = setTextTime(mMediaPlayer.currentPosition)
            }
        })
    }

    private fun setTextTime(duration: Int): String{
        var finalTimerString: String
        var secondsString: String

        val minutes  = (duration % (1000*60*60) / (1000*60))
        val seconds =  (duration % (1000*60*60) % (1000*60) / 1000)

        if(seconds < 10){
            secondsString = "0" + seconds
        }else{
            secondsString = "" + seconds
        }

        finalTimerString = "" + minutes + ":" + secondsString
        return finalTimerString
    }

}
