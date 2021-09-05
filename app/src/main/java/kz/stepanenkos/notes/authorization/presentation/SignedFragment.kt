package kz.stepanenkos.notes.authorization.presentation

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
import kz.stepanenkos.notes.databinding.FragmentSignedBinding
import org.koin.android.ext.android.inject

class SignedFragment : DialogFragment(R.layout.fragment_signed) {
    private val loginViewModel: LoginViewModel by inject()

    private lateinit var userLoggedTextView: TextView
    private lateinit var closeButton: Button
    private lateinit var binding: FragmentSignedBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignedBinding.bind(view)

        userLoggedTextView = binding.fragmentSignedTextViewYouAreLoggedInAs
        closeButton = binding.fragmentSignedButtonClose

        val currentUser = loginViewModel.getCurrentUser()
        updateUI(currentUser)

        closeButton.setOnClickListener {
            findNavController().popBackStack()
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