package android.prosotec.proyectocierzo


import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_new_mini_player.*


/**
 * Created by ccucr on 18/03/2018.
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mPlayerAdapter: PlayerAdapter
    private var mUserIsSeeking: Boolean = false
    private var songProgress: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(my_toolbar)

        val toggle = ActionBarDrawerToggle(this,drawer_layout,my_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        initializePlayerView()
        initializeSeekbar()
        initializePlaybackController()



        /*val button1 = findViewById<FloatingActionButton> (R.id.play)
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

        }*/

    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayerAdapter.release()
    }

    override fun onStart() {
        super.onStart()
        mPlayerAdapter.loadMedia(R.raw.song1)
    }

    override fun onStop() {
        super.onStop()
        // Solo liberar el MusicPlayer si no se está reproduciendo y no se está rotando la pantalla
        if (!isChangingConfigurations() || !mPlayerAdapter.isPlaying())
        {
            mPlayerAdapter.release()
        }
    }



    private fun initializePlaybackController() {
        var mMediaPlayerHolder = MediaPlayerHolder(this)
        mMediaPlayerHolder.setPlaybackInfoListener(PlaybackListener())
        mPlayerAdapter = mMediaPlayerHolder
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

    private fun initializePlayerView() {
        play.setOnClickListener(
                object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        if (mPlayerAdapter.isPlaying()) {
                            play.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                            mPlayerAdapter.pause()

                        } else {
                            mPlayerAdapter.play()
                            play.setImageResource(R.drawable.ic_pause_white_24dp)
                            actual_time.post(mUpdateTime)
                        }
                    }
                }
        )

        skip_prev.setOnClickListener(
                object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        mPlayerAdapter.reset()
                        mPlayerAdapter.play()
                    }
                }
        )
    }

    private fun initializeSeekbar() {
        seek_bar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    var userSelectedPosition = 0

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                        mUserIsSeeking = true
                    }

                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            userSelectedPosition = progress
                        }
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        mUserIsSeeking = false
                        mPlayerAdapter.seekTo(userSelectedPosition)
                    }
                }
        )
    }

    private val mUpdateTime = object : Runnable {
        override fun run() {
            if (mPlayerAdapter.isPlaying()) {
                var time: Int = songProgress/1000
                var timeM: Int = time/60
                var timeS: Int = time.rem(60)
                var timeStringM = if (timeM < 10) { "0$timeM" } else { timeM.toString() }
                var timeStringS = if (timeS < 10) { "0$timeS" } else { timeS.toString() }
                actual_time.text = "$timeStringM:$timeStringS"
                actual_time.postDelayed(this, 1000)
            } else {
                actual_time.removeCallbacks(this)
            }
        }
    }

    inner class PlaybackListener : PlaybackInfoListener() {

        override fun onDurationChanged(duration: Int) {
            seek_bar.max = duration

            var time: Int = duration/1000
            var timeM: Int = time/60
            var timeS: Int = time.rem(60)
            var timeStringM = if (timeM < 10) { "0$timeM" } else { timeM.toString() }
            var timeStringS = if (timeS < 10) { "0$timeS" } else { timeS.toString() }
            length.text = "$timeStringM:$timeStringS"
        }

        override fun onPositionChanged(position: Int) {
            if (!mUserIsSeeking) {
                seek_bar.progress = position
                songProgress = position
            }
        }

        override fun onStateChanged(state: PlaybackInfoListener.State) {

        }

        override fun onPlaybackCompleted() {

        }
    }

}

