package kz.stepanenkos.notes.user.data.datasource

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseUser

private const val USER_UID = "user_uid"
private const val USER_EMAIL = "user_email"
private const val USER_DISPLAY_NAME = "user_display_name"
private const val USER_PHOTO_URL = "user_photo_url"
private const val USER_IS_LOGGED_IN = "user_is_logged_in"
private const val USER_IS_EMAIL_VERIFIED = "user_is_email_verified"
private const val LAST_USER_EMAIL = "last_user_email"

private const val DEFAULT_EMPTY_VALUE = ""
private const val DEFAULT_FOR_EMPTY_BOOLEAN = false

class UserCredentialsSharedPrefsDataSource(
    private val sharedPreferences: SharedPreferences
): UserCredentialsDataSource {
    override fun saveUserCredentials(firebaseUser: FirebaseUser?) {
        if(firebaseUser != null) {
            sharedPreferences.edit {
                putString(USER_UID, firebaseUser.uid)
                putString(USER_EMAIL, firebaseUser.email)
                putString(USER_DISPLAY_NAME, firebaseUser.displayName)
                putString(USER_PHOTO_URL, firebaseUser.email)
                putBoolean(USER_IS_LOGGED_IN, true)
                putBoolean(USER_IS_EMAIL_VERIFIED, firebaseUser.isEmailVerified)
            }
        }
    }

    override fun getUid(): String {
        return sharedPreferences.getString(USER_UID, DEFAULT_EMPTY_VALUE)!!
    }

    override fun getEmail(): String {
        return sharedPreferences.getString(USER_EMAIL, DEFAULT_EMPTY_VALUE)!!
    }

    override fun getDisplayName(): String {
        return sharedPreferences.getString(USER_DISPLAY_NAME, DEFAULT_EMPTY_VALUE)!!
    }

    override fun getPhotoUrl(): String {
        return sharedPreferences.getString(USER_PHOTO_URL, DEFAULT_EMPTY_VALUE)!!
    }

    override fun isEmailVerified(): Boolean {
        return sharedPreferences.getBoolean(USER_IS_EMAIL_VERIFIED, DEFAULT_FOR_EMPTY_BOOLEAN)
    }

    override fun setLastUserEmail(lastEmail: String) {
        sharedPreferences.edit {
            putString(LAST_USER_EMAIL, lastEmail)
        }
    }

    override fun getLastUserEmail(): String {
        return sharedPreferences.getString(LAST_USER_EMAIL, DEFAULT_EMPTY_VALUE)!!
    }


}