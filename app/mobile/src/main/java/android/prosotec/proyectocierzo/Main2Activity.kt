package android.prosotec.proyectocierzo

import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.prosotec.proyectocierzo.R.id.mini
import android.prosotec.proyectocierzo.fragment.*
import android.support.design.widget.CoordinatorLayout.Behavior.getTag
import android.support.design.widget.TabItem
import android.support.design.widget.TabLayout
import android.view.*

import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.fragment_main2.view.*
import android.view.Gravity
import android.support.v4.view.MenuItemCompat.getActionView
import android.support.v7.app.ActionBar
import android.support.v7.widget.RecyclerView
import android.widget.Adapter
import android.widget.SearchView


class Main2Activity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var mMiniPlayerFragment: MiniPlayerFragment = MiniPlayerFragment()
    private val mFragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        setSupportActionBar(toolbar)
        getSupportActionBar()!!.setDisplayShowTitleEnabled(false)  /* Quitamos el título de la barra*/

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter


        // Set up the ViewPager with the sections adapter.
        val mViewPager = findViewById(R.id.container) as ViewPager
        mViewPager.setAdapter(mSectionsPagerAdapter)

        val tabLayout = findViewById(R.id.tabs) as TabLayout

        mViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(mViewPager))

        mFragmentManager.beginTransaction().replace(R.id.mini_player, mMiniPlayerFragment,
                mMiniPlayerFragment.getTag()).commit()




    }

    override fun onStart() {
        super.onStart()
        mMiniPlayerFragment.initialize(this)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            when (arguments.getInt(ARG_SECTION_NUMBER)) {
                1 -> return inflater!!.inflate(R.layout.fragment_prueba, container, false)
                2 -> return inflater!!.inflate(R.layout.fragment_prueba2, container, false)
                3 -> return inflater!!.inflate(R.layout.fragment_prueba3, container, false)
                else -> return inflater!!.inflate(R.layout.fragment_player, container, false)
            }

        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            // Show 4 total pages.
            return 4
        }
    }
}
