package android.prosotec.proyectocierzo.fragment.recyclerview

import android.os.AsyncTask
import android.os.Bundle
import android.prosotec.proyectocierzo.Main2Activity
import android.prosotec.proyectocierzo.PersonRowAdapter
import android.prosotec.proyectocierzo.SongRowAdapter
import android.prosotec.proyectocierzo.SimpleItemTouchHelperCallback
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cierzo.model.ALBUM
import cierzo.model.AUTHOR
import cierzo.model.PLAYLIST
import cierzo.model.SONG
import cierzo.model.objects.Album
import cierzo.model.objects.Author
import cierzo.model.objects.Playlist
import cierzo.model.objects.Song
import com.example.android.mediasession.CierzoApp
import com.example.android.mediasession.R

/**
 * Demonstrates the use of [RecyclerView] with a [LinearLayoutManager] and a
 * [GridLayoutManager].
 */
class SongRowRecyclerViewFragment : Fragment()  {

    protected lateinit var mRecyclerView: RecyclerView
    protected lateinit var mAdapter: SongRowAdapter
    protected var mDataset: MutableList<String> = mutableListOf("Canción 1", "Canción 2", "Canción 3", "Canción 4",
            "Canción 5", "Canción 6", "Canción 7", "Canción 8", "Canción 9", "Canción 10")

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
        rootView.findViewById<View>(R.id.floatButton).visibility = View.GONE

        mRecyclerView.setLayoutManager(LinearLayoutManager(activity))

        var mode: Int? = null
        var search: String? = null
        var listIds: ArrayList<String>? = null
        var playlistId: String? = null
        val bundle = this.arguments
        if (bundle != null) {
            mode = bundle.getInt("MODE")
            search = bundle.getString("search")
            listIds = bundle.getStringArrayList("listIds")
            playlistId = bundle.getString("playlistId")
        }

        if (mode == MODE_SEARCH_SONGS) {
            mAdapter = SongRowAdapter((activity as Main2Activity),
                    null,
                    true,
                    false,
                    false,
                    false,
                    searchSongsAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, search)
                            .get().toMutableList() as MutableList<Song>)

        } else if (mode == MODE_SEARCH_MULTIPLE_SONGS) {
            mAdapter = SongRowAdapter((activity as Main2Activity),
                    null,
                    true,
                    false,
                    false,
                    false,
                    getMultiSongsAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, listIds)
                            .get().toMutableList() as MutableList<Song>)
        } else if (mode == MODE_PLAYLIST_SONGS) {
            mAdapter = SongRowAdapter((activity as Main2Activity),
                    getPlaylistAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, playlistId)
                            .get(),
                    true,
                    false,
                    false,
                    false)
        } else if (mode == MODE_ALBUM_SONGS) {
            mAdapter = SongRowAdapter((activity as Main2Activity),
                    null,
                    true,
                    false,
                    false,
                    false,
                    getAlbumAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, playlistId)
                            .get().songs.toMutableList())
        } else {
            mAdapter = SongRowAdapter((activity as Main2Activity), (activity?.application as CierzoApp).mUserLogged.getFavoritePlaylist(),
                    showCrossIcon = false, showDragAndDropIcon = false, favAsRemove = true)
        }
        // Set CardAdapter as the adapter for RecyclerView.
        mRecyclerView.adapter = mAdapter
        // END_INCLUDE(initializeRecyclerView)

        val callback = SimpleItemTouchHelperCallback(mAdapter, false, false)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(mRecyclerView)

        return rootView
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private fun initDataset() {
        mDataset = mutableListOf("Canción 1", "Canción 2", "Canción 3", "Canción 4",
        "Canción 5", "Canción 6", "Canción 7", "Canción 8", "Canción 9", "Canción 10")    }

    companion object {
        private val TAG = "CardsRecyclerViewFragment"
        public const val MODE_SEARCH_SONGS = 1
        public const val MODE_SEARCH_MULTIPLE_SONGS = 2
        public const val MODE_PLAYLIST_SONGS = 3
        public const val MODE_ALBUM_SONGS = 4
    }

    inner class searchSongsAsync : AsyncTask<String, Void, List<Any>>() {
        override fun doInBackground(vararg params: String?): List<Any> {
            return cierzo.model.searchSongs(name = params[0] ?: "")
        }
    }

    inner class getMultiSongsAsync : AsyncTask<List<String>, Void, List<Any>>() {
        override fun doInBackground(vararg params: List<String>?): List<Any> {
            var returnList: MutableList<Song> = mutableListOf()
            for (id in params[0]!!) {
                returnList.add(cierzo.model.getFromServer(SONG,id) as Song)
            }
            return returnList
        }
    }

    inner class getPlaylistAsync : AsyncTask<String, Void, Playlist>() {
        override fun doInBackground(vararg params: String?): Playlist {
            return cierzo.model.getFromServer(PLAYLIST,params[0]!!) as Playlist
        }
    }

    inner class getAlbumAsync : AsyncTask<String, Void, Album>() {
        override fun doInBackground(vararg params: String?): Album {
            return cierzo.model.getFromServer(ALBUM,params[0]!!) as Album
        }
    }
}
