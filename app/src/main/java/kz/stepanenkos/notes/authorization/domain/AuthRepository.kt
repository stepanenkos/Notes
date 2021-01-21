package kz.stepanenkos.notes.authorization.domain

import com.google.firebase.auth.FirebaseUser
import kz.stepanenkos.notes.common.model.LoginData

interface AuthRepository {
    suspend fun signIn(email: String, password: String): LoginData<FirebaseUser?, Throwable>
    suspend fun signUp(email: String, password: String): LoginData<FirebaseUser, Throwable>
    fun signOut()
    fun forgotPassword(email: String)
    fun currentUser(): FirebaseUser?


}