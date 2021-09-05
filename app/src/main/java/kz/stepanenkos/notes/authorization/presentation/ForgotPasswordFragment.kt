package kz.stepanenkos.notes.authorization.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.databinding.FragmentForgotPasswordBinding
import org.koin.android.ext.android.inject

class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {
    private val loginViewModel: LoginViewModel by inject()
    private lateinit var emailEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var closeButton: Button
    private lateinit var informationTextView: TextView
    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentForgotPasswordBinding.bind(view)

        emailEditText = binding.fragmentForgotPasswordEditTextEmail
        sendButton = binding.fragmentForgotPasswordButtonSend
        closeButton = binding.fragmentForgotPasswordButtonClose
        informationTextView = binding.fragmentForgotPasswordTextViewInformation

        sendButton.setOnClickListener {
            if(emailEditText.text.isNotBlank() && emailEditText.text.matches(Regex("\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*\\.\\w{2,4}"))) {
                loginViewModel.forgotPassword(emailEditText.text.toString())
                informationTextView.text = getString(R.string.fragment_forgot_password_information_text_letter_send)
            } else if(emailEditText.text.isBlank()){
                informationTextView.text = getString(R.string.fragment_forgot_password_information_text_enter_email)
            } else {
                informationTextView.text = getString(R.string.fragment_forgot_password_information_text_enter_correct_email)
            }
        }

        closeButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}