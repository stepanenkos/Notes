package kz.stepanenkos.notes.common.model

sealed class LoginData<out Result, out Error> {
    data class Success<Result>(
        val result: Result
    ) : LoginData<Result, Nothing>()

    data class Error<Error>(
        val error: Error
    ) : LoginData<Nothing, Error>()
}