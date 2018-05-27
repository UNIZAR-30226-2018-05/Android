package android.prosotec.proyectocierzo.fragment.recyclerview

import android.content.res.Configuration
import android.os.Bundle
import android.prosotec.proyectocierzo.CardAdapter
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.mediasession.R
import android.R.attr.defaultValue
import android.R.attr.key
import android.os.AsyncTask
import cierzo.model.objects.Playlist
import com.example.android.mediasession.CierzoApp


/**
 * Demonstrates the use of [RecyclerView] with a [LinearLayoutManager] and a
 * [GridLayoutManager].
 */
class CardsRecyclerViewFragment : Fragment() {

    protected lateinit var mRecyclerView: RecyclerView
    protected lateinit var mAdapter: CardAdapter
    protected var mDataset: Array<String> = arrayOf("Lista 1", "Lista 2", "Lista 3", "Lista 4",
            "Lista 5", "Lista 6", "Lista 7", "Lista 8", "Lista 9", "Lista 10")

    enum class LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
        initDataset()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.recycler_view_frag, container, false)
        rootView.setTag(TAG)

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = rootView.findViewById(R.id.recyclerView)

        if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(GridLayoutManager(activity, 3))
        } else {
            mRecyclerView.setLayoutManager(GridLayoutManager(activity, 6))
        }

        var mode: Int? = null
        var id: String? = null
        val bundle = this.arguments
        if (bundle != null) {
            mode = bundle.getInt("MODE")
            id = bundle.getString("ID")
        }

        var friendsPlaylists: MutableList<Playlist> = mutableListOf()


        val result: Exception? = AdapterCreatorTask().execute(mode.toString(), id).get()
        if (result != null) {
            throw result
        }

        // Set CardAdapter as the adapter for RecyclerView.
        mRecyclerView.adapter = mAdapter
        // END_INCLUDE(initializeRecyclerView)

        return rootView
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private fun initDataset() {
        mDataset = arrayOf("Lista 1", "Lista 2", "Lista 3", "Lista 4", "Lista 5", "Lista 6", "Lista 7", "Lista 8", "Lista 9", "Lista 10")
    }

    companion object {
        private val TAG = "CardsRecyclerViewFragment"
        public const val MODE_USERLOGGED_PLAYLISTS = 0
        public const val MODE_FRIENDS_PLAYLISTS = 1
        public const val MODE_USER_PLAYLISTS = 2
        public const val MODE_FROMFAVORITE_ALBUMS = 3
        public const val MODE_ARTIST_ALBUMS = 4
    }

    inner class AdapterCreatorTask : AsyncTask<String, Void, Exception?>() {
        override fun doInBackground(vararg params: String?): Exception? {
            val mode: Int = params[0]?.toInt()!!
            val id: String = params[1] ?: ""
            var friendsPlaylists: MutableList<Playlist> = mutableListOf()

            try {
                if (mode == MODE_USERLOGGED_PLAYLISTS) {
                    mAdapter = CardAdapter((activity?.application as CierzoApp)
                            .mUserLogged.getUser().getPlaylists())
                } else if (mode == MODE_FRIENDS_PLAYLISTS) {
                    for (friend in (activity?.application as CierzoApp).mUserLogged.getUser().getFriends()) {
                        friendsPlaylists.addAll(friend.getPlaylists())
                    }
                    mAdapter = CardAdapter(friendsPlaylists)
                } else if (mode == MODE_USER_PLAYLISTS) {
                    mAdapter = CardAdapter(cierzo.model.searchUsers(username = id!!))
                } else if (mode == MODE_FROMFAVORITE_ALBUMS) {
                    mAdapter = CardAdapter((activity?.application as CierzoApp)
                            .mUserLogged.getAlbumsFromFavorite())
                } else if (mode == MODE_ARTIST_ALBUMS) {
                    if (id != null && id != "") {
                        mAdapter = CardAdapter(cierzo.model.searchAlbums(author = id))
                    }
                }
            } catch (e: Exception) {
                return e
            }
            return null
        }
    }
}
