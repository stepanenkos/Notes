package kz.stepanenkos.notes

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.CustomPopupMenu
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.edit
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kz.stepanenkos.notes.common.extensions.view.gone
import kz.stepanenkos.notes.common.extensions.view.hide
import kz.stepanenkos.notes.common.extensions.view.show
import kz.stepanenkos.notes.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject

private const val NIGHT_MODE_KEY = "night_mode_key"
private const val NIGHT_MODE_SHARED_PREFS = "night_mode_shared_prefs"

class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val firebaseAuth: FirebaseAuth by inject()
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var binding: ActivityMainBinding

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            supportFragmentManager.findFragmentById(binding.activityMainNavHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.background = null
        sharedPrefs = getSharedPreferences(NIGHT_MODE_SHARED_PREFS, MODE_PRIVATE)
        coordinatorLayout = binding.activityMainCoordinatorLayout
    }

    @SuppressLint("RestrictedApi")
    private fun setListeners() {
        bottomNavigationView.setOnItemSelectedListener { bottomNavigationViewItemMenu ->
            when(bottomNavigationViewItemMenu.itemId) {
                R.id.mainFragment -> {
                    navController.navigate(R.id.mainFragment)
                    true
                }

                R.id.addNoteOrTask -> {
                    popupMenu()
                    true
                }

                R.id.searchNotesFragment -> {
                    navController.navigate(R.id.searchNotesFragment)
                    true
                }

                R.id.menuFragment -> {
                    navController.navigate(R.id.menuFragment)
                    true
                }
                else -> { false }
            }
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

    private fun popupMenu() {
        val addNoteOrTaskView = bottomNavigationView.findViewById<View>(R.id.addNoteOrTask)
        val popupMenu = CustomPopupMenu(this, addNoteOrTaskView)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.menu.findItem(R.id.editorNotesFragment).setIcon(R.drawable.ic_sticky_note)
        popupMenu.menu.findItem(R.id.editorTasksFragment).setIcon(R.drawable.ic_add_task)
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { popupMenuItem ->

            when(popupMenuItem.itemId) {
                R.id.editorNotesFragment -> {

                    navController.navigate(R.id.editorNotesFragment)
                    true
                }
                else -> {
                    navController.navigate(R.id.editorTasksFragment)
                    true
                }
            }
        }

        popupMenu.setOnDismissListener {
            bottomNavigationView.menu.findItem(R.id.mainFragment).isChecked = true
        }
    }

    private fun toLoginScreen() {
        navController.navigate(R.id.loginFragment)
    }

    private fun showUISignedUser() {
        bottomNavigationView.show()
        coordinatorLayout.show()
    }

    private fun showUIUnsignedUser() {
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