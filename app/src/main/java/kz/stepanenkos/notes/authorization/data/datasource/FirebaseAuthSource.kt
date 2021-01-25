package kz.stepanenkos.notes.authorization.data.datasource

import com.google.firebase.auth.FirebaseUser
import kz.stepanenkos.notes.common.model.LoginData

interface FirebaseAuthSource {
    suspend fun signIn(email: String, password: String): LoginData<FirebaseUser?, Throwable>
    suspend fun signUp(email: String, password: String): LoginData<FirebaseUser, Throwable>
    suspend fun firebaseAuthWithGoogle(token: String): LoginData<FirebaseUser?, Throwable>

    fun forgotPassword(email: String)
    fun signOut()
    fun currentUser(): FirebaseUser?
    fun signOutGoogle()
}