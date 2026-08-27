package com.revolutionary.codelearn.core.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface IdeTabDao {
    @Query("SELECT * FROM ide_tabs ORDER BY position ASC")
    fun observeAll(): Flow<List<IdeTabEntity>>

    @Upsert
    suspend fun upsert(entity: IdeTabEntity)

    @Query("DELETE FROM ide_tabs WHERE id = :id")
    suspend fun delete(id: String)
}
