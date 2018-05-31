package android.prosotec.proyectocierzo.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.prosotec.proyectocierzo.Main2Activity
import android.prosotec.proyectocierzo.view.MiniPlayerView
import android.support.v4.app.Fragment
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PlayerFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayerFragment : Fragment() {

    // Variables y valores para el fragment

    private var mAlbumArt: ImageView? = null
    private var mTitleTextView: TextView? = null
    private var mArtistTextView: TextView? = null
    private var mSeekBarAudio: MediaSeekBar? = null
    private var mPlayButton: ImageButton? = null
    private var mCurrentTime: MediaCurrentTime? = null
    private var mFinalTime: MediaFinalTime? = null

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


    // Funciones propias del fragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mTitleTextView = view?.findViewById(R.id.titulo)
        mArtistTextView = view?.findViewById(R.id.autor)
        mAlbumArt = view?.findViewById(R.id.cover_image)
        mSeekBarAudio = view?.findViewById(R.id.seek_bar)
        mPlayButton = view?.findViewById(R.id.play)
        mCurrentTime = view?.findViewById(R.id.actual_time)
        mFinalTime = view?.findViewById(R.id.length)

        val clickListener = ClickListener()
        view?.findViewById<View>(R.id.skip_prev)?.setOnClickListener(clickListener)
        view?.findViewById<View>(R.id.play)?.setOnClickListener(clickListener)
        view?.findViewById<View>(R.id.skip_next)?.setOnClickListener(clickListener)

        mMediaBrowserHelper = MediaBrowserConnection(context!!)
        mMediaBrowserHelper.registerCallback(MediaBrowserListener())
        mMediaBrowserHelper.onStart()

        return inflater!!.inflate(R.layout.fragment_player, container, false)

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
                    mPlayButton?.setImageResource(R.drawable.ic_play_arrow_white_24dp)

                } else {
                    mMediaBrowserHelper.getTransportControls().play()
                    mPlayButton?.setImageResource(R.drawable.ic_pause_white_24dp)
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
                    context,
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


