package kz.stepanenkos.notes

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var addNoteButton: FloatingActionButton
    private lateinit var imageViewHamburger: ImageView
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_menu_settings -> {
                navController.navigate(R.id.settingsFragment)
                true
            }
            R.id.item_menu_sign_in -> {
                navController.navigate(R.id.loginDialogFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initViews() {
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.activity_main_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        addNoteButton = findViewById(R.id.fab)
        addNoteButton.setOnClickListener {
            navController.navigate(R.id.editorFragment)
        }
        val sideBar: NavigationView = findViewById(R.id.activity_main_nav_view)
        sideBar?.setupWithNavController(navController)

        imageViewHamburger = findViewById(R.id.im_hamburger)
        drawerLayout = findViewById(R.id.drawer_layout)

        imageViewHamburger.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }
}