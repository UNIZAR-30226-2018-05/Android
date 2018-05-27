package com.example.android.mediasession

import android.support.v4.media.session.MediaSessionCompat

object ShuffleTool {

    fun shuffleList(mOriginalList: MutableList<MediaSessionCompat.QueueItem>, index: Int): List<MediaSessionCompat.QueueItem>{
        var returnList: MutableList<MediaSessionCompat.QueueItem> = mutableListOf()
        returnList.addAll(mOriginalList.subList(0,index+1))
        returnList.addAll(mOriginalList.subList(index+1,mOriginalList.lastIndex+1).shuffled())
        return returnList
    }



}