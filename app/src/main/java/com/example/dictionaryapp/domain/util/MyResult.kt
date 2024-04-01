package com.example.dictionaryapp.domain.util

sealed interface MyResult<T> {
    class Success<T>(val data: T? = null) : MyResult<T>
    class Error<T>(val message: String? = null) : MyResult<T>
    class Loading<T>(val isLoading: Boolean = true) : MyResult<T>
}

// sealed class Result<T>(
//     val data: T? = null,
//     val message: String? = null
// ) {
//     class Success<T>(data: T?) : Result<T>(data)
//     class Error<T>(message: String) : Result<T>(message = message)
//     class Loading<T>(val isLoading: Boolean = true) : Result<T>(null)
// }