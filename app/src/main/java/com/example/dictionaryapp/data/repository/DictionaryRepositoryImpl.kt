package com.example.dictionaryapp.data.repository

import android.app.Application
import com.example.dictionaryapp.R
import com.example.dictionaryapp.data.api.DictionaryApi
import com.example.dictionaryapp.data.mapper.toWordItem
import com.example.dictionaryapp.domain.model.WordItem
import com.example.dictionaryapp.domain.util.MyResult
import com.example.dictionaryapp.domain.repository.DictionaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class DictionaryRepositoryImpl @Inject constructor(
    private val dictionaryApi: DictionaryApi,
    private val application: Application
): DictionaryRepository {
    override suspend fun getWordResult(word: String): Flow<MyResult<WordItem>> {
        return flow {
            emit(MyResult.Loading(true))

            val remoteWordResultDto = try {
                dictionaryApi.getWordResult(word)
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(MyResult.Error(application.getString(R.string.failed_to_get_a_result)))
                emit(MyResult.Loading(false))
                return@flow
            } catch (e: IOException) {
                e.printStackTrace()
                emit(MyResult.Error(application.getString(R.string.failed_to_get_a_result)))
                emit(MyResult.Loading(false))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(MyResult.Error(application.getString(R.string.failed_to_get_a_result)))
                emit(MyResult.Loading(false))
                return@flow
            }

            remoteWordResultDto?.let{ wordResultDto ->
                wordResultDto[0]?.let { wordItemDto ->
                    emit(MyResult.Success(wordItemDto.toWordItem()))
                    emit(MyResult.Loading(false))
                    return@flow
                }
            }

            emit(MyResult.Error(application.getString(R.string.failed_to_get_a_result)))
            emit(MyResult.Loading(false))
        }
    }
}