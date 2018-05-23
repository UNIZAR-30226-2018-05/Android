package android.prosotec.proyectocierzo

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_local_player.*
import java.io.File
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.example.android.mediasession.R
import com.example.android.mediasession.client.MediaBrowserHelper
import com.example.android.mediasession.service.MusicService
import com.example.android.mediasession.service.contentcatalogs.MusicLibrary
import com.example.android.mediasession.ui.MediaSeekBar

import com.chibde.visualizer.LineVisualizer
import com.example.android.mediasession.service.players.MediaPlayerAdapter.MEDIA_PLAYER_ID

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class LocalPlayerActivity : AppCompatActivity() {
    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        fullscreen_content.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
    }
    private var mVisible: Boolean = false


    private lateinit var mMediaBrowserHelper: MediaBrowserConnection
    private var mSeekBarAudio: MediaSeekBar? = null

    private var mTitleTextView: TextView? = null
    private var mArtistTextView: TextView? = null
    private var mAlbumArt: ImageView? = null
    private var mPlayButton: ImageButton? = null
    private var mlineVisualizer: LineVisualizer? = null

    private var mIsPlaying: Boolean = false

    private var uri: Uri? = null

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_local_player)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mSeekBarAudio = findViewById(R.id.seek_bar)
        mTitleTextView = findViewById(R.id.song_name)
        mArtistTextView = findViewById(R.id.artist_name)
        mAlbumArt = findViewById(R.id.cover_image)
        mPlayButton = findViewById(R.id.play)

        val clickListener = ClickListener()
        findViewById<View>(R.id.ib_prev).setOnClickListener(clickListener)
        findViewById<View>(R.id.play).setOnClickListener(clickListener)
        findViewById<View>(R.id.ib_next).setOnClickListener(clickListener)

        mVisible = true

        prepareSong()
        mMediaBrowserHelper = MediaBrowserConnection(this)
        mMediaBrowserHelper.registerCallback(MediaBrowserListener())


        mlineVisualizer = LineVisualizer(this)
        var mainLayout: LinearLayout = findViewById(R.id.fullscreen_content)
        mainLayout.addView(mlineVisualizer)
        mlineVisualizer?.setColor(ContextCompat.getColor(this, R.color.colorAccent))
        mlineVisualizer?.setStrokeWidth(5)

        var vCreatorTask: VisualizerCreatorTask = VisualizerCreatorTask()
        vCreatorTask.execute(this)
    }

    override fun onStart() {
        super.onStart()
        mMediaBrowserHelper.onStart()
    }

    override fun onStop() {
        super.onStop()
        mSeekBarAudio?.disconnectController()
        mMediaBrowserHelper.onStop()
        mlineVisualizer?.release()
    }

    private fun prepareSong(){
        // Código para capturar el intent de mp3 en local.
        if (Intent.ACTION_VIEW == intent.action) {
            var uriS = intent.dataString
            uri = intent.data
            //val Fpath = Commons.getPath(uri, this)
            //val file = File(Fpath)
            //val filename = file.getName()
           // song_name.setText(filename)
            //song_name.setSelected(true)

            MusicLibrary.replaceWithSong(uri, this)
        }
    }

    private inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            when (v.id) {
                R.id.skip_prev -> mMediaBrowserHelper.getTransportControls().skipToPrevious()
                R.id.play -> if (mIsPlaying) {
                    mMediaBrowserHelper.getTransportControls().pause()

                } else {
                    mMediaBrowserHelper.getTransportControls().play()
                }
                R.id.skip_next -> mMediaBrowserHelper.getTransportControls().skipToNext()
            }
        }
    }

    private inner class MediaBrowserConnection constructor(context: Context) : MediaBrowserHelper(context, MusicService::class.java) {

        override fun onConnected(mediaController: MediaControllerCompat) {
            mSeekBarAudio?.setMediaController(mediaController)
        }

        override fun onChildrenLoaded(parentId: String,
                                      children: List<MediaBrowserCompat.MediaItem>) {
            super.onChildrenLoaded(parentId, children)

            val mediaController = mediaController

            // Queue up all media items for this simple sample.
            for (mediaItem in children) {
                mediaController.addQueueItem(mediaItem.description)
            }

            // Call prepare now so pressing play just works.
            if (uri != null)
            mediaController.transportControls.prepare()

            // Se provoca un PlaybackStateChanged para que se recargue la seekbar
            if (!mIsPlaying) {
                mMediaBrowserHelper.getTransportControls().play()
                mMediaBrowserHelper.getTransportControls().pause()
            } else {
                mMediaBrowserHelper.getTransportControls().pause()
                mMediaBrowserHelper.getTransportControls().play()
            }
        }
    }

    private inner class MediaBrowserListener : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(playbackState: PlaybackStateCompat?) {
            mIsPlaying = playbackState != null && playbackState.state == PlaybackStateCompat.STATE_PLAYING
            mPlayButton?.setPressed(mIsPlaying)
        }

        override fun onMetadataChanged(mediaMetadata: MediaMetadataCompat?) {
            if (mediaMetadata == null) {
                return
            }
            mTitleTextView?.setText(
                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
            mArtistTextView?.setText(
                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
            mAlbumArt?.setImageBitmap(MusicLibrary.getAlbumBitmap(
                    this@LocalPlayerActivity,
                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)))
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
        }

        override fun onQueueChanged(queue: List<MediaSessionCompat.QueueItem>?) {
            super.onQueueChanged(queue)
        }
    }

    inner class VisualizerCreatorTask: AsyncTask<Context,Void,Boolean>() {
        override fun doInBackground(vararg params: Context?): Boolean {
            while (MEDIA_PLAYER_ID == -1) {
                Log.d("VisualizerCreatorTask", "Waiting for Media Player to initialize.")
                Thread.sleep(250)
            }
            Log.d("VisualizerCreatorTask", "Media Player initialized. ID: $MEDIA_PLAYER_ID")
            mlineVisualizer?.setPlayer(MEDIA_PLAYER_ID)
            return true
        }
    }
}
