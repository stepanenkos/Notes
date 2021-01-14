package kz.stepanenkos.notes.login.presentation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.presentation.AbstractTextWatcher
import org.koin.android.ext.android.inject


class LoginDialogFragment : DialogFragment() {
    private val loginViewModel: LoginViewModel by inject()

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private var currentUser: FirebaseUser? = null

    private var email: String = ""
    private var password: String = ""
    private lateinit var emailTextView: TextView
    private lateinit var emailEditText: EditText
    private lateinit var passwordTextView: TextView
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var signInButton: Button
    private lateinit var closeButton: Button
    private lateinit var googleSignInButton: SignInButton
    private lateinit var informationTextView: TextView
    private lateinit var enterAsTextView: TextView
    private lateinit var signOutButton: Button
    private lateinit var forgetPasswordTextViewButton: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        emailTextView = view.findViewById(R.id.fragment_login_text_view_email)
        emailEditText = view.findViewById(R.id.fragment_login_edit_text_email)
        passwordTextView = view.findViewById(R.id.fragment_login_text_view_password)
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
        forgetPasswordTextViewButton = view.findViewById(R.id.fragment_login_text_view_forgot_password)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
        auth = Firebase.auth
        googleSignInClient = GoogleSignIn.getClient(requireContext(), getGSO())
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
        } else {
            showUnSigned()
        }

    }

    private fun setListeners() {

        signInButton.setOnClickListener {
            if (email.isNotBlank() && password.isNotBlank()) {
                loginViewModel.signIn(email, password)
            } else {
                informationTextView.visibility = View.VISIBLE
                informationTextView.text = getString(R.string.fragment_login_dialog_information_text_all_fields_must_be_filled)
            }
        }

        signUpButton.setOnClickListener {
            if (email.isNotBlank() && password.isNotBlank()) {
                loginViewModel.signUp(email, password)
            } else {
                informationTextView.visibility = View.VISIBLE
                informationTextView.text = getString(R.string.fragment_login_dialog_information_text_all_fields_must_be_filled)
            }
        }

        closeButton.setOnClickListener {
            dismiss()
        }

        signOutButton.setOnClickListener {
            auth.signOut()
            googleSignInClient.signOut()
            currentUser = null
            showUnSigned()
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
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getGSO(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.fragment_login_dialog_request_id_token_for_google_sign_in))
            .requestEmail()
            .build()
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = loginViewModel.getCurrentUser()
                    updateUI(user)
                }
            }
    }

    private fun showSigned(currentUser: FirebaseUser) {
        if (currentUser.isEmailVerified) {
            findNavController().navigate(R.id.signedFragment)
        }
    }

    private fun showUnSigned() {
        emailTextView.visibility = View.VISIBLE
        emailEditText.visibility = View.VISIBLE
        passwordTextView.visibility = View.VISIBLE
        passwordEditText.visibility = View.VISIBLE
        signUpButton.visibility = View.VISIBLE
        signInButton.visibility = View.VISIBLE
        googleSignInButton.visibility = View.VISIBLE
        informationTextView.visibility = View.VISIBLE
        signOutButton.visibility = View.GONE
        enterAsTextView.visibility = View.GONE
        forgetPasswordTextViewButton.visibility = View.VISIBLE
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
                informationTextView.text = getString(R.string.fragment_login_dialog_error_text_no_internet_connection)
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



