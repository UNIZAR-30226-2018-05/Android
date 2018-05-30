package android.prosotec.proyectocierzo

import android.content.Context
import android.os.Bundle
import android.prosotec.proyectocierzo.fragment.recyclerview.SongRowRecyclerViewFragment
import android.support.v7.app.AppCompatActivity
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
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_main2.*

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
(private val mDataSet: List<Any>,
 private val activity: AppCompatActivity) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    private var mPlaylists: List<Playlist>? = null
    private var mAlbums: List<Album>? = null

    init {
        if (mDataSet.size > 0 && mDataSet.elementAt(0) is Playlist) {
            mPlaylists = mDataSet.distinctBy { (it as Playlist).getInfo().elementAt(0) } as List<Playlist>
        } else if (mDataSet.size > 0 && mDataSet.elementAt(0) is Album) {
            mAlbums = mDataSet.distinctBy { (it as Album).id } as List<Album>
        }
    }

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder(v: View, listAux: List<Any>,context: Context) : RecyclerView.ViewHolder(v) {
        val card_title: TextView
        val card_subtitle: TextView
        val card_image: ImageView

        init {
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener {
                if(listAux.elementAt(0) is Playlist){
                    if (context is Main2Activity) {
                        var playlistAux: Playlist = listAux.get(adapterPosition) as Playlist
                        var infoPlay: Set<Any> = playlistAux.getInfo()
                        context.cTitle.text = infoPlay.elementAt(1) as String
                        context.cOwner.text = playlistAux.getOwner().getInfo().elementAt(2)
                        context.cBio.text = infoPlay.elementAt(2) as String
                        var imageURL = infoPlay.elementAt(4) as String
                        Ion.with(context.cImage)
                                .placeholder(R.drawable.gray_background)
                                .load(imageURL)
                        context.cards_view.visibility = View.VISIBLE
                        context.main_layout.visibility = View.GONE

                        var bundleSongs = Bundle();
                        bundleSongs.putInt("MODE", SongRowRecyclerViewFragment.MODE_PLAYLIST_SONGS)
                        bundleSongs.putString("playlistId", infoPlay.elementAt(0) as String)
                        var fragmentSongs = SongRowRecyclerViewFragment()
                        fragmentSongs.setArguments(bundleSongs)
                        var transactionS = context.supportFragmentManager.beginTransaction()
                        transactionS.replace(R.id.card_view_contents, fragmentSongs as android.support.v4.app.Fragment)
                        transactionS.addToBackStack(null)
                        transactionS.commit()
                    }
                }else if(listAux.elementAt(0) is Album){
                    if (context is Main2Activity) {
                        var albumAux: Album = listAux.get(adapterPosition) as Album
                        context.cTitle.text = albumAux.name
                        context.cOwner.text = albumAux.authorName
                        context.cBio.text = albumAux.desc
                        var imageURL = albumAux.imageURL
                        Ion.with(context.cImage)
                                .placeholder(R.drawable.gray_background)
                                .load(imageURL)
                        context.cards_view.visibility = View.VISIBLE
                        context.main_layout.visibility = View.GONE

                        var bundleSongs = Bundle();
                        bundleSongs.putInt("MODE", SongRowRecyclerViewFragment.MODE_ALBUM_SONGS)
                        bundleSongs.putString("playlistId", albumAux.id )
                        var fragmentSongs = SongRowRecyclerViewFragment()
                        fragmentSongs.setArguments(bundleSongs)
                        var transactionS = context.supportFragmentManager.beginTransaction()
                        transactionS.replace(R.id.card_view_contents, fragmentSongs as android.support.v4.app.Fragment)
                        transactionS.addToBackStack(null)
                        transactionS.commit()
                    }
                }
            }
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


        return ViewHolder(v,mDataSet,activity)
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
            if (mPlaylists!!.get(position).getSongs().size > 0) {
                Ion.with(viewHolder.card_image)
                        .placeholder(R.drawable.gray_background)
                        .load(imageURL)
            }

        } else if (mAlbums != null) {
            var album: Album = mAlbums!!.get(position)
            viewHolder.card_title.text = album.name
            viewHolder.card_subtitle.text = album.authorName
            var imageURL: String = album.imageURL
            if (mAlbums!!.get(position).songs.size > 0) {
                Ion.with(viewHolder.card_image)
                        .placeholder(R.drawable.gray_background)
                        .load(imageURL)
            }
        }

    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return if (mPlaylists != null) {
            mPlaylists!!.size
        } else if (mAlbums != null) {
            mAlbums!!.size
        } else {
            mDataSet.size
        }
    }

    companion object {
        private val TAG = "CardAdapter"
    }
}
