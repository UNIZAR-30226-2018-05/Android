package android.prosotec.proyectocierzo

/**
 * Adaptador para las clases que implementen el reproductor (Media Player)
 */
interface PlayerAdapter {

    fun loadMedia(resourceID: Int?)

    fun loadMedia(URL: String)

    fun release()

    fun isPlaying() : Boolean

    fun play()

    fun reset()

    fun pause()

    fun initializeProgressCallback()

    fun seekTo(position: Int)

}