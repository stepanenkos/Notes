package kz.stepanenkos.notes.authorization.di

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kz.stepanenkos.notes.authorization.data.DefaultAuthRepository
import kz.stepanenkos.notes.authorization.data.datasource.DefaultFirebaseAuthSource
import kz.stepanenkos.notes.authorization.data.datasource.FirebaseAuthSource
import kz.stepanenkos.notes.authorization.domain.AuthRepository
import kz.stepanenkos.notes.authorization.presentation.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

private const val REQUEST_ID_TOKEN =
    "714054521708-e36rfl0u8or951clp71sqiilohvqu5mc.apps.googleusercontent.com"

val loginModule: Module = module {
    factory<AuthRepository> {
        DefaultAuthRepository(get())
    }

    factory<FirebaseAuthSource> {
        DefaultFirebaseAuthSource(
            auth = get(),
            signInGoogleSignInClient = get(),
            userCredentialsDataSource = get()
        )
    }

    viewModel {
        LoginViewModel(
            authRepository = get(),
            userCredentialsDataSource = get()
        )
    }

    single<GoogleSignInClient> {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(REQUEST_ID_TOKEN)
            .requestEmail()
            .build()

        GoogleSignIn.getClient(androidContext(), gso)
    }
}