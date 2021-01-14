package kz.stepanenkos.notes.login.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import kz.stepanenkos.notes.R
import org.koin.android.ext.android.inject

class SignedFragment : DialogFragment() {
    private val loginViewModel: LoginViewModel by inject()

    private lateinit var userLoggedTextView: TextView
    private lateinit var signOutButton: Button
    private lateinit var closeButton: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signed, container, false)
        userLoggedTextView = view.findViewById(R.id.fragment_signed_text_view_you_are_logged_in_as)
        signOutButton = view.findViewById(R.id.fragment_signed_button_sign_out)
        closeButton = view.findViewById(R.id.fragment_signed_button_close)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = loginViewModel.getCurrentUser()
        updateUI(currentUser)
        signOutButton.setOnClickListener {
            loginViewModel.signOut()
            findNavController().popBackStack()
        }

        closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            userLoggedTextView.text = getString(
                R.string.fragment_login_dialog_information_text_you_are_logged_in_as,
                currentUser.displayName ?: "",
                currentUser.email
            )
        }
    }
}