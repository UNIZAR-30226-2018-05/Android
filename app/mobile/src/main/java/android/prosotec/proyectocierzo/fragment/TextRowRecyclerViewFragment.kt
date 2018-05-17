package android.prosotec.proyectocierzo.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.prosotec.proyectocierzo.CardAdapter
import android.prosotec.proyectocierzo.R
import android.prosotec.proyectocierzo.RowAdapter
import android.prosotec.proyectocierzo.SimpleItemTouchHelperCallback
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Demonstrates the use of [RecyclerView] with a [LinearLayoutManager] and a
 * [GridLayoutManager].
 */
class TextRowRecyclerViewFragment : Fragment()  {

    protected lateinit var mRecyclerView: RecyclerView
    protected lateinit var mAdapter: RowAdapter
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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.recycler_view_frag, container, false)
        rootView.setTag(TAG)

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = rootView.findViewById(R.id.recyclerView)

        mRecyclerView.setLayoutManager(LinearLayoutManager(activity))

        mAdapter = RowAdapter(mDataset, true)
        // Set CardAdapter as the adapter for RecyclerView.
        mRecyclerView.adapter = mAdapter
        // END_INCLUDE(initializeRecyclerView)

        val callback = SimpleItemTouchHelperCallback(mAdapter, true, false)
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
    }
}
