package com.example.android.mediasession.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.widget.AppCompatSeekBar
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import android.widget.TextView

class MediaFinalTime(context: Context, attrs: AttributeSet) : TextView(context, attrs){

    // Pasa un entero a un String con formato de tiempo
    fun setDuration(duration: Int){
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
}