package kz.stepanenkos.notes.authorization.data.datasource

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import kz.stepanenkos.notes.common.model.LoginData
import kz.stepanenkos.notes.user.data.datasource.UserCredentialsDataSource

private const val RC_SIGN_IN = 9001

class DefaultFirebaseAuthSource(
    private val auth: FirebaseAuth,
    private val signInGoogleSignInClient: GoogleSignInClient,
    private val userCredentialsDataSource: UserCredentialsDataSource
) : FirebaseAuthSource {
    override suspend fun signIn(
        email: String,
        password: String
    ): LoginData<FirebaseUser?, Throwable> {
        auth.signInWithEmailAndPassword(email, password).await()
        return if (auth.currentUser != null) {
            userCredentialsDataSource.saveUserCredentials(auth.currentUser)

            if (auth.currentUser!!.email != userCredentialsDataSource.getEmail()) {
                userCredentialsDataSource.setLastUserEmail(userCredentialsDataSource.getEmail())
            }
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
        if (auth.currentUser != null) {
            auth.currentUser!!.sendEmailVerification()
        }
        return if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            userCredentialsDataSource.saveUserCredentials(auth.currentUser!!)
            if (auth.currentUser!!.email != userCredentialsDataSource.getEmail()) {
                userCredentialsDataSource.setLastUserEmail(userCredentialsDataSource.getEmail())
            }

            LoginData.Success(auth.currentUser!!)
        } else {
            LoginData.Error(FirebaseAuthException(" ", " "))
        }

    }

    override fun forgotPassword(email: String) {
        auth.sendPasswordResetEmail(email)
    }

    override fun signOut() {
        userCredentialsDataSource.setLastUserEmail(userCredentialsDataSource.getEmail())
        auth.signOut()
    }

    override fun currentUser(): FirebaseUser? = auth.currentUser

    override fun signOutGoogle() {
        signInGoogleSignInClient.signOut()
    }

    override suspend fun firebaseAuthWithGoogle(token: String): LoginData<FirebaseUser?, Throwable> {
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credential).await()
        return if (auth.currentUser != null) {
                userCredentialsDataSource.saveUserCredentials(auth.currentUser)
                if (userCredentialsDataSource.getEmail() != auth.currentUser?.email) {
                    auth.currentUser?.email?.let { userCredentialsDataSource.setLastUserEmail(it) }
                }
                LoginData.Success(auth.currentUser!!)
            } else {
                LoginData.Error(FirebaseAuthException(" ", " "))
            }

    }
}

