package kz.stepanenkos.notes

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.jakewharton.threetenabp.AndroidThreeTen
import io.github.inflationx.viewpump.ViewPump
import kz.stepanenkos.notes.authorization.di.loginModule
import kz.stepanenkos.notes.common.di.applicationModule
import kz.stepanenkos.notes.common.firebasedatabase.di.firebaseDatabaseModule
import kz.stepanenkos.notes.editor.di.addNoteModule
import kz.stepanenkos.notes.listnotes.di.notesModule
import kz.stepanenkos.notes.listtasks.di.tasksModule
import kz.stepanenkos.notes.notification.NotificationHelper
import kz.stepanenkos.notes.searchnotes.di.searchNoteModule
import kz.stepanenkos.notes.user.di.userCredentialsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        NotificationHelper.init(this)
        ViewPump.init(ViewPump.builder()
            .addInterceptor(TextSizeUpdatingInterceptor())
            .build())
        /*AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)*/
        startKoin{
            androidContext(this@NotesApplication)
            modules(
                applicationModule,
                addNoteModule,
                notesModule,
                tasksModule,
                loginModule,
                firebaseDatabaseModule,
                userCredentialsModule,
                searchNoteModule,
            )
        }
    }
}