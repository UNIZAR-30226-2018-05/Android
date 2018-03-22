package android.prosotec.proyectocierzo

/**
 * Created by ccucr on 21/03/2018.
 */
interface PlayerAdapter {

    fun loadMedia(resourceID: Int?)

    fun release()

    fun isPlaying() : Boolean

    fun play()

    fun reset()

    fun pause()

    fun initializeProgressCallback()

    fun seekTo(position: Int)

}