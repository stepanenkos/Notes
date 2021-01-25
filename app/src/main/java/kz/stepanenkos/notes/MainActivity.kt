package kz.stepanenkos.notes

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kz.stepanenkos.notes.authorization.presentation.LoginViewModel
import kz.stepanenkos.notes.user.data.datasource.UserCredentialsDataSource
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {
    private val loginViewModel: LoginViewModel by inject()
    private val userCredentialsDataSource: UserCredentialsDataSource by inject()
    private val firebaseAuth: FirebaseAuth by inject()

    private lateinit var navController: NavController
    private lateinit var addNoteButton: FloatingActionButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var toolbar: Toolbar
    private lateinit var signInButton: TextView
    private lateinit var signOutButton: TextView
    private lateinit var userEnterAsTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser

//        if(currentUser?.isEmailVerified == true) {
//        }
        updateUI(currentUser)
        firebaseAuth.addAuthStateListener(this)
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
        addNoteButton = findViewById(R.id.fab)
        addNoteButton.setOnClickListener {
            navController.navigate(
                R.id.editorFragment,
                null,
                NavOptions.Builder().setLaunchSingleTop(true).build()
            )
        }
        val sideBar: NavigationView = findViewById(R.id.activity_main_nav_view)
        sideBar.setupWithNavController(navController)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        drawerLayout = findViewById(R.id.drawer_layout)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = true
        actionBarDrawerToggle.syncState()
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        signInButton = sideBar.getHeaderView(0).findViewById(R.id.nav_header_button_sign_in)
        signOutButton = sideBar.getHeaderView(0).findViewById(R.id.nav_header_button_sign_out)
        signInButton.setOnClickListener {
            navController.navigate(
                R.id.loginDialogFragment,
                null,
                NavOptions.Builder().setLaunchSingleTop(true).build()
            )
            drawerLayout.close()

        }
        signOutButton.setOnClickListener {
            loginViewModel.signOut()
            loginViewModel.signOutGoogle()
            updateUI(loginViewModel.getCurrentUser())
        }
        userEnterAsTextView =
            sideBar.getHeaderView(0).findViewById(R.id.nav_header_text_view_you_sign_in_as)
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null) {
            userEnterAsTextView.text = getString(
                R.string.fragment_login_dialog_information_text_you_are_logged_in_as,
                firebaseUser.displayName ?: "",
                firebaseUser.email
            )
            signInButton.visibility = View.GONE
            signOutButton.visibility = View.VISIBLE
        } else {
            userEnterAsTextView.text =
                getString(R.string.fragment_login_dialog_information_text_you_are_not_logged_in)
            signInButton.visibility = View.VISIBLE
            signOutButton.visibility = View.GONE
        }
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        Log.d("TAG", "onAuthStateChanged: ${p0.currentUser?.email}")
        if (p0.currentUser?.isEmailVerified == true || p0.currentUser?.providerData?.get(0)
                ?.equals("google.com") == true
        ) {
            updateUI(p0.currentUser)
        }
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        }
    }
}