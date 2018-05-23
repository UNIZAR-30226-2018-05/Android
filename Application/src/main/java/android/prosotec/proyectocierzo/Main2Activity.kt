package android.prosotec.proyectocierzo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.prosotec.proyectocierzo.fragment.*
import android.prosotec.proyectocierzo.fragment.recyclerview.CardsRecyclerViewFragment
import android.prosotec.proyectocierzo.fragment.recyclerview.PersonRowRecyclerViewFragment
import android.prosotec.proyectocierzo.fragment.recyclerview.SongRowRecyclerViewFragment
import android.prosotec.proyectocierzo.view.MiniPlayerView
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.android.mediasession.R
import com.example.android.mediasession.client.MediaBrowserHelper
import com.example.android.mediasession.service.MusicService
import com.example.android.mediasession.service.contentcatalogs.MusicLibrary
import com.example.android.mediasession.ui.MediaCurrentTime
import com.example.android.mediasession.ui.MediaFinalTime
import com.example.android.mediasession.ui.MediaSeekBar

import kotlinx.android.synthetic.main.activity_main2.*


class Main2Activity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private var mAlbumArt: ImageView? = null
    private var mTitleTextView: TextView? = null
    private var mArtistTextView: TextView? = null
    private var mSeekBarAudio: MediaSeekBar? = null
    private var mPlayButton: ImageButton? = null
    private var mCurrentTime: MediaCurrentTime? = null
    private var mFinalTime: MediaFinalTime? = null

    // Valores estáticos para codificar las peticiones de permisos
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1
    private val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 2
    private val MY_PERMISSIONS_REQUEST_WAKE_LOCK = 3
    private val MY_PERMISSIONS_REQUEST_INTERNET = 4

    private lateinit var mMiniPlayer: MiniPlayerView

    private lateinit var mMediaBrowserHelper: MediaBrowserHelper

    private var mIsPlaying: Boolean = false

    private var mUpdateTime: Runnable = object : Runnable {
        override fun run() {
            if (mIsPlaying) {
                mCurrentTime?.setCurrentTime(mTime)
                mTime += 250
                mCurrentTime?.postDelayed(this, 250)
            } else if (!mIsPlaying) {
                mCurrentTime?.removeCallbacks(this)
                mUpdaterRunning = false
            }
        }
    }

    private var mUpdaterRunning: Boolean = false
    private var mTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        // Para reproductor
        mMiniPlayer = mini_player_view

        setSupportActionBar(toolbar)
        getSupportActionBar()!!.setDisplayShowTitleEnabled(false)  /* Quitamos el título de la barra*/

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter


        // Set up the ViewPager with the sections adapter.
        val mViewPager = findViewById(R.id.container) as ViewPager
        mViewPager.setAdapter(mSectionsPagerAdapter)

        val tabLayout = findViewById(R.id.tabs) as TabLayout

        mViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(mViewPager))

        // Código para el reproductor

        mTitleTextView = mMiniPlayer.findViewById(R.id.titulo)
        mArtistTextView = mMiniPlayer.findViewById(R.id.autor)
        mAlbumArt = mMiniPlayer.findViewById(R.id.cover_image)
        mSeekBarAudio = mMiniPlayer.findViewById(R.id.seek_bar)
        mPlayButton = mMiniPlayer.findViewById(R.id.play)
        mCurrentTime = mMiniPlayer.findViewById(R.id.actual_time)
        mFinalTime = mMiniPlayer.findViewById(R.id.length)


        val clickListener = ClickListener()
        mMiniPlayer.findViewById<View>(R.id.skip_prev).setOnClickListener(clickListener)
        mMiniPlayer.findViewById<View>(R.id.play).setOnClickListener(clickListener)
        mMiniPlayer.findViewById<View>(R.id.skip_next).setOnClickListener(clickListener)

        mMediaBrowserHelper = MediaBrowserConnection(this)
        mMediaBrowserHelper.registerCallback(MediaBrowserListener())

        // Código para los permisos
        //checkPermissions(this@Main2Activity)
    }

    /*
    // Función para comprobar si se tienen los permisos necesarios
    fun checkPermissions(actividad: Activity) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(actividad,            // Permiso para acceder al almacenamiento
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(actividad,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(actividad,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            }
        } else if (ContextCompat.checkSelfPermission(actividad,     // Permiso de grabar audio
                        Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(actividad,
                            Manifest.permission.RECORD_AUDIO)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                ActivityCompat.requestPermissions(actividad,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

            }
        } else if (ContextCompat.checkSelfPermission(actividad,     // Permiso de internet
                         Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(actividad,
                         Manifest.permission.INTERNET)) {

            } else {

                ActivityCompat.requestPermissions(actividad,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        MY_PERMISSIONS_REQUEST_INTERNET);
            }
        }else if (ContextCompat.checkSelfPermission(actividad,     // Permiso de bloqueo
                        Manifest.permission.WAKE_LOCK)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(actividad,
                            Manifest.permission.WAKE_LOCK)) {

            } else {

                ActivityCompat.requestPermissions(actividad,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        MY_PERMISSIONS_REQUEST_WAKE_LOCK)
            }
        }
    }

    // Funcion para captar cambios en los permisos por parte del usuario
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                }
                return
            }MY_PERMISSIONS_REQUEST_RECORD_AUDIO -> {
                if (grantResults.size > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                }
                return

            }MY_PERMISSIONS_REQUEST_INTERNET ->{
                if (grantResults.size > 0 && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                }
                return

            }MY_PERMISSIONS_REQUEST_WAKE_LOCK ->{
                if (grantResults.size > 0 && grantResults[3] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                }
                return

            }
        }
    }*/

    override fun onStart() {
        super.onStart()
        mMediaBrowserHelper.onStart()
    }

     override fun onStop() {
        super.onStop()
        mSeekBarAudio?.disconnectController()
        mMediaBrowserHelper.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return if (position == 0) {
                CardsRecyclerViewFragment()
            } else if (position == 1) {
                SongRowRecyclerViewFragment()
            } else if (position == 3) {
                PersonRowRecyclerViewFragment()
            } else {
                CardsRecyclerViewFragment()
            }
        }

        override fun getCount(): Int {
            // Show 4 total pages.
            return 4
        }
    }

    // Para reproductor

    /**
     * Convenience class to collect the click listeners together.
     *
     *
     * In a larger app it's better to split the listeners out or to use your favorite
     * library.
     */
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

    /**
     * Customize the connection to our [android.support.v4.media.MediaBrowserServiceCompat]
     * and implement our app specific desires.
     */
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

    /**
     * Implementation of the [MediaControllerCompat.Callback] methods we're interested in.
     *
     *
     * Here would also be where one could override
     * `onQueueChanged(List<MediaSessionCompat.QueueItem> queue)` to get informed when items
     * are added or removed from the queue. We don't do this here in order to keep the UI
     * simple.
     */
    private inner class MediaBrowserListener : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(playbackState: PlaybackStateCompat?) {
            mIsPlaying = playbackState != null && playbackState.state == PlaybackStateCompat.STATE_PLAYING
            mPlayButton?.setPressed(mIsPlaying)

            mTime = playbackState?.position?.toInt() ?: 0

            if (!mUpdaterRunning) {
                mUpdaterRunning = true
                mCurrentTime?.post(mUpdateTime)
            }
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
                    this@Main2Activity,
                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)))
            mFinalTime?.setDuration(mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION).toInt())
            mCurrentTime?.setCurrentTime(0)
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
        }

        override fun onQueueChanged(queue: List<MediaSessionCompat.QueueItem>?) {
            super.onQueueChanged(queue)
        }
    }

}
