package android.prosotec.proyectocierzo

/**
 * Created by ccucr on 21/03/2018.
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