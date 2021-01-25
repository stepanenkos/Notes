package kz.stepanenkos.notes.authorization.presentation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.presentation.AbstractTextWatcher
import org.koin.android.ext.android.inject

private const val RC_SIGN_IN = 9001

class LoginDialogFragment : DialogFragment() {
    private val loginViewModel: LoginViewModel by inject()
    private val googleSignInClient: GoogleSignInClient by inject()
    private val auth: FirebaseAuth by inject()
    private var currentUser: FirebaseUser? = null
    private var email: String = ""
    private var password: String = ""

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var signInButton: Button
    private lateinit var closeButton: Button
    private lateinit var googleSignInButton: SignInButton
    private lateinit var informationTextView: TextView
    private lateinit var enterAsTextView: TextView
    private lateinit var signOutButton: Button
    private lateinit var forgetPasswordTextViewButton: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        emailEditText = view.findViewById(R.id.fragment_login_edit_text_email)
        passwordEditText = view.findViewById(R.id.fragment_login_edit_text_password)
        signUpButton = view.findViewById(R.id.fragment_login_button_sign_up)
        signInButton = view.findViewById(R.id.fragment_login_button_sign_in)
        closeButton = view.findViewById(R.id.fragment_login_button_close)
        informationTextView = view.findViewById(R.id.fragment_login_text_view_information)
        enterAsTextView = view.findViewById(R.id.fragment_login_you_enter_how)
        googleSignInButton = view.findViewById(R.id.fragment_login_button_google_login)
        closeButton = view.findViewById(R.id.fragment_login_button_close)
        enterAsTextView = view.findViewById(R.id.fragment_login_you_enter_how)
        signOutButton = view.findViewById(R.id.fragment_login_sign_out)
        forgetPasswordTextViewButton =
            view.findViewById(R.id.fragment_login_text_view_forgot_password)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
        observeViewModelLiveData()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
        currentUser = loginViewModel.getCurrentUser()
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            showSigned(currentUser)
        }
    }

    private fun setListeners() {

        signInButton.setOnClickListener {
            if (email.isNotBlank() && password.isNotBlank()) {
                Log.d("TAG", "setListeners: $email $password")
                loginViewModel.signIn(email, password)
            } else {
                informationTextView.visibility = View.VISIBLE
                informationTextView.text =
                    getString(R.string.fragment_login_dialog_information_text_all_fields_must_be_filled)
            }
        }

        signUpButton.setOnClickListener {
            if (email.isNotBlank() && password.isNotBlank()) {
                loginViewModel.signUp(email, password)
            } else {
                informationTextView.visibility = View.VISIBLE
                informationTextView.text =
                    getString(R.string.fragment_login_dialog_information_text_all_fields_must_be_filled)
            }
        }

        closeButton.setOnClickListener {
            dismiss()
        }

        signOutButton.setOnClickListener {
            googleSignInClient.signOut()
            auth.signOut()
            currentUser = null
            //showUnSigned()
        }

        forgetPasswordTextViewButton.setOnClickListener {
            dismiss()
            findNavController().navigate(R.id.forgotPasswordFragment)
        }

        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        emailEditText.addTextChangedListener(object : AbstractTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                email = emailEditText.text.toString()

            }
        })

        passwordEditText.addTextChangedListener(object : AbstractTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                password = passwordEditText.text.toString()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                loginViewModel.firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    private fun showSigned(currentUser: FirebaseUser) {
        if (currentUser.isEmailVerified) {
            findNavController().navigate(
                R.id.signedFragment,
                null,
                NavOptions.Builder().setLaunchSingleTop(true).build()
            )
        }
    }


    private fun observeViewModelLiveData() {
        loginViewModel.signIn.observe(this, ::showSigned)
        loginViewModel.errorSignIn.observe(this, ::showErrorMessage)

        loginViewModel.signUp.observe(this, ::showSigned)
        loginViewModel.errorSignUp.observe(this, ::showErrorMessage)
    }

    private fun showErrorMessage(throwable: Throwable) {
        informationTextView.visibility = View.VISIBLE
        when (throwable.javaClass) {
            FirebaseException::class.java -> {
                informationTextView.text =
                    getString(R.string.fragment_login_dialog_error_text_no_internet_connection)
            }

            FirebaseAuthInvalidUserException::class.java -> {
                informationTextView.text =
                    getString(R.string.fragment_login_dialog_error_text_user_not_registered)
            }

            FirebaseAuthInvalidCredentialsException::class.java -> {
                informationTextView.text =
                    getString(R.string.fragment_login_dialog_error_text_invalid_email_or_password)
            }
        }
    }
}