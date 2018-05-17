package android.prosotec.proyectocierzo.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.prosotec.proyectocierzo.CardAdapter
import android.prosotec.proyectocierzo.R
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.recycler_view_frag, container, false)
        rootView.setTag(TAG)

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = rootView.findViewById(R.id.recyclerView)

        if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(GridLayoutManager(activity, 2))
        } else {
            mRecyclerView.setLayoutManager(GridLayoutManager(activity, 4))
        }

        mAdapter = CardAdapter(mDataset)
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
    }
}
