package kz.stepanenkos.notes.login.data

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kz.stepanenkos.notes.login.data.datasource.FirebaseSource
import kz.stepanenkos.notes.common.model.LoginData
import kz.stepanenkos.notes.login.domain.LoginRepository

class DefaultLoginRepository(
    private val firebaseSource: FirebaseSource
) : LoginRepository {

    override suspend fun signIn(
        email: String,
        password: String
    ): LoginData<FirebaseUser?, Throwable> {
        return try {
            firebaseSource.signIn(email, password)
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
            firebaseSource.signUp(email, password)
        } catch (e: FirebaseAuthException) {
            LoginData.Error(e)
        } catch (e: FirebaseException) {
            LoginData.Error(e)
        }
    }

    override fun forgotPassword(email: String) {
        firebaseSource.forgotPassword(email)
    }

    override fun signOut() {
        firebaseSource.signOut()
    }

    override fun currentUser(): FirebaseUser? = firebaseSource.currentUser()

}