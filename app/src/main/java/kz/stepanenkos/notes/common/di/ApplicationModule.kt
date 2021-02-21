package kz.stepanenkos.notes.common.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

private const val APPLICATION_SHARED_PREFS = "application_shared_prefs"

val applicationModule: Module = module {

    single {
        Firebase.auth
    }

    single {
        Firebase.firestore
    }

    single {
        androidContext().getSharedPreferences(APPLICATION_SHARED_PREFS, Context.MODE_PRIVATE)
    }
}