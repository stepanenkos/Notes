package kz.stepanenkos.notes

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.jakewharton.threetenabp.AndroidThreeTen
import io.github.inflationx.viewpump.ViewPump
import kz.stepanenkos.TextSizeUpdatingInterceptor
import kz.stepanenkos.notes.editor.di.addNoteModule
import kz.stepanenkos.notes.common.di.applicationModule
import kz.stepanenkos.notes.listnotes.di.notesModule
import kz.stepanenkos.notes.authorization.di.loginModule
import kz.stepanenkos.notes.common.firebasedatabase.di.firebaseDatabaseModule
import kz.stepanenkos.notes.user.di.userCredentialsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        ViewPump.init(ViewPump.builder()
            .addInterceptor(TextSizeUpdatingInterceptor())
            .build())
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode())
        startKoin{
            androidContext(this@NotesApplication)
            modules(
                applicationModule,
                addNoteModule,
                notesModule,
                loginModule,
                firebaseDatabaseModule,
                userCredentialsModule,
            )
        }
    }
}