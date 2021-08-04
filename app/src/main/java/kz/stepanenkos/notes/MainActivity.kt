package kz.stepanenkos.notes

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.edit
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kz.stepanenkos.notes.common.extensions.view.gone
import kz.stepanenkos.notes.common.extensions.view.hide
import kz.stepanenkos.notes.common.extensions.view.show
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
    private lateinit var coordinatorLayout: CoordinatorLayout

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
        coordinatorLayout = findViewById(R.id.activity_main_coordinator_layout)
    }

    private fun setListeners() {

        addNoteButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setPositiveButton("Добавить заметку") {_, _ ->
                navController.navigate(
                    R.id.editorNotesFragment,
                    /*null,
                    NavOptions.Builder().setLaunchSingleTop(true).build()*/
                )
            }
            builder.setNegativeButton("Добавить задачу") {_, _ ->
                navController.navigate(
                    R.id.editorTasksFragment,
                    /*null,
                    NavOptions.Builder().setLaunchSingleTop(true).build()*/
                )
            }
            builder.create().show()
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

    private fun showUISignedUser() {
        addNoteButton.show()
        bottomNavigationView.show()
        coordinatorLayout.show()
    }

    private fun showUIUnsignedUser() {
        addNoteButton.hide()
        bottomNavigationView.hide()
        coordinatorLayout.gone()
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null && firebaseUser.isEmailVerified) {
            showUISignedUser()
        } else {
            showUIUnsignedUser()
        }
    }
}