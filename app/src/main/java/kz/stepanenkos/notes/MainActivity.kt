package kz.stepanenkos.notes

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kz.stepanenkos.notes.authorization.presentation.LoginViewModel
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(){
    private val loginViewModel: LoginViewModel by inject()
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
        loginViewModel.signIn.observe(this, ::updateUI)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
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
        val navOptionsBuilder = NavOptionsBuilder()
        navController
        addNoteButton.setOnClickListener {
            navController.navigate(R.id.editorFragment, null, NavOptions.Builder().setLaunchSingleTop(true).build())
        }
        val sideBar: NavigationView = findViewById(R.id.activity_main_nav_view)
        sideBar.setupWithNavController(navController)
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = " "
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        drawerLayout = findViewById(R.id.drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = true
        actionBarDrawerToggle.syncState()
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        signInButton =  sideBar.getHeaderView(0).findViewById(R.id.nav_header_button_sign_in)
        signOutButton = sideBar.getHeaderView(0).findViewById(R.id.nav_header_button_sign_out)
        signInButton.setOnClickListener {
            navController.navigate(R.id.loginDialogFragment)
        }
        signOutButton.setOnClickListener {
            loginViewModel.signOut()
        }
        userEnterAsTextView = sideBar.getHeaderView(0).findViewById(R.id.nav_header_text_view_you_sign_in_as)

    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        Log.d("TAG", "updateUI: ${firebaseUser?.email}")
        if(firebaseUser != null) {
            userEnterAsTextView.text = getString(
                R.string.fragment_login_dialog_information_text_you_are_logged_in_as,
                firebaseUser.displayName ?: "",
                firebaseUser.email
            )
            signInButton.visibility = View.GONE
            signOutButton.visibility = View.VISIBLE
        } else {
            userEnterAsTextView.text = getString(R.string.fragment_login_dialog_information_text_you_are_not_logged_in)
            signInButton.visibility = View.VISIBLE
            signOutButton.visibility = View.GONE
        }
    }

}