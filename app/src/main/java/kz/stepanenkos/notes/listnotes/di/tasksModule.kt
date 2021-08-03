package kz.stepanenkos.notes.listnotes.di

import kz.stepanenkos.notes.listnotes.presentation.NotesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val notesModule: Module = module {
    viewModel {
        NotesViewModel(
            firebaseDatabaseRepository = get(),
            userCredentialsDataSource = get(),
        )
    }
}