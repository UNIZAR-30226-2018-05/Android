package android.prosotec.proyectocierzo

import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.android.synthetic.main.nav_header_index.view.*
import kotlinx.android.synthetic.main.text_row_item.view.*
import java.util.*

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
class RowAdapter
// END_INCLUDE(recyclerViewSampleViewHolder)

/**
 * Initialize the dataset of the Adapter.
 *
 * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
 */
(private val mDataSet: MutableList<String>, private val showFavIcon: Boolean) : RecyclerView.Adapter<RowAdapter.ViewHolder>(), ItemTouchHelperAdapter {


    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val textView: TextView

        init {
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener { Log.d(TAG, "Element $adapterPosition clicked.") }
            textView = v.findViewById(R.id.text_row_title) as TextView
        }
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view.
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.text_row_item, viewGroup, false)

        if (!showFavIcon) {
            v.text_row_fav_icon.visibility = View.GONE
        }

        return ViewHolder(v)
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.d(TAG, "Element $position set.")

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.textView.text_row_title.text = mDataSet[position]

    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return mDataSet.size
    }

    override fun onItemDismiss(position: Int) {
        mDataSet.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(mDataSet, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mDataSet, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    companion object {
        private val TAG = "RowAdapter"
    }
}
