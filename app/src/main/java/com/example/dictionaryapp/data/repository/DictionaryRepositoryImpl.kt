package com.example.dictionaryapp.data.repository

import android.app.Application
import com.example.dictionaryapp.R
import com.example.dictionaryapp.data.api.DictionaryApi
import com.example.dictionaryapp.data.mapper.toWordItem
import com.example.dictionaryapp.domain.model.WordItem
import com.example.dictionaryapp.domain.util.MyResult
import com.example.dictionaryapp.domain.repository.DictionaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class DictionaryRepositoryImpl @Inject constructor(
    private val dictionaryApi: DictionaryApi,
    private val application: Application
) : DictionaryRepository {
    override suspend fun getWordResult(word: String): Flow<MyResult<WordItem>> {
        return flow {
            val remoteWordResultDto = try {
                dictionaryApi.getWordResult(word)
            } catch (e: HttpException) {
                emitOnFail(e)
                return@flow
            } catch (e: IOException) {
                emitOnFail(e)
                return@flow
            } catch (e: Exception) {
                emitOnFail(e)
                return@flow
            }
            remoteWordResultDto?.let { wordResultDto ->
                wordResultDto[0]?.let { wordItemDto ->
                    emit(MyResult.Success(wordItemDto.toWordItem()))
                    return@flow
                }
            }
            emitOnFail()
        }
    }

    private suspend inline fun FlowCollector<MyResult<WordItem>>.emitOnFail(
        e: Exception? = null,
        errorMessage: String = application.getString(R.string.couldnt_find_this_word)
    ) {
        e?.printStackTrace()
        emit(MyResult.Error(errorMessage))
        // emit(MyResult.Loading(false))
    }

}