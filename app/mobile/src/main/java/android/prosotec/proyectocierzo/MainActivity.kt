package android.prosotec.proyectocierzo


import android.media.MediaPlayer
import android.os.Bundle
import android.prosotec.proyectocierzo.R.id.drawer_layout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageButton
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by ccucr on 18/03/2018.
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(my_toolbar)

        val toggle = ActionBarDrawerToggle(this,drawer_layout,my_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val button1 = findViewById<FloatingActionButton> (R.id.play)
        val mp = MediaPlayer.create (this, R.raw.song1)
        var position = 0
        button1.setOnClickListener {
            if (position == 0) {
                mp.start()
                position = 1
                button1.setImageResource(R.drawable.ic_pause_white_24dp)
            } else{
                mp.pause()
                position = 0
                button1.setImageResource(R.drawable.ic_play_arrow_white_24dp)
            }

        }

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {

            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}