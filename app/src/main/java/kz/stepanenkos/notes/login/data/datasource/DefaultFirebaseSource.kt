package kz.stepanenkos.notes.login.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import kz.stepanenkos.notes.common.model.LoginData

class DefaultFirebaseSource(
    private val auth: FirebaseAuth
) : FirebaseSource {
    override suspend fun signIn(email: String, password: String): LoginData<FirebaseUser?, Throwable> {
        auth.signInWithEmailAndPassword(email, password).await()
        return if (auth.currentUser != null) {
            LoginData.Success(auth.currentUser)
        } else {
            LoginData.Error(FirebaseAuthException("", ""))
        }
    }

    override suspend fun signUp(
        email: String,
        password: String
    ): LoginData<FirebaseUser, Throwable> {
        auth.createUserWithEmailAndPassword(email, password).await()
        return if (auth.currentUser != null) {
            val user = auth.currentUser!!
            user.sendEmailVerification()
            LoginData.Success(user)
        } else {
            LoginData.Error(FirebaseAuthException("", ""))
        }

    }

    override fun forgotPassword(email: String) {
        auth.sendPasswordResetEmail(email)
    }

    override fun signOut() = auth.signOut()


    override fun currentUser(): FirebaseUser? = auth.currentUser
}