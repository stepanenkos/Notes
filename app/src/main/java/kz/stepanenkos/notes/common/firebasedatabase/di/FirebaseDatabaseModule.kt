package kz.stepanenkos.notes.common.firebasedatabase.di

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import kz.stepanenkos.notes.common.firebasedatabase.data.DefaultFirebaseDatabaseRepository
import kz.stepanenkos.notes.common.firebasedatabase.data.datasource.DefaultFirebaseDatabaseSource
import kz.stepanenkos.notes.common.firebasedatabase.data.datasource.FirebaseDatabaseSource
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val firebaseDatabaseModule: Module = module {
    single {
        FirebaseDatabase.getInstance()
    }

    factory<FirebaseDatabaseSource> {
        DefaultFirebaseDatabaseSource(
            firebaseDatabase = get(),
            auth = get()
        )
    }

    factory<FirebaseDatabaseRepository> {
        DefaultFirebaseDatabaseRepository(
            firebaseDatabaseSource = get()
        )
    }
}