package kz.stepanenkos.notes.common.di

import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kz.stepanenkos.notes.common.data.DefaultDatabaseRepository
import kz.stepanenkos.notes.common.roomdatabase.NotesDatabase
import kz.stepanenkos.notes.common.domain.DatabaseRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

private const val DATABASE_NAME = "notes-database"

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
}