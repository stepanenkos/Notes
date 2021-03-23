package kz.stepanenkos.notes.authorization.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.presentation.AbstractTextWatcher
import org.koin.android.ext.android.inject

private const val RC_SIGN_IN = 9001

class LoginFragment : Fragment() {
    private val loginViewModel: LoginViewModel by inject()
    private val googleSignInClient: GoogleSignInClient by inject()
    private val auth: FirebaseAuth by inject()
    private var currentUser: FirebaseUser? = null
    private var email: String = ""
    private var password: String = ""

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var signUpButton: Button
    private lateinit var signInButton: Button
    private lateinit var googleSignInButton: SignInButton
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
        googleSignInButton = view.findViewById(R.id.fragment_login_button_google_login)
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            googleSignInButton.setColorScheme(SignInButton.COLOR_DARK)
        } else {
            googleSignInButton.setColorScheme(SignInButton.COLOR_LIGHT)
        }
        forgetPasswordTextViewButton =
            view.findViewById(R.id.fragment_login_text_view_forgot_password)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModelLiveData()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
        currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            findNavController().popBackStack()
        }
    }

    private fun setListeners() {
        signInButton.setOnClickListener {
            hideKeyboardFrom(requireContext(), requireView())

                if (isValidCredentials(email, password)) {
                    loginViewModel.signIn(email, password)
                } else {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.fragment_login_dialog_information_text_all_fields_must_be_filled),
                        Snackbar.LENGTH_LONG
                    ).show()
                }

        }

        signUpButton.setOnClickListener {
            hideKeyboardFrom(requireContext(), requireView())
            if (isValidCredentials(email, password)) {
                loginViewModel.signUp(email, password)
                if (auth.currentUser != null && !auth.currentUser?.isEmailVerified!!) {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.fragment_forgot_password_information_text_letter_send),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {
                Snackbar.make(
                    requireView(),
                    getString(R.string.fragment_login_dialog_information_text_all_fields_must_be_filled),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        forgetPasswordTextViewButton.setOnClickListener {

            findNavController().navigate(
                R.id.forgotPasswordFragment,
                null,
                NavOptions.Builder().setLaunchSingleTop(true).build()
            )
        }

        googleSignInButton.setOnClickListener {
            signInWithGoogle()
            if (currentUser != null) {
                findNavController().popBackStack()
            }
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
                Snackbar.make(
                    requireView(),
                    e.toString(),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    private fun observeViewModelLiveData() {
        loginViewModel.signIn.observe(this, ::updateUI)
        loginViewModel.errorSignIn.observe(this, ::showErrorMessage)

        loginViewModel.signUp.observe(this, ::updateUI)
        loginViewModel.errorSignUp.observe(this, ::showErrorMessage)
    }

    private fun showErrorMessage(throwable: Throwable) {
        when (throwable.javaClass) {


            FirebaseAuthInvalidUserException::class.java -> {
                Snackbar.make(
                    requireView(),
                    getString(R.string.fragment_login_dialog_error_text_user_not_registered),
                    Snackbar.LENGTH_LONG
                ).show()
            }

            FirebaseAuthInvalidCredentialsException::class.java -> {
                Snackbar.make(
                    requireView(),
                    //throwable.localizedMessage,
                    getString(R.string.fragment_login_dialog_error_text_invalid_email_or_password),
                    Snackbar.LENGTH_LONG
                ).show()
            }

            FirebaseException::class.java -> {
                Snackbar.make(
                    requireView(),
                    getString(R.string.fragment_login_dialog_error_text_no_internet_connection),
                    Snackbar.LENGTH_LONG
                ).show()
            }

            FirebaseAuthUserCollisionException::class.java -> {
                Snackbar.make(
                    requireView(),
                    "Пользователь с таким адресом электронной почты уже зарегистрирован",
                    Snackbar.LENGTH_LONG
                ).show()
            }

            FirebaseAuthWeakPasswordException::class.java -> {
                Snackbar.make(
                    requireView(),
                    "Пароль должен быть не менее 6 символов",
                    Snackbar.LENGTH_LONG
                ).show()
            }

            FirebaseTooManyRequestsException::class.java -> {
                Snackbar.make(
                    requireView(),
                    "Были много раз введены не корректные данные. На данный момент доступ заблокирован. Попробуйте позже или если вы забыли пароль, воспользуйтесь кнопкой \"Забыли пароль\"",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun isValidCredentials(email: String, password: String): Boolean {
        return email.matches(Regex("\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*\\.\\w{2,4}")) && password.isNotBlank()
    }

    private fun hideKeyboardFrom(context: Context, view: View?) {
        val imm =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}