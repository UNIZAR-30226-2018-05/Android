package android.prosotec.proyectocierzo.fragment.recyclerview

import android.os.AsyncTask
import android.os.Bundle
import android.prosotec.proyectocierzo.PersonRowAdapter
import android.prosotec.proyectocierzo.SimpleItemTouchHelperCallback
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cierzo.model.AUTHOR
import cierzo.model.USER
import cierzo.model.objects.Author
import cierzo.model.objects.User
import com.example.android.mediasession.CierzoApp
import com.example.android.mediasession.R

/**
 * Demonstrates the use of [RecyclerView] with a [LinearLayoutManager] and a
 * [GridLayoutManager].
 */
class PersonRowRecyclerViewFragment : Fragment()  {

    protected lateinit var mRecyclerView: RecyclerView
    protected lateinit var mAdapter: PersonRowAdapter
    protected var mDataset: MutableList<String> = mutableListOf("Persona 1", "Persona 2", "Persona 3", "Persona 4",
            "Persona 5", "Persona 6", "Persona 7", "Persona 8", "Persona 9", "Persona 10")

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
        var listIds: List<String>? = null
        val bundle = this.arguments
        if (bundle != null) {
            mode = bundle.getInt("MODE")
            search = bundle.getString("search")
            listIds = bundle.getStringArrayList("listIds")
        }

        if (mode == MODE_SEARCH_AUTHORS) {
            mAdapter = PersonRowAdapter(searchAuthorsAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, search).get().toMutableList())
        } else if (mode == MODE_SEARCH_MULTIPLE_AUTHORS){
            mAdapter = PersonRowAdapter(getMultiAuthorsAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, listIds).get().toMutableList())
        } else if (mode == MODE_SEARCH_USERS) {
            mAdapter = PersonRowAdapter(searchUsersAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, search).get().toMutableList())
        } else if (mode == MODE_SEARCH_MULTIPLE_USERS) {
            mAdapter = PersonRowAdapter(getMultiUsersAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, listIds).get().toMutableList())
        } else {
            mAdapter = PersonRowAdapter(getAuthorsFavAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get().toMutableList())
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
        mDataset = mutableListOf("Persona 1", "Persona 2", "Persona 3", "Persona 4",
                "Persona 5", "Persona 6", "Persona 7", "Persona 8", "Persona 9", "Persona 10")    }

    companion object {
        private val TAG = "CardsRecyclerViewFragment"
        public const val MODE_SEARCH_AUTHORS = 1
        public const val MODE_SEARCH_MULTIPLE_AUTHORS = 2
        public const val MODE_SEARCH_MULTIPLE_USERS = 3
        public const val MODE_SEARCH_USERS = 4
    }

    inner class getAuthorsFavAsync : AsyncTask<Int, Void, List<Any>>() {
        override fun doInBackground(vararg params: Int?): List<Any> {
            return (activity?.application as CierzoApp).mUserLogged.getAuthorsFromFavorite()
        }
    }

    inner class searchAuthorsAsync : AsyncTask<String, Void, List<Any>>() {
        override fun doInBackground(vararg params: String?): List<Any> {
            return cierzo.model.searchAuthors(name = params[0] ?: "")
        }
    }

    inner class searchUsersAsync : AsyncTask<String, Void, List<Any>>() {
        override fun doInBackground(vararg params: String?): List<Any> {
            return cierzo.model.searchUsers(username = params[0] ?: "")
        }
    }

    inner class getMultiAuthorsAsync : AsyncTask<List<String>, Void, List<Any>>() {
        override fun doInBackground(vararg params: List<String>?): List<Any> {
            var returnList: MutableList<Author> = mutableListOf()
            for (id in params[0]!!) {
                returnList.add(cierzo.model.getFromServer(AUTHOR,id) as Author)
            }
            return returnList
        }
    }

    inner class getMultiUsersAsync : AsyncTask<List<String>, Void, List<Any>>() {
        override fun doInBackground(vararg params: List<String>?): List<Any> {
            var returnList: MutableList<User> = mutableListOf()
            for (id in params[0]!!) {
                returnList.add(cierzo.model.getFromServer(USER,id) as User)
            }
            return returnList
        }
    }
}
