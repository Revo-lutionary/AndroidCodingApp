package com.revolutionary.codelearn.core.data

import kotlinx.coroutines.flow.Flow

class IdeTabRepository(private val dao: IdeTabDao) {
    fun observeAll(): Flow<List<IdeTabEntity>> = dao.observeAll()

    suspend fun upsert(tab: IdeTabEntity) = dao.upsert(tab)

    suspend fun delete(id: String) = dao.delete(id)
}
