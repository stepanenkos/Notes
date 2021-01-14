package kz.stepanenkos.notes.login.di

import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.login.data.datasource.DefaultFirebaseSource
import kz.stepanenkos.notes.login.data.datasource.FirebaseSource
import kz.stepanenkos.notes.login.data.DefaultLoginRepository
import kz.stepanenkos.notes.login.domain.LoginRepository
import kz.stepanenkos.notes.login.presentation.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
private const val REQUEST_ID_TOKEN = "714054521708-vv77ccoadld79eh0n3f5o74nknas7qse.apps.googleusercontent.com"
val loginModule: Module = module {
    factory<LoginRepository> {
        DefaultLoginRepository(get())
    }

    factory<FirebaseSource> {
        DefaultFirebaseSource(get())
    }

    viewModel {
        LoginViewModel(get())
    }

    single<GoogleSignInClient>{
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(REQUEST_ID_TOKEN)
            .requestEmail()
            .build()

        GoogleSignIn.getClient(androidContext(), gso)
    }
}