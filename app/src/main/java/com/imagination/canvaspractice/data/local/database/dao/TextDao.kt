package com.imagination.canvaspractice.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.imagination.canvaspractice.data.local.database.entity.TextEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Text operations
 */
@Dao
interface TextDao {
    @Query("SELECT * FROM texts WHERE boardId = :boardId")
    fun getTextsByBoardId(boardId: Long): Flow<List<TextEntity>>

    @Query("SELECT * FROM texts WHERE boardId = :boardId")
    suspend fun getTextsByBoardIdSync(boardId: Long): List<TextEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertText(text: TextEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTexts(texts: List<TextEntity>)

    @Query("DELETE FROM texts WHERE boardId = :boardId")
    suspend fun deleteTextsByBoardId(boardId: Long)

    @Query("DELETE FROM texts WHERE id = :textId")
    suspend fun deleteText(textId: String)
}
