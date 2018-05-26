package android.prosotec.proyectocierzo

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cierzo.model.objects.Album
import cierzo.model.objects.Playlist
import com.example.android.mediasession.R
import com.squareup.picasso.Picasso

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
class CardAdapter
// END_INCLUDE(recyclerViewSampleViewHolder)

/**
 * Initialize the dataset of the Adapter.
 *
 * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
 */
(private val mDataSet: List<Any>) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    private var mPlaylists: List<Playlist>? = null
    private var mAlbums: List<Album>? = null

    init {
        if (mDataSet.size > 0 && mDataSet.elementAt(0) is Playlist) {
            mPlaylists = mDataSet as List<Playlist>
        } else if (mDataSet.size > 0 && mDataSet.elementAt(0) is Album) {
            mAlbums = mDataSet as List<Album>
        }
    }

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val card_title: TextView
        val card_subtitle: TextView
        val card_image: ImageView

        init {
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener { Log.d(TAG, "Element $adapterPosition clicked.") }
            card_title = v.findViewById(R.id.card_title) as TextView
            card_subtitle = v.findViewById(R.id.card_subtitle) as TextView
            card_image = v.findViewById(R.id.card_image) as ImageView
        }
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view.
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.card_row_item, viewGroup, false)

        return ViewHolder(v)
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.d(TAG, "Element $position set.")

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        if (mPlaylists != null) {
            var playlistInfo: Set<Any> = mPlaylists!!.get(position).getInfo()
            viewHolder.card_title.text = playlistInfo.elementAt(1) as String
            viewHolder.card_subtitle.text = "${playlistInfo.elementAt(5) as Int} canciones"
            var imageURL: String = playlistInfo.elementAt(4) as String
            Picasso.get().load(imageURL).into(viewHolder.card_image);

        } else if (mAlbums != null) {
            var album: Album = mAlbums!!.get(position)
            viewHolder.card_title.text = album.name
            viewHolder.card_subtitle.text = album.authorName
        }

    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return mDataSet.size
    }

    companion object {
        private val TAG = "CardAdapter"
    }
}
