package kz.stepanenkos.notes

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.koin.android.ext.android.inject

private const val NIGHT_MODE_KEY = "night_mode_key"
private const val NIGHT_MODE_SHARED_PREFS = "night_mode_shared_prefs"

class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val firebaseAuth: FirebaseAuth by inject()
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var navController: NavController
    private lateinit var addNoteButton: FloatingActionButton
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getSharedPreferences(NIGHT_MODE_SHARED_PREFS, MODE_PRIVATE).getBoolean(
                NIGHT_MODE_KEY,
                false
            )
        ) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.DarkTheme)
            getSharedPreferences(NIGHT_MODE_SHARED_PREFS, MODE_PRIVATE).edit {
                putBoolean(NIGHT_MODE_KEY, true)
            }
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            setTheme(R.style.LightTheme)
            getSharedPreferences(NIGHT_MODE_SHARED_PREFS, MODE_PRIVATE).edit {
                putBoolean(NIGHT_MODE_KEY, false)
            }
        }
        setContentView(R.layout.activity_main)
        initViews()
        setListeners()

    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { ViewPumpContextWrapper.wrap(it) })
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null && currentUser.isEmailVerified) {
            updateUI(currentUser)
        } else {
            toLoginScreen()
        }
        firebaseAuth.addAuthStateListener(this)
        sharedPrefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun initViews() {
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.activity_main_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.background = null

        sharedPrefs = getSharedPreferences(NIGHT_MODE_SHARED_PREFS, MODE_PRIVATE)
        addNoteButton = findViewById(R.id.fab)

        val sideBar: NavigationView = findViewById(R.id.activity_main_nav_view)
        sideBar.setupWithNavController(navController)
    }

    private fun setListeners() {

        addNoteButton.setOnClickListener {
            navController.navigate(
                R.id.editorFragment,
                /*null,
                NavOptions.Builder().setLaunchSingleTop(true).build()*/
            )
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        recreate()
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        if (p0.currentUser != null && p0.currentUser!!.isEmailVerified) {
            updateUI(p0.currentUser)
        } else {
            toLoginScreen()
        }
    }

    private fun toLoginScreen() {
        navController.navigate(R.id.loginFragment)
    }

    private fun showUISignedUser(firebaseUser: FirebaseUser?) {
        addNoteButton.show()
    }

    private fun showUIUnsignedUser() {
        addNoteButton.hide()
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null && firebaseUser.isEmailVerified) {
            showUISignedUser(firebaseUser)
        } else {
            showUIUnsignedUser()
        }
    }
}