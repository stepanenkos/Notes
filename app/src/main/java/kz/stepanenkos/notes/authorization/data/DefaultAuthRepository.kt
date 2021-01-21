package kz.stepanenkos.notes.authorization.data

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kz.stepanenkos.notes.authorization.data.datasource.FirebaseAuthSource
import kz.stepanenkos.notes.authorization.domain.AuthRepository
import kz.stepanenkos.notes.common.model.LoginData

class DefaultAuthRepository(
    private val firebaseAuthSource: FirebaseAuthSource
) : AuthRepository {

    override suspend fun signIn(
        email: String,
        password: String
    ): LoginData<FirebaseUser?, Throwable> {
        return try {
            firebaseAuthSource.signIn(email, password)
        } catch (e: FirebaseAuthException) {
            LoginData.Error(e)
        } catch (e: FirebaseException) {
            LoginData.Error(e)
        }
    }

    override suspend fun signUp(
        email: String,
        password: String
    ): LoginData<FirebaseUser, Throwable> {
        return try {
            firebaseAuthSource.signUp(email, password)
        } catch (e: FirebaseAuthException) {
            LoginData.Error(e)
        } catch (e: FirebaseException) {
            LoginData.Error(e)
        }
    }

    override fun forgotPassword(email: String) {
        firebaseAuthSource.forgotPassword(email)
    }

    override fun signOut() {
        firebaseAuthSource.signOut()
    }

    override fun currentUser(): FirebaseUser? = firebaseAuthSource.currentUser()
}