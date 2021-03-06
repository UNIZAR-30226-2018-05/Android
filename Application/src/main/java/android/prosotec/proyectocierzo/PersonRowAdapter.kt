package android.prosotec.proyectocierzo

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cierzo.model.objects.Album
import cierzo.model.objects.Author
import cierzo.model.objects.Playlist
import cierzo.model.objects.User
import com.example.android.mediasession.R
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.person_row_item.view.*
import org.w3c.dom.Text
import java.util.*

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
class PersonRowAdapter
// END_INCLUDE(recyclerViewSampleViewHolder)

/**
 * Initialize the dataset of the Adapter.
 *
 * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
 */
(private val mDataSet: MutableList<Any>,
 private val activity: AppCompatActivity,
 private val showFavIcon: Boolean = false,
 private val showDragAndDropIcon: Boolean = false) : RecyclerView.Adapter<PersonRowAdapter.ViewHolder>(), ItemTouchHelperAdapter {
    private var mAuthors: MutableList<Author> = mutableListOf()
    private var mUsers: MutableList<User> = mutableListOf()

    init {
        if (mDataSet.size > 0) {
            if (mDataSet[0] is Author) {
                mAuthors = mDataSet.distinctBy { (it as Author).id } as MutableList<Author>
            } else if (mDataSet[0] is User) {
                mUsers = mDataSet.distinctBy { (it as User).getInfo().elementAt(0) } as MutableList<User>
            }
        }
    }

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder(v: View,listAux: MutableList<Author>,context: Context) : RecyclerView.ViewHolder(v) {
        val title: TextView
        val subtitle: TextView
        val image: ImageView

        init {
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener {
                if (context is Main2Activity) {
                        var authorAux: Author = listAux.get(adapterPosition)
                        context.pName.text = authorAux.name
                        context.pBio.text = authorAux.bio
                        context.person_view.visibility = View.VISIBLE
                        context.main_layout.visibility = View.GONE
                        context.search_view.visibility = View.GONE
                }
            }
            title = v.findViewById(R.id.text_row_title) as TextView
            subtitle = v.findViewById(R.id.text_row_subtitle) as TextView
            image = v.findViewById(R.id.text_row_image) as ImageView
        }
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view.
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.person_row_item, viewGroup, false)

        if (!showFavIcon) {
            v.text_row_fav_icon.visibility = View.GONE
        }

        if (!showDragAndDropIcon) {
            v.text_row_dragAndDrop_icon.visibility = View.GONE
        }

        return ViewHolder(v,mAuthors,activity)
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.d(TAG, "Element $position set.")

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        if (mAuthors.size > 0) {
            viewHolder.title.text = mAuthors[position].name
            viewHolder.subtitle.visibility = View.GONE
            var imageURL: String = mAuthors[position].imageURL
            Ion.with(viewHolder.image)
                    .placeholder(R.drawable.gray_background)
                    .load(imageURL)
        } else if (mUsers.size > 0) {
            viewHolder.title.text = mUsers[position].getInfo().elementAt(2) // Name
            viewHolder.subtitle.text = mUsers[position].getInfo().elementAt(1) // Username
            viewHolder.image.setImageResource(R.drawable.ic_account_circle_black_24dp)
        }

    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return if (mAuthors.size > 0) { mAuthors.size } else { mUsers.size }
    }

    override fun onItemDismiss(position: Int) {
        if (mAuthors.size > 0) {
            mAuthors.removeAt(position)
            notifyItemRemoved(position)
        } else if (mUsers.size > 0) {
            mUsers.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        /*if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(mDataSet, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mDataSet, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)*/
    }

    companion object {
        private val TAG = "PersonRowAdapter"
    }
}
