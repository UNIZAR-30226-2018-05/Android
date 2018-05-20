package android.prosotec.proyectocierzo

import android.content.Context
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
import android.prosotec.proyectocierzo.mediasession.client.MediaBrowserHelper
import android.prosotec.proyectocierzo.mediasession.service.MusicService
import android.prosotec.proyectocierzo.mediasession.service.contentcatalogs.MusicLibrary
import android.prosotec.proyectocierzo.mediasession.ui.MediaSeekBar
import android.prosotec.proyectocierzo.view.MiniPlayerView
import android.support.design.widget.TabLayout
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.*
import android.widget.ImageView
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.view_new_mini_player.*


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
    private val mFragmentManager = supportFragmentManager
    private lateinit var mMediaBrowserHelper: MediaBrowserHelper

    private var mMiniPlayerView: MiniPlayerView? = null
    private var mAlbumArt: ImageView? = null
    private var mTitleTextView: TextView? = null
    private var mArtistTextView: TextView? = null
    private var mMediaControlsImage: ImageView? = null
    private var mSeekBarAudio: MediaSeekBar? = null

    private var mIsPlaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        /*if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = RecyclerViewFragment()
            transaction.replace(R.id.sample_content_fragment, fragment)
            transaction.commit()
        }*/

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

        mMiniPlayerView = this.findViewById(R.id.mini_player_view)
        mSeekBarAudio = mMiniPlayerView?.findViewById(R.id.seek_bar)
        mAlbumArt = mMiniPlayerView?.findViewById(R.id.cover_image)
        mTitleTextView = mMiniPlayerView?.findViewById(R.id.titulo)
        mArtistTextView = mMiniPlayerView?.findViewById(R.id.autor)

        val clickListener = ClickListener()
        mMiniPlayerView?.findViewById<View>(R.id.skip_prev)?.setOnClickListener(clickListener)
        mMiniPlayerView?.findViewById<View>(R.id.play)?.setOnClickListener(clickListener)
        mMiniPlayerView?.findViewById<View>(R.id.skip_next)?.setOnClickListener(clickListener)

        mMediaBrowserHelper = MediaBrowserConnection(this)
        mMediaBrowserHelper.registerCallback(MediaBrowserListener())
    }

    override fun onStart() {
        super.onStart()
        mMediaBrowserHelper.onStart()

    }

    override fun onStop() {
        super.onStop()
        mSeekBarAudio?.disconnectController()
        mMediaBrowserHelper.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaBrowserHelper.onDestroy()
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
     * A placeholder fragment containing a simple view.
     */
    /*class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            when (arguments?.getInt(ARG_SECTION_NUMBER)) {
                1 -> return inflater!!.inflate(R.layout.fragment_prueba, container, false)
                2 -> return inflater!!.inflate(R.layout.fragment_prueba2, container, false)
                3 -> return inflater!!.inflate(R.layout.fragment_prueba3, container, false)
                else -> return inflater!!.inflate(R.layout.fragment_player, container, false)
            }



        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }*/

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
            mMediaControlsImage?.setPressed(mIsPlaying)
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
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
        }

        override fun onQueueChanged(queue: List<MediaSessionCompat.QueueItem>?) {
            super.onQueueChanged(queue)
        }
    }

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
                R.id.skip_prev -> mMediaBrowserHelper.transportControls.skipToPrevious()
                R.id.play -> if (mIsPlaying) {
                    mMediaBrowserHelper.transportControls.pause()
                } else {
                    mMediaBrowserHelper.getTransportControls().play()
                }
                R.id.skip_next -> mMediaBrowserHelper.transportControls.skipToNext()
            }
        }
    }
}
