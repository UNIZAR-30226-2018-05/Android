package android.prosotec.proyectocierzo

/**
 * Clase que puede recibir las actualizaciones de tiempo y estado del reproductor
 */
abstract class PlaybackInfoListener {

    enum class State(val value: String) {
        INVALID("INVALID"),
        PLAYING("PLAYING"),
        PAUSED("PAUSED"),
        RESET("RESET"),
        COMPLETED("COMPLETED")
    }

    internal abstract fun onDurationChanged(duration: Int)

    internal abstract fun onPositionChanged(position: Int)

    internal abstract fun onStateChanged(state: PlaybackInfoListener.State)

    internal abstract fun onPlaybackCompleted()

}