package kz.stepanenkos.notes.authorization.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import kz.stepanenkos.notes.R
import org.koin.android.ext.android.inject

class ForgotPasswordFragment : DialogFragment() {
    private val loginViewModel: LoginViewModel by inject()
    private lateinit var emailEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var closeButton: Button
    private lateinit var informationTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)
        emailEditText = view.findViewById(R.id.fragment_forgot_password_edit_text_email)
        sendButton = view.findViewById(R.id.fragment_forgot_password_button_send)
        closeButton = view.findViewById(R.id.fragment_forgot_password_button_close)
        informationTextView = view.findViewById(R.id.fragment_forgot_password_text_view_information)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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