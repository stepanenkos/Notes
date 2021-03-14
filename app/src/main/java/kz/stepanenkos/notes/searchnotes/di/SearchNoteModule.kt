package kz.stepanenkos.notes.searchnotes.di

import kz.stepanenkos.notes.searchnotes.presentation.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val searchNoteModule: Module = module {
    viewModel {
        SearchViewModel(
            firebaseDatabaseRepository = get()
        )
    }
}