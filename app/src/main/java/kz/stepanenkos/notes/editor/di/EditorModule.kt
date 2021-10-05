package kz.stepanenkos.notes.editor.di

import kz.stepanenkos.notes.editor.presentation.EditorViewModel
import kz.stepanenkos.notes.notification.NotificationAlarmHelper
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val addNoteModule: Module = module {

    viewModel {
        EditorViewModel(
            firebaseDatabaseRepository = get(),
            notificationAlarmHelper = get()
        )
    }

    single {
        NotificationAlarmHelper(get())
    }

}