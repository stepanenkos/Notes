package kz.stepanenkos.notes

import android.app.Application
import com.google.firebase.FirebaseApp
import com.jakewharton.threetenabp.AndroidThreeTen
import kz.stepanenkos.notes.editor.di.addNoteModule
import kz.stepanenkos.notes.common.di.applicationModule
import kz.stepanenkos.notes.listnotes.di.notesModule
import kz.stepanenkos.notes.login.di.loginModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        FirebaseApp.initializeApp(this)
        startKoin{
            androidContext(this@NotesApplication)
            modules(
                applicationModule,
                addNoteModule,
                notesModule,
                loginModule,
            )
        }
    }
}