package kz.stepanenkos.notes.login.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kz.stepanenkos.notes.common.model.LoginData
import kz.stepanenkos.notes.login.domain.LoginRepository

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {
    private val _signIn: MutableLiveData<FirebaseUser> = MutableLiveData()
    private val _errorSignIn: MutableLiveData<Throwable> = MutableLiveData()
    private val _signUp: MutableLiveData<FirebaseUser> = MutableLiveData()
    private val _errorSignUp: MutableLiveData<Throwable> = MutableLiveData()

    val signIn: LiveData<FirebaseUser> = _signIn
    val errorSignIn: LiveData<Throwable> = _errorSignIn
    val signUp: LiveData<FirebaseUser> = _signUp
    val errorSignUp: LiveData<Throwable> = _errorSignUp

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            when (val signInData = loginRepository.signIn(email, password)) {
                is LoginData.Success -> {
                    _signIn.postValue(signInData.result)
                }

                is LoginData.Error -> {
                    _errorSignIn.postValue(signInData.error)
                }
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            when (val signUpData = loginRepository.signUp(email, password)) {
                is LoginData.Success -> {
                    _signUp.postValue(signUpData.result)
                }

                is LoginData.Error -> {
                    _errorSignUp.postValue(signUpData.error)
                }
            }
        }
    }

    fun forgotPassword(email: String) {
        loginRepository.forgotPassword(email)
    }

    fun getCurrentUser(): FirebaseUser? {
        return loginRepository.currentUser()
    }

    fun signOut() {
        loginRepository.signOut()
    }
}