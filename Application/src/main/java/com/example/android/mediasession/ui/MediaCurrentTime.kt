package com.example.android.mediasession.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.support.v4.media.session.MediaControllerCompat
import android.util.AttributeSet
import android.widget.TextView

class MediaCurrentTime(context: Context, attrs: AttributeSet) : TextView(context, attrs){

    fun setCurrentTime(duration: Int){
        var finalTimerString: String
        var secondsString: String

        val minutes  = (duration % (1000*60*60) / (1000*60))
        val seconds =  (duration % (1000*60*60) % (1000*60) / 1000)

        if(seconds < 10){
            secondsString = "0" + seconds
        }else{
            secondsString = "" + seconds
        }

        finalTimerString = "" + minutes + ":" + secondsString
        setText(finalTimerString)
    }

/*
    fun setMediaController(mediaController: MediaControllerCompat?) {
        if (mediaController != null) {
            mControllerCallback = ControllerCallback()
            mediaController.registerCallback(mControllerCallback)
        } else if (mMediaController != null) {
            mMediaController.unregisterCallback(mControllerCallback)
            mControllerCallback = null
        }
        mMediaController = mediaController
    }*/
}