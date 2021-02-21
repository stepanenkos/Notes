package kz.stepanenkos.notes

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kz.stepanenkos.notes.authorization.presentation.LoginViewModel
import kz.stepanenkos.notes.common.extensions.view.gone
import kz.stepanenkos.notes.common.extensions.view.show
import kz.stepanenkos.notes.user.data.datasource.UserCredentialsDataSource
import org.koin.android.ext.android.inject

private const val NIGHT_MODE_KEY = "night_mode_key"
private const val NIGHT_MODE_SHARED_PREFS = "night_mode_shared_prefs"

class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val loginViewModel: LoginViewModel by inject()
    private val userCredentialsDataSource: UserCredentialsDataSource by inject()
    private val firebaseAuth: FirebaseAuth by inject()

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var navController: NavController
    private lateinit var addNoteButton: FloatingActionButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var toolbar: Toolbar
    private lateinit var userProfilePhoto: ImageView
    private lateinit var signInButton: TextView
    private lateinit var signOutButton: TextView
    private lateinit var userEnterAsTextView: TextView
    private lateinit var userProfileSettingsTextView: TextView
    private lateinit var switchMaterial: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getSharedPreferences(NIGHT_MODE_SHARED_PREFS, MODE_PRIVATE).getBoolean(NIGHT_MODE_KEY, false)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item);
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun initViews() {

        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.activity_main_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        sharedPrefs = getSharedPreferences(NIGHT_MODE_SHARED_PREFS, MODE_PRIVATE)
        addNoteButton = findViewById(R.id.fab)
        toolbar = findViewById(R.id.toolbar)
        val sideBar: NavigationView = findViewById(R.id.activity_main_nav_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        userProfileSettingsTextView =
            sideBar.getHeaderView(0).findViewById(R.id.nav_header_button_profile_settings)
        signInButton = sideBar.getHeaderView(0).findViewById(R.id.nav_header_button_sign_in)
        signOutButton = sideBar.getHeaderView(0).findViewById(R.id.nav_header_button_sign_out)
        userEnterAsTextView =
            sideBar.getHeaderView(0).findViewById(R.id.nav_header_text_view_you_sign_in_as)
        userProfilePhoto = sideBar.getHeaderView(0).findViewById(R.id.nav_header_profile_photo)
        val findItem = sideBar.menu.findItem(R.id.nav_night_mode)
        switchMaterial = findItem.actionView as SwitchMaterial
        switchMaterial.isChecked = sharedPrefs.getBoolean(NIGHT_MODE_KEY, false)
            sideBar.setupWithNavController(navController)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = true
        actionBarDrawerToggle.syncState()
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
    }

    private fun setListeners() {

        addNoteButton.setOnClickListener {
            navController.navigate(
                R.id.editorFragment,
                null,
                NavOptions.Builder().setLaunchSingleTop(true).build()
            )
        }

        signInButton.setOnClickListener {
            navController.navigate(
                R.id.loginFragment,
                null,
                NavOptions.Builder().setLaunchSingleTop(true).build()
            )
            drawerLayout.close()

        }

        userProfileSettingsTextView.setOnClickListener {
            drawerLayout.close()
            navController.navigate(
                R.id.signedFragment,
                null,
                NavOptions.Builder().setLaunchSingleTop(true).build()
            )
        }

        signOutButton.setOnClickListener {
            loginViewModel.signOut()
            loginViewModel.signOutGoogle()
            updateUI(loginViewModel.getCurrentUser())
        }

        switchMaterial.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                setTheme(R.style.DarkTheme)
                switchMaterial.isChecked = true
                sharedPrefs.edit {
                    putBoolean(NIGHT_MODE_KEY, true)
                }
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                setTheme(R.style.LightTheme)
                switchMaterial.isChecked = false
                sharedPrefs.edit {
                    putBoolean(NIGHT_MODE_KEY, false)
                }
            }
        }

    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null && firebaseUser.isEmailVerified) {
            userEnterAsTextView.text = getString(
                R.string.fragment_login_dialog_information_text_you_are_logged_in_as,
                firebaseUser.displayName ?: "",
                firebaseUser.email
            )
            signInButton.gone()
            userProfileSettingsTextView.show()
            signOutButton.show()
            Glide.with(applicationContext)
                .load(firebaseUser.photoUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_account_photo)
                .fallback(R.drawable.ic_account_photo)
                .into(userProfilePhoto)
            addNoteButton.show()
        } else {
            userEnterAsTextView.text =
                getString(R.string.fragment_login_dialog_information_text_you_are_not_logged_in)
            Glide.with(this)
                .load(R.drawable.ic_account_photo)
                .circleCrop()
                .into(userProfilePhoto)
            signInButton.show()
            userProfileSettingsTextView.gone()
            signOutButton.gone()
            addNoteButton.hide()
        }
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        if (p0.currentUser != null && p0.currentUser!!.isEmailVerified) {
            updateUI(p0.currentUser)
        } else {
            toLoginScreen()
        }
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        }
    }

    private fun toLoginScreen() {
        navController.navigate(R.id.loginFragment)
    }
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

        recreate()
                /*finish()
                overridePendingTransition(R.anim.none, R.anim.none)
                val intent = intent
                intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)*/


    }
}