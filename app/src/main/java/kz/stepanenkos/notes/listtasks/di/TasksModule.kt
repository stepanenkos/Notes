package kz.stepanenkos.notes.listtasks.di

import kz.stepanenkos.notes.listtasks.presentation.TasksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val tasksModule: Module = module {
    viewModel {
        TasksViewModel(
            firebaseDatabaseRepository = get(),
            userCredentialsDataSource = get(),
        )
    }
}