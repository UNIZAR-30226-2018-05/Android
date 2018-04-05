package android.prosotec.proyectocierzo.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.prosotec.proyectocierzo.R

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PlayerFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_player, container, false)
    }
}// Required empty public constructor