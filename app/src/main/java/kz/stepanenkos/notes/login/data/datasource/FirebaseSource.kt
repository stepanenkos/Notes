package kz.stepanenkos.notes.login.data.datasource

import com.google.firebase.auth.FirebaseUser
import kz.stepanenkos.notes.common.model.LoginData

interface FirebaseSource {
    suspend fun signIn(email: String, password: String): LoginData<FirebaseUser?, Throwable>

    suspend fun signUp(email: String, password: String): LoginData<FirebaseUser, Throwable>

     fun forgotPassword(email: String)

    fun signOut()

    fun currentUser(): FirebaseUser?
}