package kz.stepanenkos.notes.common.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kz.stepanenkos.notes.common.roomdatabase.data.DefaultDatabaseRepository
import kz.stepanenkos.notes.common.roomdatabase.domain.DatabaseRepository
import kz.stepanenkos.notes.common.roomdatabase.NotesDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

private const val DATABASE_NAME = "notes-database"
private const val APPLICATION_SHARED_PREFS = "application_shared_prefs"

val applicationModule: Module = module {
    single {
        val database = Room.databaseBuilder(
            androidContext(),
            NotesDatabase::class.java,
            DATABASE_NAME
        ).build()

        database.notesDao()
    }

    single {
        Firebase.auth
    }

    factory<DatabaseRepository> {
        DefaultDatabaseRepository(get())
    }

    single {
        androidContext().getSharedPreferences(APPLICATION_SHARED_PREFS, Context.MODE_PRIVATE)
    }
}