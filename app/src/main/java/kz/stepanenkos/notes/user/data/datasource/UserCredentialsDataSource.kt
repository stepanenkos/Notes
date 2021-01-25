package kz.stepanenkos.notes.user.data.datasource

import com.google.firebase.auth.FirebaseUser

interface UserCredentialsDataSource {
    fun saveUserCredentials(firebaseUser: FirebaseUser?)
    fun getUid(): String
    fun getEmail(): String
    fun getDisplayName(): String
    fun getPhotoUrl(): String
    fun isEmailVerified(): Boolean
    fun setLastUserEmail(lastEmail: String)
    fun getLastUserEmail(): String
}