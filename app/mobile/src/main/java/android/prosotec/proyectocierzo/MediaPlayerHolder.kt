package android.prosotec.proyectocierzo

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.util.Log
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Clase que implementa el reproductor (Media Player)
 */
class MediaPlayerHolder : PlayerAdapter {

    private val PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 1000

    private var mContext: Context
    private var mMediaPlayer: MediaPlayer? = null
    private var mPlaybackInfoListener: PlaybackInfoListener? = null
    // Executor es un objeto que permite ejecutar tareas en paralelo
    private var mExecutor: ScheduledExecutorService? = null
    private var mSeekbarPositionUpdateTask: Runnable? = null
    private var mResourceId: Int? = null

    constructor(context: Context) {
        mContext = context.applicationContext
    }

    private fun initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer()
            mMediaPlayer?.setOnCompletionListener(MediaPlayer.OnCompletionListener {
                stopUpdatingCallbackWithPosition(true)
                if (mPlaybackInfoListener != null) {
                    mPlaybackInfoListener?.onStateChanged(PlaybackInfoListener.State.COMPLETED)
                    mPlaybackInfoListener?.onPlaybackCompleted()
                }
            })
        }
    }

    /**
     * Asigna un Listener
     */
    fun setPlaybackInfoListener(listener: PlaybackInfoListener) {
        mPlaybackInfoListener = listener
    }

    /**
     * Carga una canción en el MediaPlayer
     */
    override fun loadMedia(resourceID: Int?) {
        mResourceId = resourceID

        initializeMediaPlayer()

        var assetFileDescriptor: AssetFileDescriptor =
        mContext.resources.openRawResourceFd(R.raw.song1)

        try {
            mMediaPlayer?.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
        } catch (e: Exception) {
            Log.e("MPH_setDataSource", e.toString())
        }

        try {
            mMediaPlayer?.prepare()
        } catch (e: Exception) {
            Log.e("MPH_preprare", e.toString())
        }

        initializeProgressCallback()
    }

    /**
     * Libera el MediaPlayer
     */
    override fun release() {
        mMediaPlayer?.release()
        mMediaPlayer = null
    }

    /**
     * Devuelve si se está reproduciendo o no una canción
     */
    override fun isPlaying() : Boolean {
        return mMediaPlayer?.isPlaying ?: false
    }

    /**
     * Comienza a reproducir una canción
     */
    override fun play() {
        if (mMediaPlayer != null && !mMediaPlayer!!.isPlaying) {
            mMediaPlayer?.start()
            mPlaybackInfoListener?.onStateChanged(PlaybackInfoListener.State.PLAYING)
            startUpdatingCallbackWithPosition()
        }
    }

    /**
     * Devuelve al inicio la canción que se está reproduciendo
     */
    override fun reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer?.reset()
            loadMedia(mResourceId)
            mPlaybackInfoListener?.onStateChanged(PlaybackInfoListener.State.RESET)
            stopUpdatingCallbackWithPosition(true)
        }
    }

    /**
     * Pausa la canción en reproducción
     */
    override fun pause() {
        if (mMediaPlayer?.isPlaying == true) {
            mMediaPlayer?.pause()
            mPlaybackInfoListener?.onStateChanged(PlaybackInfoListener.State.PAUSED)
        }
    }

    /**
     * Mueve la reproducción a la posición indicada
     */
    override fun seekTo(position: Int) {
        mMediaPlayer?.seekTo(position)
    }


    /**
     * Inicializa el Callback para mandar información de la posición
     */
    override fun initializeProgressCallback() {
        val duration = mMediaPlayer?.duration ?: 0
        mPlaybackInfoListener?.onDurationChanged(duration)
        mPlaybackInfoListener?.onPositionChanged(0)
    }

    /**
     * Método para empezar a mandar actualizaciones de la posición.
     */
    private fun startUpdatingCallbackWithPosition() {
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor()
        }
        if (mSeekbarPositionUpdateTask == null) {
            // Crea un hilo/tarea donde ejecuta la actualización
            mSeekbarPositionUpdateTask = Runnable { updateProgressCallbackTask() }
        }
        mExecutor?.scheduleAtFixedRate(
                mSeekbarPositionUpdateTask,
                0,
                PLAYBACK_POSITION_REFRESH_INTERVAL_MS.toLong(),
                TimeUnit.MILLISECONDS
        )
    }

    /**
     * Método para dejar de mandar actualizaciones de la posición.
     */
    private fun stopUpdatingCallbackWithPosition(resetUIPlaybackPosition: Boolean) {
        mExecutor?.shutdownNow()
        mExecutor = null
        mSeekbarPositionUpdateTask = null
        if (resetUIPlaybackPosition != null) {
            mPlaybackInfoListener?.onPositionChanged(0)
        }

    }

    /**
     * Método que manda la posición actual
     */
    private fun updateProgressCallbackTask() {
        if (mMediaPlayer?.isPlaying == true) {
            val currentPosition = mMediaPlayer!!.currentPosition
            mPlaybackInfoListener?.onPositionChanged(currentPosition)
        }
    }

}