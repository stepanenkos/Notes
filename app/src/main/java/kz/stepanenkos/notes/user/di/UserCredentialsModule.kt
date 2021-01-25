package kz.stepanenkos.notes.user.di

import kz.stepanenkos.notes.user.data.datasource.UserCredentialsSharedPrefsDataSource
import kz.stepanenkos.notes.user.data.datasource.UserCredentialsDataSource
import org.koin.core.module.Module
import org.koin.dsl.module

val userCredentialsModule: Module = module {
    single<UserCredentialsDataSource> {
        UserCredentialsSharedPrefsDataSource(
            sharedPreferences = get()
        )
    }
}