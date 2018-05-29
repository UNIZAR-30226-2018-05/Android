package android.prosotec.proyectocierzo

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.support.v4.app.FragmentActivity
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import com.example.android.mediasession.R
import kotlinx.android.synthetic.main.song_row_item.view.*
import java.util.*
import cierzo.model.objects.Playlist
import cierzo.model.objects.Song
import com.example.android.mediasession.CierzoApp
import com.example.android.mediasession.service.contentcatalogs.MusicLibrary
import com.koushikdutta.ion.Ion
import java.net.URL

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
class SongRowAdapter
// END_INCLUDE(recyclerViewSampleViewHolder)

/**
 * Initialize the dataset of the Adapter.
 *
 * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
 */
(private val activity: AppCompatActivity,
 private val mPlaylist: Playlist,
 private val showFavIcon: Boolean = true,
 private val showDragAndDropIcon: Boolean = true,
 private val showCrossIcon: Boolean = true,
 private val favAsRemove: Boolean = false) : RecyclerView.Adapter<SongRowAdapter.ViewHolder>(), ItemTouchHelperAdapter {
    private var mSongs: MutableList<Song> = mutableListOf()

    init {
        mSongs = mPlaylist.getSongs().toMutableList()
    }

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder(mPlaylist: Playlist, context: Context, v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView
        val artist: TextView
        var coverImage: ImageView
        val favIcon: CompoundButton

        init {
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener {
                var metadata: MediaMetadataCompat = MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mPlaylist.getInfo().elementAt(0) as String)
                        .build()
                if (context is Main2Activity) {
                    context.mMediaBrowserHelper.setQueue(metadata, adapterPosition)
                }
            }
            title = v.findViewById(R.id.text_row_title) as TextView
            artist = v.findViewById(R.id.text_row_artist) as TextView
            coverImage = v.findViewById(R.id.text_row_cover_image) as ImageView
            favIcon = v.findViewById(R.id.text_row_fav_icon) as CompoundButton
        }
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view.
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.song_row_item, viewGroup, false)

        if (!showFavIcon) {
            v.text_row_fav_icon.visibility = View.GONE
        } else {
            v.text_row_fav_icon.setOnCheckedChangeListener(CheckListener())
        }

        if (!showDragAndDropIcon) {
            v.text_row_dragAndDrop_icon.visibility = View.GONE
        }

        if (!showCrossIcon) {
            v.text_row_cross_icon.visibility = View.GONE
        }

        return ViewHolder(mPlaylist, activity, v)
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.d(TAG, "Element $position set.")

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.favIcon.isChecked = mPlaylist.getInfo().elementAt(1).equals("Favoritos")
        viewHolder.title.text = mSongs[position].name
        viewHolder.artist.text = mSongs[position].authorName
        viewHolder.favIcon.text = position.toString()
        viewHolder.coverImage.setOnClickListener({loadPlaylist()})
        viewHolder.coverImage.setImageBitmap(MusicLibrary.getAlbumBitmap(activity, mSongs[position].id))
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return mSongs.size
    }

    override fun onItemDismiss(position: Int) {
        mSongs.removeAt(position)
        if (favAsRemove) {
            RemoveFromFavTask().execute(position)
        }
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(mSongs, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mSongs, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    fun loadPlaylist() {
        var children: MutableList<MediaBrowserCompat.MediaItem> = mutableListOf()

        var mediaDescription: MediaDescriptionCompat = MediaDescriptionCompat.Builder()
                .setMediaId(mPlaylist.getInfo().elementAt(0) as String)
                .build()

        children.add(MediaBrowserCompat.MediaItem(mediaDescription, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE))

        /*(activity as Main2Activity).mMediaBrowserHelper
                .mMediaBrowserSubscriptionCallback.onChildrenLoaded("root", children)*/
    }

    companion object {
        private val TAG = "SongRowAdapter"
    }

    inner class CheckListener : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            var index = if (buttonView?.text.toString().equals("")) {-1}
                else {buttonView?.text.toString().toInt()}
            when (buttonView?.id) {
                R.id.text_row_fav_icon -> if (!isChecked && favAsRemove) {
                    if (index != -1) {onItemDismiss(index)}
                } else if (isChecked){
                    if (index != -1) { AddToFavTask().execute(index).get() }
                }
            }
        }
    }

    inner class RemoveFromFavTask : AsyncTask<Int, Void, Boolean>() {
        override fun doInBackground(vararg params: Int?): Boolean {
            if (params[0] != null) {
                (activity.application as CierzoApp).mUserLogged.removeFromFavorite(params[0]!!)
                return true
            } else {
                return false
            }
        }
    }

    inner class AddToFavTask : AsyncTask<Int, Void, Boolean>() {
        override fun doInBackground(vararg params: Int?): Boolean {
            if (params[0] != null) {
                (activity.application as CierzoApp).mUserLogged.addToFavorite(mSongs[params[0]!!])
                return true
            } else {
                return false
            }
        }
    }
}
