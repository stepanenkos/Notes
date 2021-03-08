package kz.stepanenkos.notes.menu.presentation

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.internal.LifecycleFragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.authorization.presentation.LoginViewModel
import kz.stepanenkos.notes.common.extensions.view.gone
import kz.stepanenkos.notes.common.extensions.view.show
import org.koin.android.ext.android.inject

private const val NIGHT_MODE_KEY = "night_mode_key"
private const val NIGHT_MODE_SHARED_PREFS = "night_mode_shared_prefs"

class MenuFragment : Fragment(), FirebaseAuth.AuthStateListener,
    SharedPreferences.OnSharedPreferenceChangeListener {
    private val loginViewModel: LoginViewModel by inject()
    private val firebaseAuth: FirebaseAuth by inject()

    private lateinit var userProfilePhoto: ImageView
    private lateinit var signInButton: TextView
    private lateinit var signOutButton: TextView
    private lateinit var userEnterAsTextView: TextView
    private lateinit var userProfileSettingsTextView: TextView
    private lateinit var switchMaterial: SwitchMaterial
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)
        userProfileSettingsTextView =
            view.findViewById(R.id.nav_header_button_profile_settings)
        signInButton = view.findViewById(R.id.nav_header_button_sign_in)
        signOutButton = view.findViewById(R.id.nav_header_button_sign_out)
        userEnterAsTextView =
            view.findViewById(R.id.nav_header_text_view_you_sign_in_as)
        userProfilePhoto = view.findViewById(R.id.nav_header_profile_photo)

        switchMaterial = view.findViewById(R.id.fragment_menu_night_mode_switch)
        sharedPrefs = activity?.getSharedPreferences(NIGHT_MODE_SHARED_PREFS, AppCompatActivity.MODE_PRIVATE)!!
        switchMaterial.isChecked = sharedPrefs.getBoolean(NIGHT_MODE_KEY, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signInButton.setOnClickListener {
            Toast.makeText(requireContext(), "Войти", Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                R.id.loginFragment,
                null,
                NavOptions.Builder().setLaunchSingleTop(true).build()
            )
        }

        userProfileSettingsTextView.setOnClickListener {
            findNavController().navigate(
                R.id.signedFragment,
                /*      null,
                      NavOptions.Builder().setLaunchSingleTop(true).build()*/
            )
        }

        signOutButton.setOnClickListener {
            Toast.makeText(requireContext(), "Выйти", Toast.LENGTH_SHORT).show()
            loginViewModel.signOut()
            loginViewModel.signOutGoogle()
            updateUI(loginViewModel.getCurrentUser())
        }

        switchMaterial.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                activity?.setTheme(R.style.DarkTheme)
                switchMaterial.isChecked = true
                sharedPrefs.edit {
                    putBoolean(NIGHT_MODE_KEY, true)
                }
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                activity?.setTheme(R.style.LightTheme)
                switchMaterial.isChecked = false
                sharedPrefs.edit {
                    putBoolean(NIGHT_MODE_KEY, false)
                }
            }
        }
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


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        activity?.recreate()
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        if (p0.currentUser != null && p0.currentUser!!.isEmailVerified) {
            updateUI(p0.currentUser)
        } else {
            toLoginScreen()
        }
    }

    private fun showUISignedUser(firebaseUser: FirebaseUser?) {
        userEnterAsTextView.text = activity?.getString(
            R.string.fragment_login_dialog_information_text_you_are_logged_in_as,
            firebaseUser?.displayName ?: "",
            firebaseUser?.email
        )
        signInButton.gone()
        signOutButton.show()
        userProfileSettingsTextView.show()
        if(lifecycle.currentState == Lifecycle.State.RESUMED) {
            Glide.with(this)
                .load(firebaseUser?.photoUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_account_photo)
                .fallback(R.drawable.ic_account_photo)
                .into(userProfilePhoto)
        }
    }

    private fun showUIUnsignedUser() {
        userEnterAsTextView.text =
            activity?.getString(R.string.fragment_login_dialog_information_text_you_are_not_logged_in)
        if(lifecycle.currentState == Lifecycle.State.RESUMED) {
            Glide.with(this)
                .load(R.drawable.ic_account_photo)
                .circleCrop()
                .into(userProfilePhoto)
        }
        signInButton.show()
        signOutButton.gone()
        userProfileSettingsTextView.gone()

    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null && firebaseUser.isEmailVerified) {
            showUISignedUser(firebaseUser)
        } else {
            showUIUnsignedUser()
        }
    }



    private fun toLoginScreen() {
        findNavController().navigate(R.id.loginFragment)
    }
}