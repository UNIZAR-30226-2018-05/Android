package android.prosotec.proyectocierzo.fragment.recyclerview

import android.os.AsyncTask
import android.os.Bundle
import android.prosotec.proyectocierzo.PersonRowAdapter
import android.prosotec.proyectocierzo.SimpleItemTouchHelperCallback
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        mRecyclerView.setLayoutManager(LinearLayoutManager(activity))

        mAdapter = PersonRowAdapter(getAuthorsAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get().toMutableList())
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
    }

    inner class getAuthorsAsync : AsyncTask<Int, Void, List<Any>>() {
        override fun doInBackground(vararg params: Int?): List<Any> {
            return (activity?.application as CierzoApp).mUserLogged.getAuthorsFromFavorite()
        }
    }
}
