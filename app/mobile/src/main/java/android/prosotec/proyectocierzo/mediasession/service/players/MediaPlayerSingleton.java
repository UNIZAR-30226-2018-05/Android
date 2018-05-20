package android.prosotec.proyectocierzo.mediasession.service.players;

import android.media.MediaPlayer;

public class MediaPlayerSingleton extends MediaPlayer {
    private static MediaPlayerSingleton mediaPlayerSingleton;

    private MediaPlayerSingleton() {}

    public static MediaPlayerSingleton getInstance() {
        synchronized (mediaPlayerSingleton)  {
            if(mediaPlayerSingleton == null)
                mediaPlayerSingleton = new MediaPlayerSingleton();
        }

        return mediaPlayerSingleton;
    }

}