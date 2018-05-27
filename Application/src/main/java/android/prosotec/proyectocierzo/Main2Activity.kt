package android.prosotec.proyectocierzo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
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
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.app.AlertDialog
import android.view.*
import com.example.android.mediasession.R
import com.example.android.mediasession.client.MediaBrowserHelper
import com.example.android.mediasession.service.MusicService
import com.example.android.mediasession.service.contentcatalogs.MusicLibrary
import com.example.android.mediasession.ui.MediaCurrentTime
import com.example.android.mediasession.ui.MediaFinalTime
import com.example.android.mediasession.ui.MediaSeekBar


import android.Manifest.permission.INTERNET
import android.Manifest.permission.WAKE_LOCK
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.RECORD_AUDIO
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.prosotec.proyectocierzo.view.PlayerView
import android.widget.*
import io.swagger.client.ApiException
import kotlinx.android.synthetic.main.activity_local_player.*


import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.view_new_mini_player.*
import kotlinx.android.synthetic.main.view_player.*


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

    private var mAlbumArt2: ImageView? = null
    private var mTitleTextView2: TextView? = null
    private var mArtistTextView2: TextView? = null
    private var mSeekBarAudio2: MediaSeekBar? = null
    private var mPlayButton2: ImageButton? = null
    private var mCurrentTime2: MediaCurrentTime? = null
    private var mFinalTime2: MediaFinalTime? = null

    private var mPlayerFragment: Fragment? = PlayerFragment()

    private val mFragmentManager = supportFragmentManager

    private lateinit var mMiniPlayer: MiniPlayerView
    private lateinit var mPlayerBig: PlayerView

    private lateinit var mMediaBrowserHelper: MediaBrowserHelper

    private var mIsPlaying: Boolean = false

    private var mUpdateTime: Runnable = object : Runnable {
        override fun run() {
            if (mIsPlaying) {
                mCurrentTime?.setCurrentTime(mTime)
                mCurrentTime2?.setCurrentTime(mTime)
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


    private val PERMISSION_REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        mMiniPlayer = mini_player_view
        mPlayerBig = player_view

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

        checkPermission()
        requestPermission()

        // Código para el reproductor mini

        mTitleTextView = mMiniPlayer.findViewById(R.id.titulo)
        mArtistTextView = mMiniPlayer.findViewById(R.id.autor)
        mAlbumArt = mMiniPlayer.findViewById(R.id.cover_image)
        mSeekBarAudio = mMiniPlayer.findViewById(R.id.seek_bar)
        mPlayButton = mMiniPlayer.findViewById(R.id.play)
        mCurrentTime = mMiniPlayer.findViewById(R.id.actual_time)
        mFinalTime = mMiniPlayer.findViewById(R.id.length)

        // Código para el reproductor "grande"
        mTitleTextView2 = mPlayerBig.findViewById(R.id.song_name)
        mArtistTextView2 = mPlayerBig.findViewById(R.id.artist_name)
        mAlbumArt2 = mPlayerBig.findViewById(R.id.cover_image)
        mSeekBarAudio2 = mPlayerBig.findViewById(R.id.seek_bar)
        mPlayButton2 = mPlayerBig.findViewById(R.id.play)
        mCurrentTime2 = mPlayerBig.findViewById(R.id.current_time)
        mFinalTime2 = mPlayerBig.findViewById(R.id.final_time)


        val clickListener = ClickListener()
        mMiniPlayer.findViewById<View>(R.id.skip_prev).setOnClickListener(clickListener)
        mMiniPlayer.findViewById<View>(R.id.play).setOnClickListener(clickListener)
        mMiniPlayer.findViewById<View>(R.id.skip_next).setOnClickListener(clickListener)
        mPlayerBig.findViewById<View>(R.id.skip_prev).setOnClickListener(clickListener)
        mPlayerBig.findViewById<View>(R.id.play).setOnClickListener(clickListener)
        mPlayerBig.findViewById<View>(R.id.skip_next).setOnClickListener(clickListener)
        mPlayerBig.findViewById<View>(R.id.ib_shuffle).setOnClickListener(clickListener)
        mPlayerBig.findViewById<View>(R.id.ib_repeat).setOnClickListener(clickListener)

        mMediaBrowserHelper = MediaBrowserConnection(this)
        mMediaBrowserHelper.registerCallback(MediaBrowserListener())

        val checkListener = CheckListener()
        mPlayerBig.findViewById<CompoundButton>(R.id.ib_shuffle).setOnCheckedChangeListener(checkListener)
        mPlayerBig.findViewById<CompoundButton>(R.id.ib_repeat).setOnCheckedChangeListener(checkListener)

        player_back.setOnClickListener {
            player_view.visibility = View.GONE
            main_layout.visibility = View.VISIBLE
        }
        player_view.visibility = View.GONE
        mAlbumArt!!.setOnClickListener {
            player_view.visibility = View.VISIBLE
            main_layout.visibility = View.GONE
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            /*if(player_view.visibility == View.VISIBLE) {
                player_view.visibility = View.GONE
                main_layout.visibility = View.VISIBLE
            } else {*/
                finish()
            //}
            return true
        } else {
            return super.onKeyDown(keyCode, event)
        }


    }

    override fun onStart() {
        super.onStart()
        mMediaBrowserHelper.onStart()
    }

     override fun onStop() {
        super.onStop()
        mSeekBarAudio?.disconnectController()
         mSeekBarAudio2?.disconnectController()
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
        val intent: Intent
        when(id){
            R.id.menu_profile -> {
                intent = Intent(applicationContext,ListActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_signOut ->{
                LogoutTask().execute()
                intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

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
            return if (position == 0) { // Playlists
                var bundle: Bundle = Bundle();
                bundle.putInt("MODE", CardsRecyclerViewFragment.MODE_USERLOGGED_PLAYLISTS);
                var fragment: Fragment = CardsRecyclerViewFragment()
                fragment.setArguments(bundle)
                fragment
            } /*else if (position == 1) { // Canciones
                SongRowRecyclerViewFragment()
            } else if (position == 2) { // Albumes
                CardsRecyclerViewFragment()
            } else if (position == 3) { // Artistas
                PersonRowRecyclerViewFragment()
            }*/ else {
                PruebaFragment()
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
                R.id.skip_prev -> {mMediaBrowserHelper.getTransportControls().skipToPrevious()
                    mPlayButton?.setImageResource(R.drawable.ic_pause_white_24dp)
                    mPlayButton2?.setImageResource(R.drawable.ic_pause_white_24dp)}

                R.id.play -> if (mIsPlaying) {
                    mMediaBrowserHelper.getTransportControls().pause()
                    mPlayButton?.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                    mPlayButton2?.setImageResource(R.drawable.ic_play_arrow_white_24dp)

                } else {
                    mMediaBrowserHelper.getTransportControls().play()
                    mPlayButton?.setImageResource(R.drawable.ic_pause_white_24dp)
                    mPlayButton2?.setImageResource(R.drawable.ic_pause_white_24dp)
                }

                R.id.skip_next -> {mMediaBrowserHelper.getTransportControls().skipToNext()
                mPlayButton?.setImageResource(R.drawable.ic_pause_white_24dp)
                        mPlayButton2?.setImageResource(R.drawable.ic_pause_white_24dp)}
            }
        }
    }

    private inner class CheckListener : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            when(buttonView?.id){
                R.id.ib_shuffle -> if(isChecked){
                    mMediaBrowserHelper.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL)
                }else{
                    mMediaBrowserHelper.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE)
                }
                R.id.ib_repeat -> if(isChecked){
                    mMediaBrowserHelper.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
                }else{
                    mMediaBrowserHelper.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE)
                }
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
            mSeekBarAudio2?.setMediaController(mediaController)

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

        /*    // Se provoca un PlaybackStateChanged para que se recargue la seekbar
            if (!mIsPlaying) {
                mMediaBrowserHelper.getTransportControls().play()
                mMediaBrowserHelper.getTransportControls().pause()
            } else {
                mMediaBrowserHelper.getTransportControls().pause()
                mMediaBrowserHelper.getTransportControls().play()
            }*/
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
            mTitleTextView2?.setText(
                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
            mArtistTextView?.setText(
                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
            mArtistTextView2?.setText(
                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
            mAlbumArt?.setImageBitmap(MusicLibrary.getAlbumBitmap(
                    this@Main2Activity,
                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)))
            mAlbumArt2?.setImageBitmap(MusicLibrary.getAlbumBitmap(
                    this@Main2Activity,
                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)))
            mFinalTime?.setDuration(mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION).toInt())
            mFinalTime2?.setDuration(mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION).toInt())
            mCurrentTime?.setCurrentTime(0)
            mCurrentTime2?.setCurrentTime(0)
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
        }

        override fun onQueueChanged(queue: List<MediaSessionCompat.QueueItem>?) {
            super.onQueueChanged(queue)
        }

        /** AQUI SE CAMBIA EL ESTADO DE LOS BOTONES :D **/
        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            if(repeatMode == PlaybackStateCompat.REPEAT_MODE_ALL){
                Snackbar.make(window.decorView, "repeatMode = REPEAT_MODE_ALL", Snackbar.LENGTH_LONG).show()
            }else if(repeatMode == PlaybackStateCompat.REPEAT_MODE_NONE){
                Snackbar.make(window.decorView, "repeatMode = REPEAT_MODE_NONE", Snackbar.LENGTH_LONG).show()
            }
        }

        override fun onShuffleModeChanged(shuffleMode: Int) {
            super.onShuffleModeChanged(shuffleMode)
            if(shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL){
                Snackbar.make(window.decorView, "shuffleMode = SHUFFLE_MODE_ALL", Snackbar.LENGTH_LONG).show()
            }else if(shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_NONE){
                Snackbar.make(window.decorView, "shuffleMode = SHUFFLE_MODE_NONE", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    // Funciones para los permisos

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, INTERNET)
        val result1 = ContextCompat.checkSelfPermission(applicationContext, WAKE_LOCK)
        val result2 = ContextCompat.checkSelfPermission(applicationContext, READ_EXTERNAL_STORAGE)
        val result3 = ContextCompat.checkSelfPermission(applicationContext, RECORD_AUDIO)

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {

        ActivityCompat.requestPermissions(this, arrayOf(INTERNET, WAKE_LOCK, READ_EXTERNAL_STORAGE, RECORD_AUDIO), PERMISSION_REQUEST_CODE)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0) {

                val InternetAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val WakeLockAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                val StorageAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED
                val RecordAudioAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED


                if (InternetAccepted && WakeLockAccepted && StorageAccepted && RecordAudioAccepted )
                    Snackbar.make(window.decorView, "Permisos para la aplicación garantizados.", Snackbar.LENGTH_LONG).show()
                else {

                    Snackbar.make(window.decorView, "Permisos para la aplicación denegados, puede causar que la aplicación no funcione.", Snackbar.LENGTH_LONG).show()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(INTERNET)) {
                            showMessageOKCancel("La aplicación necesita de todos los permisos para funcionar correctamente",
                                    DialogInterface.OnClickListener { dialog, which ->
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(arrayOf(INTERNET, WAKE_LOCK, READ_EXTERNAL_STORAGE, RECORD_AUDIO), PERMISSION_REQUEST_CODE)
                                        }
                                    })
                            return
                        }
                    }

                }
            }
        }
    }


    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@Main2Activity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

    private inner class LogoutTask : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            try {
                cierzo.model.logout()
                return true
            } catch (e: ApiException) {
                Log.e("Main2Activity", e.responseBody)
                return false
            }
        }
    }


}
