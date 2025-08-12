package com.work.network.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

abstract class NetworkBoundResource<ResponseType> {

    fun asFlow(): Flow<ResponseType> = flow {
        val localDataFlow = loadFromDb()
        val initialLocalData =
            localDataFlow.firstOrNull() // Get current value to decide shouldFetch

        if (shouldFetch(initialLocalData)) {
            val response = createCall()

            if (response.isSuccessful && response.body() != null) {
                saveCallResult(response.body()!!)
                emit(response.body()!!)
            } else {
                throw kotlin.Exception(response.errorBody()?.string())
            }
        } else {
            emitAll(localDataFlow)
        }
    }.flowOn(Dispatchers.IO)

    protected fun saveCallResult(item: ResponseType) {}

    protected fun shouldFetch(data: ResponseType?): Boolean = true

    protected fun loadFromDb(): Flow<ResponseType> = flowOf()

    protected abstract suspend fun createCall(): Response<ResponseType>
}